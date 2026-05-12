package com.example.zodiaccalculator.data.repositories

import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.data.models.User

object UserRepository {
    private val users = mutableMapOf<String, User>()

    fun register(username: String, password : String): Boolean{
        if(users.containsKey(username)) return false;
        val user: User = User(username, password)
//        user.calculations.add(Calculation("first"));
        users.put(username, user);
        return true;
    }

    fun login(username: String, password : String): Boolean{
        if(users.containsKey(username) && users[username]?.password == password) return true;
        return false;
    }

    fun getUserData(username: String): User?{
        return users.get(username);
    }
}