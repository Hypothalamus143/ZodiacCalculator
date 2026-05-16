package com.example.zodiaccalculator.screen.equationdashboard

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.data.models.Equation
import com.example.zodiaccalculator.data.models.Variable
import com.example.zodiaccalculator.data.models.VariableType

class EquationDashboardActivity : AppCompatActivity(), EquationDashboardContract.View {

    private lateinit var presenter: EquationDashboardPresenter
    private lateinit var linearLayoutVariables: LinearLayout
    private lateinit var recyclerViewEquations: RecyclerView
    private lateinit var buttonAddEquation: Button

    private lateinit var equationsAdapter: EquationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_equation_dashboard)

        // Initialize views
        linearLayoutVariables = findViewById(R.id.linearLayoutVariables)
        recyclerViewEquations = findViewById(R.id.recyclerViewEquations)
        buttonAddEquation = findViewById(R.id.buttonAddEquation)

        // Initialize presenter
        presenter = EquationDashboardPresenter(this, EquationDashboardModel())

        setupRecyclerView()
        setupButtons()

        // Load initial data
        presenter.loadDashboard()
    }

    private fun setupRecyclerView() {
        equationsAdapter = EquationsAdapter(
            onEditClick = { equation ->
                presenter.onEditEquationClick(equation)
            },
            onDeleteClick = { equationId ->
                presenter.onDeleteEquationClick(equationId)
            }
        )
        recyclerViewEquations.layoutManager = LinearLayoutManager(this)
        recyclerViewEquations.adapter = equationsAdapter
    }

    private fun setupButtons() {
        buttonAddEquation.setOnClickListener {
            presenter.onAddEquationClick()
        }
    }

    // ========== EquationDashboardContract.View Implementation ==========

    override fun displayEquations(equations: List<Pair<Equation, Double?>>) {
        equationsAdapter.updateEquations(equations)
    }

    override fun displayVariables(variables: List<Variable>) {
        linearLayoutVariables.removeAllViews()

        // Add button for new variable
        val addButton = Button(this).apply {
            text = "+ Add New Variable"
            setOnClickListener { presenter.onAddVariableClick() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }
        linearLayoutVariables.addView(addButton)

        // Display each variable
        variables.forEach { variable ->
            val variableView = createVariableView(variable)
            linearLayoutVariables.addView(variableView)
        }
    }

    private fun createVariableView(variable: Variable): View {
        val variableView = layoutInflater.inflate(R.layout.item_variable, null)

        val editVariableName = variableView.findViewById<EditText>(R.id.editVariableName)
        val spinnerType = variableView.findViewById<Spinner>(R.id.spinnerVariableType)
        val deleteButton = variableView.findViewById<ImageButton>(R.id.buttonDeleteVariable)
        val layoutConstant = variableView.findViewById<LinearLayout>(R.id.layoutConstantValue)
        val layoutComputed = variableView.findViewById<LinearLayout>(R.id.layoutComputedExpression)
        val seekBar = variableView.findViewById<SeekBar>(R.id.seekBarVariable)
        val valueInput = variableView.findViewById<EditText>(R.id.editTextVariableValue)
        val expressionInput = variableView.findViewById<EditText>(R.id.editTextExpression)
        val textResult = variableView.findViewById<TextView>(R.id.textComputedResult)
        val textPreview = variableView.findViewById<TextView>(R.id.textExpressionPreview)

        editVariableName.setText(variable.name)

        // Setup spinner - READ ONLY, no listener
        val typeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.variable_types,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerType.adapter = typeAdapter
        spinnerType.isEnabled = false  // Disable the spinner - prevent changes
        spinnerType.setSelection(if (variable.type == VariableType.CONSTANT) 0 else 1)

        // Function to update UI based on type
        fun updateUIVisibility() {
            val isConstant = variable.type == VariableType.CONSTANT
            layoutConstant.visibility = if (isConstant) View.VISIBLE else View.GONE
            layoutComputed.visibility = if (isConstant) View.GONE else View.VISIBLE
        }

        // Set values based on variable type
        if (variable.type == VariableType.CONSTANT) {
            val constantValue = variable.value ?: 0.0
            seekBar.progress = constantValue.toInt().coerceIn(0, 100)
            valueInput.setText(constantValue.toString())
            textResult.text = "Value: $constantValue"
        } else {
            expressionInput.setText(variable.expression ?: "")
            val resultText = if (variable.value != null && variable.isValid) {
                "Result: ${variable.value}"
            } else {
                "Result: Invalid Expression"
            }
            textResult.text = resultText
            if (!variable.isValid) {
                textResult.setTextColor(Color.RED)
            }

            // Show expression preview
            val currentValues = presenter.getCurrentVariableValues()
            textPreview.text = "= ${expressionInput.text} with current values"
        }

        updateUIVisibility()

        // Name change listener
        editVariableName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newName = editVariableName.text.toString()
                if (newName.isNotBlank() && newName != variable.name) {
                    presenter.onVariableNameChanged(variable.id, newName)
                }
            }
        }

        // Constant value listeners
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    valueInput.setText(progress.toString())
                    presenter.onConstantValueChanged(variable.id, progress.toDouble())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        valueInput.setOnEditorActionListener { _, _, _ ->
            val newValue = valueInput.text.toString().toDoubleOrNull()
            if (newValue != null) {
                seekBar.progress = newValue.toInt().coerceIn(0, 100)
                presenter.onConstantValueChanged(variable.id, newValue)
            }
            true
        }

        // Computed expression listener
        expressionInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newExpr = expressionInput.text.toString()
                if (newExpr.isNotBlank()) {
                    presenter.onComputedExpressionChanged(variable.id, newExpr)
                }
            }
        }

        // Delete button
        deleteButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Variable")
                .setMessage("Delete '${variable.name}'? This may break equations/computed variables that depend on it.")
                .setPositiveButton("Delete") { _, _ ->
                    presenter.onVariableDeleted(variable.id)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return variableView
    }
    override fun showAddEquationDialog() {
        showEquationDialog(null, "", "")
    }

    override fun showEditEquationDialog(equation: Equation) {
        showEquationDialog(equation.id, equation.name, equation.expression)
    }

    private fun showEquationDialog(id: String?, name: String, expression: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_equation, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.dialogEditEquationName)
        val expressionInput = dialogView.findViewById<EditText>(R.id.dialogEditExpression)

        nameInput.setText(name)
        expressionInput.setText(expression)

        AlertDialog.Builder(this)
            .setTitle(if (id == null) "Add Equation" else "Edit Equation")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString()
                val newExpression = expressionInput.text.toString()
                presenter.onEquationSaved(id, newName, newExpression)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun showAddVariableDialog() {
        showVariableDialog(null, "", 0.0, VariableType.CONSTANT)
    }

    override fun showEditVariableDialog(variable: Variable) {
        showVariableDialog(
            variable.id,
            variable.name,
            variable.value ?: 0.0,
            variable.type,
            variable.expression
        )
    }

    private fun showVariableDialog(
        id: String?,
        name: String,
        value: Double,
        type: VariableType,
        expression: String? = null
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_edit_variable, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.dialogVariableName)
        val valueInput = dialogView.findViewById<EditText>(R.id.dialogVariableValue)
        val expressionInput = dialogView.findViewById<EditText>(R.id.dialogVariableExpression)
        val typeSpinner = dialogView.findViewById<Spinner>(R.id.spinnerDialogVariableType)
        val valueLayout = dialogView.findViewById<LinearLayout>(R.id.layoutDialogValue)
        val expressionLayout = dialogView.findViewById<LinearLayout>(R.id.layoutDialogExpression)

        nameInput.setText(name)

        // Setup type spinner
        val typeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.variable_types,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        typeSpinner.adapter = typeAdapter
        typeSpinner.setSelection(if (type == VariableType.CONSTANT) 0 else 1)

        // Show/hide appropriate input
        fun updateInputVisibility() {
            val isConstant = typeSpinner.selectedItemPosition == 0
            valueLayout.visibility = if (isConstant) View.VISIBLE else View.GONE
            expressionLayout.visibility = if (isConstant) View.GONE else View.VISIBLE
        }

        if (type == VariableType.CONSTANT) {
            valueInput.setText(value.toString())
        } else {
            expressionInput.setText(expression ?: "")
        }

        updateInputVisibility()

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateInputVisibility()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        AlertDialog.Builder(this)
            .setTitle(if (id == null) "Add Variable" else "Edit Variable")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString()
                val isConstant = typeSpinner.selectedItemPosition == 0

                if (isConstant) {
                    val newValue = valueInput.text.toString().toDoubleOrNull() ?: 0.0
                    presenter.onVariableSaved(id, newName, VariableType.CONSTANT, newValue, null)
                } else {
                    val newExpression = expressionInput.text.toString()
                    presenter.onVariableSaved(id, newName, VariableType.COMPUTED, null, newExpression)
                }
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

    override fun refreshAll() {
        recyclerViewEquations.adapter?.notifyDataSetChanged()
    }
}

// ========== RecyclerView Adapter for Equations ==========

class EquationsAdapter(
    private val onEditClick: (Equation) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<EquationsAdapter.EquationViewHolder>() {

    private var equations = listOf<Pair<Equation, Double?>>()

    fun updateEquations(newEquations: List<Pair<Equation, Double?>>) {
        equations = newEquations
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): EquationViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_equation, parent, false)
        return EquationViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquationViewHolder, position: Int) {
        val (equation, result) = equations[position]
        holder.bind(equation, result, onEditClick, onDeleteClick)
    }

    override fun getItemCount() = equations.size

    class EquationViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val textEquationName = itemView.findViewById<TextView>(R.id.textEquationName)
        private val textExpression = itemView.findViewById<TextView>(R.id.textExpression)
        private val textResult = itemView.findViewById<TextView>(R.id.textResult)
        private val buttonEdit = itemView.findViewById<ImageButton>(R.id.buttonEditEquation)
        private val buttonDelete = itemView.findViewById<ImageButton>(R.id.buttonDeleteEquation)

        fun bind(
            equation: Equation,
            result: Double?,
            onEditClick: (Equation) -> Unit,
            onDeleteClick: (String) -> Unit
        ) {
            textEquationName.text = equation.name
            textExpression.text = "Expression: ${equation.expression}"
            textResult.text = result?.let { "Result: $it" } ?: "Result: Error"

            if (result == null) {
                textResult.setTextColor(Color.RED)
            } else {
                textResult.setTextColor(Color.parseColor("#4CAF50"))
            }

            buttonEdit.setOnClickListener { onEditClick(equation) }
            buttonDelete.setOnClickListener { onDeleteClick(equation.id) }
        }
    }
}