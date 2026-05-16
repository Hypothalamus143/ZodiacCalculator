package com.example.zodiaccalculator.screen.equationdashboard

import com.example.zodiaccalculator.data.models.Variable

class EquationDashboardPresenter(
    private val view: EquationDashboardContract.View,
    private val model: EquationDashboardModel
) : EquationDashboardContract.Presenter {
    override fun loadVariables() {
        val variables = model.getVariables()
        view.displayVariables(variables)
        updateCounter()
    }

    fun loadCurrentCalculation() {
        model.loadCurrentCalculation()
        loadVariables()
    }

    fun loadCalculation(calculationId: String) {
        model.loadCalculationVariables(calculationId)
        loadVariables()
    }

    private fun autoSave() {
        model.autoSaveCurrentCalculation()
        val size = model.getCalculationSize()
        updateCounter()
    }

    override fun onAddVariableClick() {
        view.showAddVariableDialog()
    }

    override fun onEditVariableClick(variable: Variable) {
        view.showEditVariableDialog(variable)
    }

    override fun onVariableSaved(id: String?, name: String, expression: String) {
        if (name.isBlank()) {
            view.showError("Variable name cannot be empty")
            return
        }

        if (expression.isBlank()) {
            view.showError("Expression cannot be empty")
            return
        }

        val existingVariable = model.getVariables().find { it.name == name && it.id != id }
        if (existingVariable != null) {
            view.showError("Variable with name '$name' already exists")
            return
        }

        val success = if (id == null) {
            model.addVariable(name, expression)
        } else {
            model.updateVariable(id, name, expression)
        }

        if (success) {
            refreshAfterChange()
            autoSave()  // ← Save after add/edit
            view.showSuccess(if (id == null) "Variable added" else "Variable updated")
        } else {
            view.showError("Failed to save variable")
        }
    }

    override fun onVariableDeleted(variableId: String) {
        try {
            if (model.deleteVariable(variableId)) {
                refreshAfterChange()
                autoSave()  // ← Save after delete
                view.showSuccess("Variable deleted")
            } else {
                view.showError("Failed to delete variable")
            }
        } catch (e: IllegalStateException) {
            view.showError(e.message ?: "Cannot delete: other variables depend on this one")
        }
    }

    override fun onVariableExpressionChanged(variableId: String, newExpression: String) {
        if (newExpression.isBlank()) {
            view.showError("Expression cannot be empty")
            return
        }

        if (model.updateVariableExpression(variableId, newExpression)) {
            refreshAfterChange()
            autoSave()  // ← Save after expression change
        } else {
            view.showError("Failed to update expression")
        }
    }

    override fun onVariableNameChanged(variableId: String, newName: String) {
        if (newName.isBlank()) {
            view.showError("Variable name cannot be empty")
            return
        }

        val existingVariable = model.getVariables().find { it.name == newName && it.id != variableId }
        if (existingVariable != null) {
            view.showError("Variable with name '$newName' already exists")
            return
        }

        if (model.updateVariableName(variableId, newName)) {
            refreshAfterChange()
            autoSave()  // ← Save after name change
            view.showSuccess("Variable renamed to '$newName'")
        } else {
            view.showError("Failed to rename variable")
        }
    }

    private fun refreshAfterChange() {
        val variables = model.getVariables()
        view.displayVariables(variables)
        view.refreshVariables()
    }
    override fun getCurrentVariableValues(): Map<String, Double> {
        return model.getCurrentVariableValues()
    }
    fun getSymbolicExpression(variableId: String): String {
        return model.getSymbolicExpression(variableId)
    }

    fun getUndefinedVariables(variableId: String): List<String> {
        return model.getUndefinedVariables(variableId)
    }

    fun isFullyEvaluated(variableId: String): Boolean {
        return model.isFullyEvaluated(variableId)
    }
    override fun logoClicked(){
        view.navigateToDashboard();
    }
    fun updateCounter(){
        val size = model.getCalculationSize()
        if(size == 1)
            view.setVariableCount("1 item")
        else
            view.setVariableCount(size.toString()+" items")
    }
}