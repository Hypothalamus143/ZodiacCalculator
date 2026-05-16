package com.example.zodiaccalculator.screen.register

class RegisterPresenter(private val view : RegisterContract.View, private val model : RegisterModel) : RegisterContract.Presenter{
    override fun onLoginClicked() {
        view.navigateToLogin()
    }
    override fun onRegisterClicked(
        username: String,
        password: String,
        confirmPassword: String
    ) {
        if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())view.showError("Fields cannot be empty")
        else if (password != confirmPassword)  view.showError("Passwords do not match")
        else {
            val success = model.register(username, password)
            if(success) {
                view.showSuccess()
                view.navigateToLogin()
            }
            else view.showError("Username already exists")
        }

    }
    fun onLogoClicked(){
        model.deleteAllUsers();
        view.showError("User details have been erased")
    }

}