package com.example.zodiaccalculator.screen.equationdashboard

import com.example.zodiaccalculator.data.models.Variable

class EquationDashboardContract {
    interface View {
        fun displayVariables(variables: List<Variable>)
        fun showAddVariableDialog()
        fun showEditVariableDialog(variable: Variable)
        fun showError(message: String)
        fun showSuccess(message: String)
        fun refreshVariables()
    }

    interface Presenter {
        fun loadVariables()
        fun onAddVariableClick()
        fun onEditVariableClick(variable: Variable)
        fun onVariableSaved(id: String?, name: String, expression: String)
        fun onVariableDeleted(variableId: String)
        fun onVariableExpressionChanged(variableId: String, newExpression: String)
        fun onVariableNameChanged(variableId: String, newName: String)
        fun getCurrentVariableValues(): Map<String, Double>  // ADD THIS
    }
}