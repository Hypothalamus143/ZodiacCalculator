package com.example.zodiaccalculator.screen.dashboard

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.utils.Extensions.app
import com.example.zodiaccalculator.utils.Extensions.toastText

class DashboardActivity : Activity(), DashboardContract.View {
    private lateinit var presenter : DashboardPresenter
    private lateinit var adapter: CalculationsListViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        presenter = DashboardPresenter(this, DashboardModel(app()))
        val buttonAddCalculation = findViewById<Button>(R.id.buttonAddCalculation)
        val edittextTitle = findViewById<EditText>(R.id.edittextTitle)
        buttonAddCalculation.setOnClickListener {
            presenter.onAddCalculationsClicked(edittextTitle.text.toString())
        }
//        val buttonProfile = findViewById<Button>(R.id.buttonProfile)
//        buttonProfile.setOnClickListener(){
//            val intent = Intent(this, ProfileActivity::class.java)
//            startActivity(intent)
//        }
//        val textviewLogout= findViewById<TextView>(R.id.textviewLogout)
//        textviewLogout.setOnClickListener(){
//            val intent = Intent(this, SplashActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
    }

    override fun setCalculationsListView(calculations: MutableList<Calculation>) {
        val listView = findViewById<ListView>(R.id.calculationsListView)
        adapter = CalculationsListViewAdapter(this, calculations)
        listView.adapter = adapter
    }

    override fun showError(message: String) {
        toastText(message)
    }

    override fun notifyChanges() {
        adapter.notifyDataSetChanged()
    }


}