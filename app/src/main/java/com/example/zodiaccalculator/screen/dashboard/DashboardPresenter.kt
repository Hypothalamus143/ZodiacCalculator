package com.example.zodiaccalculator.screen.dashboard


import com.example.zodiaccalculator.data.models.User
import com.example.zodiaccalculator.screen.profile.ProfileContract
import com.example.zodiaccalculator.screen.profile.ProfileModel

class DashboardPresenter(private val view: DashboardContract.View, private val model: DashboardModel) : DashboardContract.Presenter {
    override fun onAddCalculationsClicked(title: String) {
        if(title.isEmpty()){
            view.showError("Title cannot be empty")
            return
        }
        model.addCalculation(title)
        view.notifyChanges()
    }

    init {
       val calculations = model.getCalculations()
        if(calculations != null) view.setCalculationsListView(calculations)
    }
    // Add this function to your DashboardPresenter class
    fun onLogoutClicked() {
        // Clear any user session data if needed
        // For example: app().clearUserSession()
        view.logout();
        // Navigate to LoginActivity
    }
}