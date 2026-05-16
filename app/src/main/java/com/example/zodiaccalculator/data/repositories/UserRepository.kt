package com.example.zodiaccalculator.data.repositories

import android.content.Context
import com.example.zodiaccalculator.data.database.AppDatabase
import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.data.models.User
import com.example.zodiaccalculator.data.models.UserData
import com.example.zodiaccalculator.data.models.UserEntity
import com.example.zodiaccalculator.data.database.UserDao
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UserRepository {
    private lateinit var dao: UserDao
    private val gson = Gson()

    // Call this once in your Application class or MainActivity
    fun initialize(context: Context) {
        val database = AppDatabase.getInstance(context)
        dao = database.userDao()
    }

    suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO) {
            val userData = UserData(
                calculations = user.calculations.toList(),
                calculationID = user.calculationID
            )

            val serializedData = gson.toJson(userData)

            val entity = UserEntity(
                username = user.username,
                password = user.password,
                serializedData = serializedData
            )

            dao.insert(entity)
        }
    }

    private suspend fun loadUser(username: String): User? {
        return withContext(Dispatchers.IO) {
            val entity = dao.getUser(username) ?: return@withContext null

            val userData = gson.fromJson(entity.serializedData, UserData::class.java)

            val user = User(
                username = entity.username,
                password = entity.password
            )
            user.calculations = userData.calculations.toMutableList()
            user.calculationID = userData.calculationID

            return@withContext user
        }
    }

    suspend fun register(username: String, password: String): Boolean {
        return try {
            if (dao.userExists(username)) return false
            val user = User(username, password)
            saveUser(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(username: String, password: String): Boolean {
        val user = loadUser(username)
        return user != null && user.password == password
    }

    suspend fun getUserData(username: String): User? {
        return loadUser(username)
    }
    suspend fun deleteAllUserData() {
        withContext(Dispatchers.IO) {
            dao.deleteAllUsers()
        }
    }
}