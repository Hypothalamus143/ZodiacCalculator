package com.example.zodiaccalculator.data.models

data class Variable(
    val id: String,
    var name: String,
    var expression: String,
    var value: Double? = null,
    var symbolicValue: String? = null,  // NEW: Store symbolic representation
    var isValid: Boolean = true,
    var hasUndefinedVariables: Boolean = false
)