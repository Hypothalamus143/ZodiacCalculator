package com.example.zodiaccalculator.data.models

data class Variable(
    val id: String,
    var name: String,
    var expression: String,
    var value: Double? = null,
    var isValid: Boolean = true
)