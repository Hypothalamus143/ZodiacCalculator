package com.example.zodiaccalculator.screen.equationdashboard

import com.example.zodiaccalculator.data.models.Equation
import com.example.zodiaccalculator.data.models.Variable
import com.example.zodiaccalculator.data.models.VariableType
import com.example.zodiaccalculator.data.repositories.EquationRepository
import com.example.zodiaccalculator.data.utils.ExpressionEvaluator
import java.util.UUID

class EquationDashboardModel {
    private val evaluator = ExpressionEvaluator()

    fun getEquations(): List<Equation> = EquationRepository.getEquations()
    fun getVariables(): List<Variable> = EquationRepository.getVariables()
    fun getCurrentVariableValues(): Map<String, Double> = EquationRepository.getCurrentVariableValues()

    fun evaluateEquation(equation: Equation): Double? {
        val variables = EquationRepository.getCurrentVariableValues()
        return evaluator.evaluate(equation.expression, variables)
    }

    fun evaluateAllEquations(): List<Pair<Equation, Double?>> {
        val equations = getEquations()
        return equations.map { equation ->
            val result = evaluateEquation(equation)
            equation.currentResult = result
            equation to result
        }
    }

    // ========== VARIABLE OPERATIONS ==========

    fun addVariable(name: String, type: VariableType, value: Double?, expression: String?): Boolean {
        return try {
            EquationRepository.saveVariable(null, name, type, value, expression)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateVariable(id: String, name: String, type: VariableType, value: Double?, expression: String?): Boolean {
        return try {
            EquationRepository.saveVariable(id, name, type, value, expression)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateConstantValue(id: String, newValue: Double): Boolean {
        return try {
            EquationRepository.updateConstantValue(id, newValue)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateComputedExpression(id: String, newExpression: String): Boolean {
        return try {
            EquationRepository.updateComputedExpression(id, newExpression)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteVariable(id: String): Boolean {
        return try {
            EquationRepository.deleteVariable(id)
            true
        } catch (e: IllegalStateException) {
            throw e  // Re-throw to let presenter handle
        } catch (e: Exception) {
            false
        }
    }

    // ========== EQUATION OPERATIONS ==========

    fun addEquation(name: String, expression: String): Boolean {
        return try {
            val id = UUID.randomUUID().toString()
            EquationRepository.addEquation(Equation(id, name, expression))
            true
        } catch (e: Exception) {
            false
        }
    }

    fun updateEquation(id: String, name: String, expression: String): Boolean {
        return try {
            EquationRepository.updateEquation(id, name, expression)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun deleteEquation(id: String): Boolean {
        return try {
            EquationRepository.deleteEquation(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}