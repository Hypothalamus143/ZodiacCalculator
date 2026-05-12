package com.example.zodiaccalculator.screen.dashboard

import com.example.zodiaccalculator.app.CustomApp
import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.data.models.User

class DashboardModel(private val app: CustomApp) {
    fun getCalculations(): MutableList<Calculation>?{
        return app.currentUser?.calculations;
    }
    fun addCalculation(title: String){
        app.currentUser?.calculations?.add(Calculation(title))
    }
}