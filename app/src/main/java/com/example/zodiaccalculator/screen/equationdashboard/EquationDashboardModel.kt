package com.example.zodiaccalculator.screen.equationdashboard

import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.models.Variable
import com.example.zodiaccalculator.data.repositories.VariableRepository

class EquationDashboardModel(private val app: ZodiacCalculator) {

    fun getVariables(): List<Variable> = VariableRepository.getVariables()

    fun addVariable(name: String, expression: String): Boolean {
        return try {
            VariableRepository.saveVariable(null, name, expression)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateVariable(id: String, name: String, expression: String): Boolean {
        return try {
            VariableRepository.updateVariableName(id, name)
            VariableRepository.updateVariableExpression(id, expression)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateVariableExpression(id: String, newExpression: String): Boolean {
        return try {
            VariableRepository.updateVariableExpression(id, newExpression)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateVariableName(id: String, newName: String): Boolean {
        return try {
            VariableRepository.updateVariableName(id, newName)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteVariable(id: String): Boolean {
        return try {
            VariableRepository.deleteVariable(id)
            true
        } catch (e: IllegalStateException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentVariableValues(): Map<String, Double> {
        return VariableRepository.getCurrentVariableValues()
    }

    fun getSymbolicExpression(variableId: String): String {
        return VariableRepository.getSymbolicExpression(variableId)
    }

    fun getUndefinedVariables(variableId: String): List<String> {
        return VariableRepository.getUndefinedVariables(variableId)
    }

    fun isFullyEvaluated(variableId: String): Boolean {
        return VariableRepository.isFullyEvaluated(variableId)
    }

    // ========== CALCULATION METHODS ==========

    fun getCurrentCalculationId(): String? {
        return app.currentCalculationId  // ← Get from CustomApp, not User
    }

    fun loadCalculationVariables(calculationId: String) {
        val calculation = app.currentUser?.calculations?.find { it.id == calculationId }
        VariableRepository.loadFromCalculation(calculation)
    }

    fun loadCurrentCalculation() {
        val calculationId = app.currentCalculationId
        val calculation = app.currentUser?.calculations?.find { it.id == calculationId }
        VariableRepository.loadFromCalculation(calculation)
    }

    fun autoSaveCurrentCalculation() {
        val calculationId = app.currentCalculationId
        if (calculationId != null) {
            val variables = getVariables()  // Get current variables
            val calculation = app.currentUser?.calculations?.find { it.id == calculationId }
            calculation?.let {
                it.variables = variables.toList()  // Save a copy
                it.dateModified = java.time.LocalDateTime.now()
            }
        }
    }

}