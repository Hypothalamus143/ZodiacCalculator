package com.example.zodiaccalculator.screen.profile

import com.example.zodiaccalculator.data.models.User

class ProfileContract {
    interface View{
        fun navigateToDashboard()
        fun setUserDetails(user: User);
        fun navigateToLogin();
    }
    interface Presenter{
        fun onDashboardClicked()
    }
}