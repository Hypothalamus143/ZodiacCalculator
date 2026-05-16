package com.example.zodiaccalculator.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.repositories.UserRepository
import kotlinx.coroutines.launch

class LoginModel(private val app: ZodiacCalculator) : ViewModel(){
    fun login(username:String, password:String) : Boolean{
        viewModelScope.launch {
            val success = UserRepository.login(username, password)
            if(success) app.currentUser = UserRepository.getUserData(username)
        }
    return app.currentUser != null
    }
}