package com.example.zodiaccalculator.screen.login

interface LoginContract {
    interface View{
        fun showSuccess()
        fun showError(message: String)
        fun navigateToRegister()
        fun navigateToDashboard(username : String)
    }
    interface Presenter{
        fun onLoginClicked(username: String, password: String)
        fun onRegisterClicked()
    }
}