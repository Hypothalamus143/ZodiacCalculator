package com.example.zodiaccalculator.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.repositories.UserRepository
import kotlinx.coroutines.launch

class RegisterModel(private val app : ZodiacCalculator) : ViewModel(){
    fun register(username: String, password : String) : Boolean{
        viewModelScope.launch {
            val success = UserRepository.register(username, password)
            if(success) app.currentUser = UserRepository.getUserData(username)
        }
        return app.currentUser != null
    }
}