package com.example.zodiaccalculator.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.zodiaccalculator.data.models.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUser(username: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun update(user: UserEntity)

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUser(username: String)

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun userExists(username: String): Boolean

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}