package com.example.zodiaccalculator.screen.equationdashboard

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.data.models.Variable

class EquationDashboardActivity : AppCompatActivity(), EquationDashboardContract.View {

    private lateinit var presenter: EquationDashboardPresenter
    private lateinit var linearLayoutVariables: LinearLayout
    private lateinit var buttonAddVariable: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equation_dashboard)

        linearLayoutVariables = findViewById(R.id.linearLayoutVariables)
        buttonAddVariable = findViewById(R.id.buttonAddVariable)

        presenter = EquationDashboardPresenter(this, EquationDashboardModel())

        buttonAddVariable.setOnClickListener { presenter.onAddVariableClick() }

        presenter.loadVariables()
    }

    override fun displayVariables(variables: List<Variable>) {
        linearLayoutVariables.removeAllViews()

        variables.forEach { variable ->
            val variableView = createVariableView(variable)
            linearLayoutVariables.addView(variableView)
        }
    }

    private fun createVariableView(variable: Variable): View {
        val variableView = layoutInflater.inflate(R.layout.item_variable, null)

        val editVariableName = variableView.findViewById<EditText>(R.id.editVariableName)
        val editVariableExpression = variableView.findViewById<EditText>(R.id.editVariableExpression)
        val deleteButton = variableView.findViewById<ImageButton>(R.id.buttonDeleteVariable)
        val textResult = variableView.findViewById<TextView>(R.id.textVariableResult)

        editVariableName.setText(variable.name)
        editVariableExpression.setText(variable.expression)

        // Get evaluation info from presenter
        val isFullyEvaluated = presenter.isFullyEvaluated(variable.id)
        val symbolicValue = presenter.getSymbolicExpression(variable.id)
        val undefinedVariables = presenter.getUndefinedVariables(variable.id)

        val resultText = when {
            isFullyEvaluated -> {
                "= ${variable.value}"
            }
            symbolicValue != variable.expression -> {
                "≈ $symbolicValue"
            }
            undefinedVariables.isNotEmpty() -> {
                "= ${variable.expression} (missing: ${undefinedVariables.joinToString(", ")})"
            }
            else -> {
                "= ${variable.expression}"
            }
        }

        textResult.text = resultText

        // Color coding
        when {
            isFullyEvaluated -> textResult.setTextColor(Color.parseColor("#4CAF50"))  // Green
            symbolicValue != variable.expression -> textResult.setTextColor(Color.parseColor("#FF9800"))  // Orange
            else -> textResult.setTextColor(Color.RED)  // Red
        }

        editVariableName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newName = editVariableName.text.toString()
                if (newName.isNotBlank() && newName != variable.name) {
                    presenter.onVariableNameChanged(variable.id, newName)
                }
            }
        }

        editVariableExpression.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newExpression = editVariableExpression.text.toString()
                if (newExpression.isNotBlank() && newExpression != variable.expression) {
                    presenter.onVariableExpressionChanged(variable.id, newExpression)
                }
            }
        }

        deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Variable")
                .setMessage("Delete '${variable.name}'? Variables that depend on it will break.")
                .setPositiveButton("Delete") { _, _ ->
                    presenter.onVariableDeleted(variable.id)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return variableView
    }

    override fun showAddVariableDialog() {
        showVariableDialog(null, "", "")
    }

    override fun showEditVariableDialog(variable: Variable) {
        showVariableDialog(variable.id, variable.name, variable.expression)
    }

    private fun showVariableDialog(id: String?, name: String, expression: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_variable, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.dialogVariableName)
        val expressionInput = dialogView.findViewById<EditText>(R.id.dialogVariableExpression)

        nameInput.setText(name)
        expressionInput.setText(expression)

        AlertDialog.Builder(this)
            .setTitle(if (id == null) "Add Variable" else "Edit Variable")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString()
                val newExpression = expressionInput.text.toString()
                presenter.onVariableSaved(id, newName, newExpression)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun refreshVariables() {
        presenter.loadVariables()
    }
}