package com.example.zodiaccalculator.screen.equationdashboard

import com.example.zodiaccalculator.data.models.VariableType

class EquationDashboardPresenter(
    private val view: EquationDashboardContract.View,
    private val model: EquationDashboardModel
) : EquationDashboardContract.Presenter {

    override fun loadDashboard() {
        val variables = model.getVariables()
        val equationsWithResults = model.evaluateAllEquations()

        view.displayVariables(variables)
        view.displayEquations(equationsWithResults)
    }

    // ========== EQUATION OPERATIONS ==========

    override fun onAddEquationClick() {
        view.showAddEquationDialog()
    }

    override fun onEditEquationClick(equation: com.example.zodiaccalculator.data.models.Equation) {
        view.showEditEquationDialog(equation)
    }

    override fun onDeleteEquationClick(equationId: String) {
        if (model.deleteEquation(equationId)) {
            refreshAfterChange()
            view.showSuccess("Equation deleted")
        } else {
            view.showError("Failed to delete equation")
        }
    }

    override fun onEquationSaved(id: String?, name: String, expression: String) {
        if (name.isBlank() || expression.isBlank()) {
            view.showError("Name and expression cannot be empty")
            return
        }

        val success = if (id == null) {
            model.addEquation(name, expression)
        } else {
            model.updateEquation(id, name, expression)
        }

        if (success) {
            refreshAfterChange()
            view.showSuccess(if (id == null) "Equation added" else "Equation updated")
        } else {
            view.showError("Failed to save equation")
        }
    }

    override fun onEquationDeleted(id: String) {
        if (model.deleteEquation(id)) {
            refreshAfterChange()
            view.showSuccess("Equation deleted")
        } else {
            view.showError("Failed to delete equation")
        }
    }

    // ========== VARIABLE OPERATIONS ==========

    override fun onAddVariableClick() {
        view.showAddVariableDialog()
    }

    override fun onEditVariableClick(variable: com.example.zodiaccalculator.data.models.Variable) {
        view.showEditVariableDialog(variable)
    }

    override fun onVariableSaved(
        id: String?,
        name: String,
        type: VariableType,
        value: Double?,
        expression: String?
    ) {
        if (name.isBlank()) {
            view.showError("Variable name cannot be empty")
            return
        }

        // Check for duplicate names
        val existingVariable = model.getVariables().find { it.name == name && it.id != id }
        if (existingVariable != null) {
            view.showError("Variable with name '$name' already exists")
            return
        }

        // Validate based on type
        if (type == VariableType.CONSTANT && value == null) {
            view.showError("Constant variables must have a value")
            return
        }

        if (type == VariableType.COMPUTED && (expression.isNullOrBlank())) {
            view.showError("Computed variables must have an expression")
            return
        }

        val success = if (id == null) {
            model.addVariable(name, type, value, expression)
        } else {
            model.updateVariable(id, name, type, value, expression)
        }

        if (success) {
            refreshAfterChange()
            view.showSuccess(if (id == null) "Variable added" else "Variable updated")
        } else {
            view.showError("Failed to save variable")
        }
    }

    override fun onVariableDeleted(variableId: String) {
        try {
            if (model.deleteVariable(variableId)) {
                refreshAfterChange()
                view.showSuccess("Variable deleted")
            } else {
                view.showError("Failed to delete variable")
            }
        } catch (e: IllegalStateException) {
            view.showError(e.message ?: "Cannot delete: other variables depend on this one")
        }
    }

    // ========== VARIABLE VALUE/EXPRESSION CHANGES ==========

    override fun onConstantValueChanged(variableId: String, newValue: Double) {
        if (model.updateConstantValue(variableId, newValue)) {
            refreshAfterChange()
        } else {
            view.showError("Failed to update constant value")
        }
    }

    override fun onComputedExpressionChanged(variableId: String, newExpression: String) {
        if (newExpression.isBlank()) {
            view.showError("Expression cannot be empty")
            return
        }

        if (model.updateComputedExpression(variableId, newExpression)) {
            refreshAfterChange()
            view.showSuccess("Expression updated")
        } else {
            view.showError("Failed to update expression")
        }
    }

    override fun onVariableNameChanged(variableId: String, newName: String) {
        if (newName.isBlank()) {
            view.showError("Variable name cannot be empty")
            return
        }

        // Check for duplicate names
        val existingVariable = model.getVariables().find { it.name == newName && it.id != variableId }
        if (existingVariable != null) {
            view.showError("Variable with name '$newName' already exists")
            return
        }

        val variable = model.getVariables().find { it.id == variableId }
        if (variable != null) {
            if (model.updateVariable(variableId, newName, variable.type, variable.value, variable.expression)) {
                refreshAfterChange()
                view.showSuccess("Variable renamed to '$newName'")
            } else {
                view.showError("Failed to rename variable")
            }
        }
    }

    // ========== HELPERS ==========

    private fun refreshAfterChange() {
        val variables = model.getVariables()
        val equationsWithResults = model.evaluateAllEquations()

        view.displayVariables(variables)
        view.displayEquations(equationsWithResults)
        view.refreshAll()
    }
    // Add this method to EquationDashboardPresenter
    fun getCurrentVariableValues(): Map<String, Double> {
        return model.getCurrentVariableValues()
    }
}