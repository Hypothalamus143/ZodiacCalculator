package com.example.zodiaccalculator.screen.dashboard

import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.data.models.User

class DashboardContract {
    interface View{
        fun setCalculationsListView(calculations: MutableList<Calculation>)
        fun showError(message: String)
        fun notifyChanges()
//        fun navigateToDashboard()
//        fun setUserDetails(user: User);
//        fun navigateToLogin();
    }
    interface Presenter{
        fun onAddCalculationsClicked(title: String)
    }
}