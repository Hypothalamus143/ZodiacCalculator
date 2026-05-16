package com.example.zodiaccalculator.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val password: String,
    val serializedData: String  // This holds calculations + calculationID as JSON
)