package com.example.zodiaccalculator.screen.profile

import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.models.User

class ProfileModel(private val app: ZodiacCalculator) {
    fun getUserData(): User?{
        return app.currentUser;
    }
}