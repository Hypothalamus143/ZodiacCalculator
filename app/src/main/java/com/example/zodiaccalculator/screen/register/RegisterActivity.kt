package com.example.zodiaccalculator.screen.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.screen.login.LoginActivity
import com.example.zodiaccalculator.utils.Extensions.app
import com.example.zodiaccalculator.utils.Extensions.toastText

class RegisterActivity : Activity(), RegisterContract.View{
    private lateinit var presenter : RegisterPresenter
    private lateinit var buttonCreateAccount: Button
    private lateinit var edittextUsername : EditText
    private lateinit var edittextPassword : EditText
    private lateinit var edittextConfirmPassword : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        presenter = RegisterPresenter(this, RegisterModel(app()))
        edittextUsername = findViewById<EditText>(R.id.edittextUsername)
        edittextPassword = findViewById<EditText>(R.id.edittextPassword)
        edittextConfirmPassword = findViewById<EditText>(R.id.edittextConfirmPassword)
        buttonCreateAccount = findViewById<Button>(R.id.buttonCreateAccount)
        buttonCreateAccount.setOnClickListener(){
            presenter.onRegisterClicked(edittextUsername.text.toString(), edittextPassword.text.toString(), edittextConfirmPassword.text.toString())
        }
    }

    override fun showSuccess() {
        toastText("Registration Successful")
    }

    override fun showError(message: String) {
        toastText(message)
    }

    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}