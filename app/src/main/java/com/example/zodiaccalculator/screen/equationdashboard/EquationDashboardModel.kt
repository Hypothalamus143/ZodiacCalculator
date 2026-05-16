package com.example.zodiaccalculator.screen.equationdashboard

import com.example.zodiaccalculator.data.models.Variable
import com.example.zodiaccalculator.data.repositories.VariableRepository  // Changed from EquationRepository

class EquationDashboardModel {

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
}