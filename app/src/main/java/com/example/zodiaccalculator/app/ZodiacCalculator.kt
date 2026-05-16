package com.example.zodiaccalculator.app

import android.app.Application
import android.util.Log
import com.example.zodiaccalculator.data.models.User
import com.example.zodiaccalculator.data.repositories.UserRepository

class ZodiacCalculator : Application() {
    var currentUser: User? = null;
    var currentCalculationId: String? = null  // ← Store current calculation here

    companion object {
        private lateinit var instance: ZodiacCalculator

        fun getInstance(): ZodiacCalculator {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.e("Custom App", "is running")
        UserRepository.initialize(this)
    }
}