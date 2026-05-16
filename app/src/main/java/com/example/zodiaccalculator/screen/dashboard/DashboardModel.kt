package com.example.zodiaccalculator.screen.dashboard

import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.models.Calculation

class DashboardModel(private val app: ZodiacCalculator) {
    fun getCalculations(): MutableList<Calculation>?{
        return app.currentUser?.calculations;
    }
    fun addCalculation(title: String){
        app.currentUser?.calculations?.add(Calculation(app.currentUser?.calculationID++.toString(), title))
    }
}