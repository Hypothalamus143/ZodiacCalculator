package com.example.zodiaccalculator.screen.profile

import com.example.zodiaccalculator.app.CustomApp
import com.example.zodiaccalculator.data.models.User
import com.example.zodiaccalculator.data.repositories.UserRepository
class ProfileModel(private val app: CustomApp) {
    fun getUserData(): User?{
        return app.currentUser;
    }
}