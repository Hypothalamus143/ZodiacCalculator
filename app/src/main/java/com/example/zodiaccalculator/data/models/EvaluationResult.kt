package com.example.zodiaccalculator.data.models

data class EvaluationResult(
    val equationId: String,
    val equationName: String,
    val expression: String,
    val result: Double?,
    val error: String?
)