package com.example.zodiaccalculator.screen.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.screen.register.RegisterActivity
import com.example.zodiaccalculator.screen.dashboard.DashboardActivity
import com.example.zodiaccalculator.utils.Extensions.app
import com.example.zodiaccalculator.utils.Extensions.toastText

class LoginActivity : Activity(), LoginContract.View {
    private lateinit var presenter : LoginPresenter
    private lateinit var buttonLogin : Button
    private lateinit var textviewRegister: TextView
    private lateinit var edittextUsername : EditText
    private lateinit var edittextPassword : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        presenter = LoginPresenter(this, LoginModel(app()))
        edittextUsername = findViewById<EditText>(R.id.edittextUsername)
        edittextPassword = findViewById<EditText>(R.id.edittextPassword)
        buttonLogin = findViewById<Button>(R.id.buttonLogin)
        textviewRegister = findViewById<TextView>(R.id.textviewRegister)
        buttonLogin.setOnClickListener {
            presenter.onLoginClicked(edittextUsername.text.toString(), edittextPassword.text.toString())
        }
        textviewRegister = findViewById<Button>(R.id.textviewRegister)
        textviewRegister.setOnClickListener {
            presenter.onRegisterClicked()
        }
    }

    override fun showSuccess() {
        toastText("Login Successful");

    }
    override fun navigateToDashboard(username : String) {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish();
    }
    override fun showError(message: String) {
        toastText(message);
    }

    override fun navigateToRegister() {
        val intent  = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}