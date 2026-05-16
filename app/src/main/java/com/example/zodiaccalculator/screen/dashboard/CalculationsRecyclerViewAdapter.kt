package com.example.zodiaccalculator.screen.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.screen.equationdashboard.EquationDashboardActivity

class CalculationsRecyclerViewAdapter(
    private val context: Context,
    private var calculationsList: MutableList<Calculation>
) : RecyclerView.Adapter<CalculationsRecyclerViewAdapter.CalculationViewHolder>() {

    // Inner ViewHolder class
    class CalculationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textviewTitle)
        val dateCreated: TextView = itemView.findViewById(R.id.textviewDateCreated)
        val dateModified: TextView = itemView.findViewById(R.id.textviewDateModified)
        val textviewDeleteCalculation: TextView = itemView.findViewById(R.id.textviewDeleteCalculation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalculationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.calculations_recycler_view, parent, false)
        return CalculationViewHolder(view)
    }
    override fun onBindViewHolder(holder: CalculationViewHolder, position: Int) {
        val calculation = calculationsList[position]

        holder.title.text = calculation.title
        holder.dateCreated.text = calculation.dateCreated.toString()
        holder.dateModified.text = calculation.dateModified.toString()

        holder.textviewDeleteCalculation.setOnClickListener {
            calculationsList.removeAt(position)
            (context as DashboardActivity).save();
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, calculationsList.size - position)
        }

        holder.itemView.setOnClickListener {
            val app = (context.applicationContext as ZodiacCalculator)
            app.currentCalculationId = calculation.id
            val intent = Intent(context, EquationDashboardActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = calculationsList.size

    fun updateList(newList: MutableList<Calculation>) {
        calculationsList = newList
        notifyDataSetChanged()
    }

    fun notifyChanges() {
        notifyDataSetChanged()
    }
}