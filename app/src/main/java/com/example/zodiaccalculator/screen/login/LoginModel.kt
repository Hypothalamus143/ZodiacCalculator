package com.example.zodiaccalculator.screen.login

import com.example.zodiaccalculator.app.CustomApp
import com.example.zodiaccalculator.data.repositories.UserRepository
class LoginModel(private val app: CustomApp) {
    fun login(username:String, password:String) : Boolean{
        val success = UserRepository.login(username, password)
        if(success) app.currentUser = UserRepository.getUserData(username)
        return success;
    }
}