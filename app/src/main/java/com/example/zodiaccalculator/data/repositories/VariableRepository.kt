package com.example.zodiaccalculator.data.repositories

import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.data.models.Variable
import com.example.zodiaccalculator.data.utils.ExpressionEvaluator

object VariableRepository {
    private val variables = mutableMapOf<String, Variable>()
    private val evaluator = ExpressionEvaluator()
    private var isEvaluating = false

    // ========== BASIC GETTERS ==========

    fun getVariables(): List<Variable> = variables.values.toList()

    fun getVariable(id: String): Variable? = variables[id]

    // ========== SAVE / UPDATE ==========

    fun saveVariable(id: String?, name: String, expression: String): Variable {
        val existingId = id ?: java.util.UUID.randomUUID().toString()
        val variable = Variable(existingId, name, expression)
        variables[existingId] = variable
        evaluateVariableSafely(existingId)
        propagateUpdates(existingId)
        return variable
    }

    fun updateVariableExpression(id: String, newExpression: String): Boolean {
        val variable = variables[id]
        return if (variable != null) {
            variable.expression = newExpression
            evaluateVariableSafely(id)
            propagateUpdates(id)
            true
        } else {
            false
        }
    }

    fun updateVariableName(id: String, newName: String): Boolean {
        val variable = variables[id]
        return if (variable != null) {
            variable.name = newName
            true
        } else {
            false
        }
    }

    // ========== DELETE ==========

    fun deleteVariable(id: String): Boolean {
        // Check if other variables depend on this one
        val dependents = findDependents(id)
//        if (dependents.isNotEmpty()) {
//            throw IllegalStateException("Cannot delete '${variables[id]?.name}': ${dependents.size} other variable(s) depend on it")
//        }
        return variables.remove(id) != null
    }

    // ========== EVALUATION ==========

    private fun evaluateVariableSafely(variableId: String) {
        if (isEvaluating) return

        isEvaluating = true
        try {
            val variable = variables[variableId]
            if (variable != null) {
                val variableMap = buildVariableMap()

                // Try numeric evaluation
                val result = evaluator.evaluate(variable.expression, variableMap)

                if (result != null) {
                    variable.value = result
                    variable.symbolicValue = result.toString()
                    variable.isValid = true
                    variable.hasUndefinedVariables = false
                } else {
                    variable.value = null
                    variable.isValid = false
                    // Store symbolic representation
                    variable.symbolicValue = evaluator.getSymbolicExpression(variable.expression, variableMap)
                    variable.hasUndefinedVariables = evaluator.getUndefinedVariables(
                        variable.expression,
                        variables.values.map { it.name }.toSet()
                    ).isNotEmpty()
                }
            }
        } finally {
            isEvaluating = false
        }
    }

    private fun buildVariableMap(): Map<String, Double> {
        return variables.values.associate { it.name to (it.value ?: 0.0) }
    }

    private fun propagateUpdates(changedVariableId: String) {
        val dependents = findDependents(changedVariableId)
        dependents.forEach { dependentId ->
            evaluateVariableSafely(dependentId)
        }
    }

    private fun findDependents(variableId: String): List<String> {
        val variable = variables[variableId] ?: return emptyList()
        val dependents = mutableListOf<String>()

        variables.values.forEach { candidate ->
            if (candidate.id != variableId && candidate.expression.contains(variable.name)) {
                dependents.add(candidate.id)
            }
        }

        return dependents
    }

    fun getCurrentVariableValues(): Map<String, Double> {
        val sortedVariables = topologicalSort()
        sortedVariables.forEach { variableId ->
            evaluateVariableSafely(variableId)
        }

        return variables.values.associate { it.name to (it.value ?: 0.0) }
    }

    private fun topologicalSort(): List<String> {
        val result = mutableListOf<String>()
        val visited = mutableSetOf<String>()
        val temp = mutableSetOf<String>()

        fun visit(variableId: String) {
            if (temp.contains(variableId)) return
            if (visited.contains(variableId)) return

            temp.add(variableId)

            val variable = variables[variableId]
            if (variable != null) {
                variables.values.forEach { other ->
                    if (other.id != variableId && variable.expression.contains(other.name)) {
                        visit(other.id)
                    }
                }
            }

            temp.remove(variableId)
            visited.add(variableId)
            result.add(variableId)
        }

        variables.keys.forEach { visit(it) }
        return result
    }

    // ========== INITIAL DEMO DATA ==========

    fun loadFromCalculation(calculation: Calculation?) {
        clearAllVariables()
        if (calculation != null) {
            calculation.variables.forEach { variable ->
                saveVariable(variable.id, variable.name, variable.expression)
            }
        } else {
            // Only create default data if no calculation exists
            createDefaultVariables()
        }
    }

    private fun createDefaultVariables() {
        saveVariable(null, "x", "5")
        saveVariable(null, "y", "3")
        saveVariable(null, "sum", "x + y")
    }
    // Add to VariableRepository.kt

    fun getSymbolicValue(variableId: String): String {
        val variable = variables[variableId] ?: return "?"

        // Get current values of all variables
        val currentValues = getCurrentVariableValues()

        // Try to evaluate fully first
        val numericResult = evaluator.evaluate(variable.expression, currentValues)

        return if (numericResult != null) {
            numericResult.toString()  // Fully evaluable - show number
        } else {
            // Get symbolic representation
            evaluator.getSymbolicExpression(variable.expression, currentValues)
        }
    }

    fun hasUndefinedVariables(variableId: String): Boolean {
        val variable = variables[variableId] ?: return true
        val definedVariables = variables.values.map { it.name }.toSet()
        val undefined = evaluator.getUndefinedVariables(variable.expression, definedVariables)
        return undefined.isNotEmpty()
    }
    fun getSymbolicExpression(variableId: String): String {
        val variable = variables[variableId] ?: return "?"
        val currentValues = getCurrentVariableValues()
        return evaluator.getSymbolicExpression(variable.expression, currentValues)
    }

    fun getUndefinedVariables(variableId: String): List<String> {
        val variable = variables[variableId] ?: return emptyList()
        val definedVariables = variables.values.map { it.name }.toSet()
        return evaluator.getUndefinedVariables(variable.expression, definedVariables)
    }

    fun isFullyEvaluated(variableId: String): Boolean {
        val variable = variables[variableId] ?: return false
        val currentValues = getCurrentVariableValues()
        return evaluator.evaluate(variable.expression, currentValues) != null
    }
    fun clearAllVariables() {
        variables.clear()
    }

    fun loadVariables(variablesList: List<Variable>) {
        clearAllVariables()
        variablesList.forEach { variable ->
            saveVariable(variable.id, variable.name, variable.expression)
        }
    }
}