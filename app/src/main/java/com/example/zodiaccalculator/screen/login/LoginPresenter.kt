package com.example.zodiaccalculator.screen.login

class LoginPresenter(private val view: LoginContract.View, private val model: LoginModel) : LoginContract.Presenter {
    override fun onLoginClicked(username: String, password: String){
        if(username.isEmpty() || password.isEmpty()){
            view.showError("Fields cannot be empty")
            return;
        }
        val success = model.login(username, password)
        if(success){
            view.showSuccess()
            view.navigateToDashboard(username)
        }
        else view.showError("Invalid Credentials")
    }

    override fun onRegisterClicked() {
        view.navigateToRegister()
    }
}