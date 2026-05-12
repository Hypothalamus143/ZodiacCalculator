package com.example.zodiaccalculator.app

import android.app.Application
import android.util.Log
import com.example.zodiaccalculator.data.models.User

class CustomApp : Application() {
    var currentUser: User? = null;
    override fun onCreate() {
        super.onCreate()
        Log.e("Custom App", "is running")
    }
}