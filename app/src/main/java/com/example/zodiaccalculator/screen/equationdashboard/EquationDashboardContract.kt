package com.example.zodiaccalculator.screen.equationdashboard

import com.example.zodiaccalculator.data.models.Equation
import com.example.zodiaccalculator.data.models.Variable
import com.example.zodiaccalculator.data.models.VariableType

class EquationDashboardContract {
    interface View {
        fun displayEquations(equations: List<Pair<Equation, Double?>>)
        fun displayVariables(variables: List<Variable>)
        fun showAddEquationDialog()
        fun showEditEquationDialog(equation: Equation)
        fun showAddVariableDialog()
        fun showEditVariableDialog(variable: Variable)
        fun showError(message: String)
        fun showSuccess(message: String)
        fun refreshAll()
    }

    interface Presenter {
        fun loadDashboard()

        // Equation operations
        fun onAddEquationClick()
        fun onEditEquationClick(equation: Equation)
        fun onDeleteEquationClick(equationId: String)
        fun onEquationSaved(id: String?, name: String, expression: String)
        fun onEquationDeleted(id: String)

        // Variable operations
        fun onAddVariableClick()
        fun onEditVariableClick(variable: Variable)
        fun onVariableSaved(id: String?, name: String, type: VariableType, value: Double?, expression: String?)
        fun onVariableDeleted(variableId: String)

        // Variable value/expression changes
        fun onConstantValueChanged(variableId: String, newValue: Double)
        fun onComputedExpressionChanged(variableId: String, newExpression: String)
        fun onVariableNameChanged(variableId: String, newName: String)
    }
}