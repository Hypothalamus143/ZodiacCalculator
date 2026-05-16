package com.example.zodiaccalculator.data.models

data class UserData(
    val calculations: List<Calculation>,  // List, not MutableList
    val calculationID: Int
)