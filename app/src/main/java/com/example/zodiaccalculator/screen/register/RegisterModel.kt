package com.example.zodiaccalculator.screen.register

import com.example.zodiaccalculator.data.repositories.UserRepository
class RegisterModel {
    fun register(username: String, password : String) : Boolean{
        return UserRepository.register(username, password)
    }
}