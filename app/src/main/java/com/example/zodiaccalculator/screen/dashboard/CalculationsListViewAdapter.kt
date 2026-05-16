package com.example.zodiaccalculator.screen.dashboard

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.screen.equationdashboard.EquationDashboardActivity

class CalculationsListViewAdapter (private val context: Context, private val calculationsList: MutableList<Calculation>): BaseAdapter() {
    override fun getCount(): Int {
        return calculationsList.size
    }

    override fun getItem(pos: Int): Calculation {
        return calculationsList[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(
        pos: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.calculations_list_view, parent, false)
        val title = view.findViewById<TextView>(R.id.textviewTitle);
        val dateCreated = view.findViewById<TextView>(R.id.textviewDateCreated);
        val dateModified = view.findViewById<TextView>(R.id.textviewDateModified);
        val buttonDeleteCalculation = view.findViewById<Button>(R.id.buttonDeleteCalculation)
        buttonDeleteCalculation.setOnClickListener {
            calculationsList.removeAt(pos)
            notifyDataSetChanged()
        }
        view.setOnClickListener {
            val calculation = calculationsList[pos]
            val app = (context.applicationContext as ZodiacCalculator)
            app.currentCalculationId = calculation.id  // ← Store in CustomApp
            val intent = Intent(context, EquationDashboardActivity::class.java)
            context.startActivity(intent)
        }

        val calculation = calculationsList[pos]
        title.text = calculation.title;
        dateCreated.text = calculation.dateCreated.toString()
        dateModified.text = calculation.dateModified.toString()
        return view
    }
}