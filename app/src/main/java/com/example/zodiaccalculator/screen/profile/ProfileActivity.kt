package com.example.zodiaccalculator.screen.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.data.models.User
import com.example.zodiaccalculator.screen.dashboard.DashboardActivity
import com.example.zodiaccalculator.screen.login.LoginActivity
import com.example.zodiaccalculator.screen.login.LoginModel
import com.example.zodiaccalculator.screen.login.LoginPresenter
import com.example.zodiaccalculator.utils.Extensions.app

class ProfileActivity : Activity(), ProfileContract.View {
    private lateinit var presenter : ProfilePresenter
    private lateinit var buttonDashboard : Button
    private lateinit var textviewUsername: TextView
    private lateinit var textviewFirstName: TextView
    private lateinit var textviewMiddleName: TextView
    private lateinit var textviewLastName: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        textviewUsername = findViewById<TextView>(R.id.textviewUsername)
        textviewFirstName = findViewById<TextView>(R.id.textviewFirstName)
        textviewLastName = findViewById<TextView>(R.id.textviewLastName)
        textviewMiddleName = findViewById<TextView>(R.id.textviewMiddleName)
        presenter = ProfilePresenter(this, ProfileModel(app()))
        buttonDashboard = findViewById<Button>(R.id.buttonDashboard)
        buttonDashboard.setOnClickListener {
            presenter.onDashboardClicked();
        }
    }
    override fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }
    override fun setUserDetails(
        user: User
    ) {
        textviewUsername.text = user.username;
    }
    override fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}