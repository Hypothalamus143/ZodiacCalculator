package com.example.zodiaccalculator.screen.register

class RegisterContract {
    interface View{
        fun showSuccess()
        fun showError(message: String)
        fun navigateToLogin()
    }
    interface Presenter{
        fun onLoginClicked()
        fun onRegisterClicked(username: String, password: String, confirmPassword : String)
    }
}