package com.example.zodiaccalculator.data.repositories

import com.example.zodiaccalculator.data.models.Equation
import com.example.zodiaccalculator.data.models.Variable
import com.example.zodiaccalculator.data.models.VariableType
import com.example.zodiaccalculator.data.utils.ExpressionEvaluator

object EquationRepository {
    private val equations = mutableMapOf<String, Equation>()
    private val variables = mutableMapOf<String, Variable>()
    private val dependencyGraph = mutableMapOf<String, MutableSet<String>>()
    private val reverseDependencies = mutableMapOf<String, MutableSet<String>>()

    private val evaluator = ExpressionEvaluator()

    // Cache for current values to avoid recalculating
    private var cachedVariableValues: Map<String, Double>? = null
    private var cacheValid = false

    // ========== VARIABLE GETTERS ==========

    fun getVariables(): List<Variable> = variables.values.toList()
    fun getComputedVariables(): List<Variable> = variables.values.filter { it.type == VariableType.COMPUTED }
    fun getConstantVariables(): List<Variable> = variables.values.filter { it.type == VariableType.CONSTANT }
    fun getVariable(id: String): Variable? = variables[id]

    // ========== VARIABLE SAVE/UPDATE ==========

    fun saveVariable(id: String?, name: String, type: VariableType, value: Double?, expression: String?): Variable {
        val existingId = id ?: java.util.UUID.randomUUID().toString()

        val variable = if (type == VariableType.CONSTANT) {
            Variable(existingId, name, type, value ?: 0.0, null, true)
        } else {
            Variable(existingId, name, type, null, expression ?: "", true)
        }

        variables[existingId] = variable
        invalidateCache()
        updateDependencies(existingId)

        if (type == VariableType.COMPUTED) {
            evaluateComputedVariable(existingId)
        }

        return variable
    }

    fun updateConstantValue(id: String, newValue: Double): Boolean {
        val variable = variables[id]
        return if (variable?.type == VariableType.CONSTANT) {
            variable.value = newValue
            invalidateCache()
            propagateUpdates(id)
            true
        } else {
            false
        }
    }

    fun updateComputedExpression(id: String, newExpression: String): Boolean {
        val variable = variables[id]
        return if (variable?.type == VariableType.COMPUTED) {
            variable.expression = newExpression
            invalidateCache()
            updateDependencies(id)
            evaluateComputedVariable(id)
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
            invalidateCache()
            true
        } else {
            false
        }
    }

    fun updateVariableType(id: String, newType: VariableType, value: Double?, expression: String?): Boolean {
        val variable = variables[id]
        return if (variable != null) {
            variable.type = newType
            if (newType == VariableType.CONSTANT) {
                variable.value = value ?: 0.0
                variable.expression = null
            } else {
                variable.value = null
                variable.expression = expression ?: ""
            }
            variable.isValid = true
            invalidateCache()
            updateDependencies(id)
            if (newType == VariableType.COMPUTED) {
                evaluateComputedVariable(id)
            }
            propagateUpdates(id)
            true
        } else {
            false
        }
    }

    // ========== VARIABLE DELETE ==========

    fun deleteVariable(id: String): Boolean {
        val dependents = reverseDependencies[id]
        if (!dependents.isNullOrEmpty()) {
            throw IllegalStateException("Cannot delete '${variables[id]?.name}': ${dependents.size} other variable(s) depend on it")
        }

        dependencyGraph.remove(id)
        reverseDependencies.values.forEach { it.remove(id) }
        invalidateCache()

        return variables.remove(id) != null
    }

    // ========== DEPENDENCY MANAGEMENT ==========

    private fun updateDependencies(variableId: String) {
        val variable = variables[variableId]
        if (variable?.type == VariableType.COMPUTED && variable.expression != null) {
            val usedVariables = findVariableNamesInExpression(variable.expression!!)
            dependencyGraph[variableId] = usedVariables.toMutableSet()

            usedVariables.forEach { depId ->
                reverseDependencies.getOrPut(depId) { mutableSetOf() }.add(variableId)
            }
        } else {
            dependencyGraph[variableId] = mutableSetOf()
        }
    }

    private fun findVariableNamesInExpression(expression: String): Set<String> {
        val usedVariableIds = mutableSetOf<String>()
        variables.values.forEach { variable ->
            if (expression.contains(variable.name)) {
                usedVariableIds.add(variable.id)
            }
        }
        return usedVariableIds
    }

    private fun propagateUpdates(changedVariableId: String) {
        val toUpdate = mutableListOf<String>()
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<String>()
        queue.add(changedVariableId)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val dependents = reverseDependencies[current] ?: emptySet()

            dependents.forEach { dependent ->
                if (!visited.contains(dependent)) {
                    visited.add(dependent)
                    toUpdate.add(dependent)
                    queue.add(dependent)
                }
            }
        }

        // Clear cache before updating dependents
        invalidateCache()

        toUpdate.forEach { variableId ->
            evaluateComputedVariable(variableId)
        }
    }

    private fun evaluateComputedVariable(variableId: String) {
        val variable = variables[variableId]
        if (variable?.type == VariableType.COMPUTED && variable.expression != null) {
            // Build variable map WITHOUT re-evaluating to avoid recursion
            val variableMap = buildVariableMapSafely()
            val result = evaluator.evaluate(variable.expression!!, variableMap)

            if (result != null) {
                variable.value = result
                variable.isValid = true
            } else {
                variable.value = null
                variable.isValid = false
            }
        }
    }

    // Safely build variable map without triggering re-evaluation
    private fun buildVariableMapSafely(): Map<String, Double> {
        return variables.values.associate { it.name to (it.value ?: 0.0) }
    }

    fun getCurrentVariableValues(): Map<String, Double> {
        // Use cache if valid
        if (cacheValid && cachedVariableValues != null) {
            return cachedVariableValues!!
        }

        // Re-evaluate all computed variables first (without recursion)
        val computedVars = getComputedVariables()
        computedVars.forEach { computedVar ->
            if (computedVar.expression != null) {
                val variableMap = buildVariableMapSafely()
                val result = evaluator.evaluate(computedVar.expression!!, variableMap)
                computedVar.value = result
                computedVar.isValid = result != null
            }
        }

        // Build and cache the result
        cachedVariableValues = variables.values.associate { it.name to (it.value ?: 0.0) }
        cacheValid = true
        return cachedVariableValues!!
    }

    private fun invalidateCache() {
        cacheValid = false
        cachedVariableValues = null
    }

    // ========== EQUATION OPERATIONS ==========

    fun getEquations(): List<Equation> = equations.values.toList()

    fun addEquation(equation: Equation) {
        equations[equation.id] = equation
    }

    fun updateEquation(id: String, name: String, expression: String): Boolean {
        val equation = equations[id]
        return if (equation != null) {
            equation.name = name
            equation.expression = expression
            true
        } else {
            false
        }
    }

    fun deleteEquation(id: String): Boolean {
        return equations.remove(id) != null
    }

    fun getEquation(id: String): Equation? = equations[id]

    // ========== INITIAL DEMO DATA ==========

    init {
        saveVariable(null, "x", VariableType.CONSTANT, 5.0, null)
        saveVariable(null, "y", VariableType.CONSTANT, 3.0, null)
        saveVariable(null, "a", VariableType.CONSTANT, 2.0, null)

        saveVariable(null, "sum", VariableType.COMPUTED, null, "x + y")
        saveVariable(null, "product", VariableType.COMPUTED, null, "x * y")
        saveVariable(null, "quadratic", VariableType.COMPUTED, null, "sum^2 + product")

        equations["eq1"] = Equation("eq1", "Linear Equation", "x + y")
        equations["eq2"] = Equation("eq2", "Quadratic Equation", "x^2 + y")
        equations["eq3"] = Equation("eq3", "Using Computed", "sum * 2 + product")
    }
}