package com.example.zodiaccalculator.screen.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.repositories.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RegisterModel(private val app : ZodiacCalculator) : ViewModel(){
    fun register(username: String, password : String) : Boolean{
        return runBlocking {
            val success = UserRepository.register(username, password)
            success
        }
    }
    fun deleteAllUsers(){
        return runBlocking {  UserRepository.deleteAllUserData()}
    }
}