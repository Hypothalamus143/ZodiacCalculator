package com.example.zodiaccalculator.data.models

import java.time.LocalDateTime

class Calculation(
    val id: String,
    val title: String
) {
    var variables: List<Variable> = emptyList()
    var drawing: Drawing = Drawing()  // Always has a drawing with zero strokes
    val dateCreated: String = LocalDateTime.now().toString()
    var dateModified: String = LocalDateTime.now().toString()
}