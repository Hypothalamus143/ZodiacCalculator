package com.example.zodiaccalculator.data.models

data class Equation(
    val id: String,
    var name: String,
    var expression: String,
    var currentResult: Double? = null
)