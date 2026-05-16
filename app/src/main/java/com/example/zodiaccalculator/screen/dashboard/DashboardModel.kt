package com.example.zodiaccalculator.screen.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.data.repositories.UserRepository
import kotlinx.coroutines.launch

class DashboardModel(private val app: ZodiacCalculator) : ViewModel(){
    fun getCalculations(): MutableList<Calculation>?{
        return app.currentUser?.calculations;
    }
    fun addCalculation(title: String){
        app.currentUser?.calculations?.add(Calculation(app.currentUser?.calculationID++.toString(), title))
    }
    fun autoSave(){
        viewModelScope.launch{
            UserRepository.saveUser(app.currentUser!!)
        }
    }
}