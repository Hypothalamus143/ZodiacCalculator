package com.example.zodiaccalculator.screen.equationdashboard

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.data.models.Variable
import com.example.zodiaccalculator.screen.dashboard.DashboardActivity
import com.example.zodiaccalculator.utils.Extensions.app
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EquationDashboardActivity : Activity(), EquationDashboardContract.View {

    private lateinit var presenter: EquationDashboardPresenter
    private lateinit var linearLayoutVariables: LinearLayout
    private lateinit var buttonAddVariable: FloatingActionButton
    private lateinit var imageHome: ImageView
    private lateinit var textviewVariableCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equation_dashboard)

        linearLayoutVariables = findViewById(R.id.linearLayoutVariables)
        buttonAddVariable = findViewById(R.id.buttonAddVariable)
        imageHome = findViewById<ImageView>(R.id.imageViewLogo)
        textviewVariableCount = findViewById<TextView>(R.id.textviewVariableCount)
        val app = app()
        val model = EquationDashboardModel(app)
        presenter = EquationDashboardPresenter(this, model)

        // Load from current calculation (set by CalculationsListView)
        presenter.loadCurrentCalculation()

        buttonAddVariable.setOnClickListener { presenter.onAddVariableClick() }
        imageHome.setOnClickListener { presenter.logoClicked() }
    }

    override fun displayVariables(variables: List<Variable>) {
        linearLayoutVariables.removeAllViews()

        variables.forEach { variable ->
            val variableView = createVariableView(variable)
            linearLayoutVariables.addView(variableView)
        }
    }


    override fun navigateToDashboard(){
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun createVariableView(variable: Variable): View {
        val variableView = layoutInflater.inflate(R.layout.item_variable, null)

        val textviewVariableName = variableView.findViewById<TextView>(R.id.textVariableName)
        val textviewExpressionName = variableView.findViewById<TextView>(R.id.textVariableExpression)
        val textviewDeleteVariable = variableView.findViewById<TextView>(R.id.textviewDeleteVariable)
        val textResult = variableView.findViewById<TextView>(R.id.textVariableResult)

        textviewVariableName.setText(variable.name)
        textviewExpressionName.setText(variable.expression)

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

        textviewVariableName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newName = textviewVariableName.text.toString()
                if (newName.isNotBlank() && newName != variable.name) {
                    presenter.onVariableNameChanged(variable.id, newName)
                }
            }
        }

        textviewExpressionName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newExpression = textviewExpressionName.text.toString()
                if (newExpression.isNotBlank() && newExpression != variable.expression) {
                    presenter.onVariableExpressionChanged(variable.id, newExpression)
                }
            }
        }

        textviewDeleteVariable.setOnClickListener {
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
    override fun setVariableCount(count: String){
        textviewVariableCount.text = count
    }
}