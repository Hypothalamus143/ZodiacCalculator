package com.example.zodiaccalculator.data.models

data class Variable(
    val id: String,
    var name: String,
    var type: VariableType,
    var value: Double? = null,
    var expression: String? = null,
    var isValid: Boolean = true  // ← ADD THIS LINE
)