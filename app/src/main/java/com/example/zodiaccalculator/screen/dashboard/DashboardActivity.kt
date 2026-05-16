package com.example.zodiaccalculator.screen.dashboard

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.screen.login.LoginActivity
import com.example.zodiaccalculator.utils.Extensions.app
import com.example.zodiaccalculator.utils.Extensions.toastText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class DashboardActivity : Activity(), DashboardContract.View {
    private lateinit var presenter: DashboardPresenter
    private lateinit var adapter: CalculationsRecyclerViewAdapter
    private lateinit var textviewItemCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        textviewItemCount = findViewById(R.id.textviewItemCount)

        // Setup RecyclerView FIRST
        val recyclerView = findViewById<RecyclerView>(R.id.calculationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CalculationsRecyclerViewAdapter(this, mutableListOf())
        recyclerView.adapter = adapter


        // THEN create presenter
        presenter = DashboardPresenter(this, DashboardModel(app()))



        // Setup FAB button to show dialog
        val buttonAddCalculation = findViewById<FloatingActionButton>(R.id.buttonAddCalculation)
        buttonAddCalculation.setOnClickListener {
            showAddCalculationDialog()
        }

        // Setup logout button
        val buttonLogout = findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            presenter.onLogoutClicked()
        }
    }

    private fun showAddCalculationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_calculation, null)
        val titleInput = dialogView.findViewById<TextInputEditText>(R.id.edittextDialogTitle)

        MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleInput.text.toString().trim()
                if (title.isNotEmpty()) {
                    presenter.onAddCalculationsClicked(title)
                } else {
                    toastText("Please enter a title")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun setCalculationsListView(calculations: MutableList<Calculation>) {
        adapter.updateList(calculations)
        updateItemCount(calculations.size)
    }

    override fun showError(message: String) {
        toastText(message)
    }

    override fun notifyChanges() {
        adapter.notifyChanges()
        updateItemCount(adapter.getItemCount())
    }

    override fun logout(){
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun updateItemCount(count: Int) {
        textviewItemCount.text = "$count item${if (count != 1) "s" else ""}"
    }
}