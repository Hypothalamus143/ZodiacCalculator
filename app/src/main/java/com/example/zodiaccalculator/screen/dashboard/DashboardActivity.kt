package com.example.zodiaccalculator.screen.dashboard

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.utils.Extensions.app
import com.example.zodiaccalculator.utils.Extensions.toastText

class DashboardActivity : Activity(), DashboardContract.View {
    private lateinit var presenter: DashboardPresenter
    private lateinit var adapter: CalculationsRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val recyclerView = findViewById<RecyclerView>(R.id.calculationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CalculationsRecyclerViewAdapter(this, mutableListOf())  // Initialize here
        recyclerView.adapter = adapter

        presenter = DashboardPresenter(this, DashboardModel(app()))

        val buttonAddCalculation = findViewById<Button>(R.id.buttonAddCalculation)
        val edittextTitle = findViewById<EditText>(R.id.edittextTitle)

        buttonAddCalculation.setOnClickListener {
            presenter.onAddCalculationsClicked(edittextTitle.text.toString())
        }
    }

    override fun setCalculationsListView(calculations: MutableList<Calculation>) {
        adapter?.updateList(calculations)  // Safe call with ?
    }

    override fun showError(message: String) {
        toastText(message)
    }

    override fun notifyChanges() {
        adapter?.notifyChanges()  // Safe call with ?
    }
}