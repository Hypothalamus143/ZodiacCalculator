package com.example.zodiaccalculator.data.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

class Calculation(
    val id: String,
    val title: String){
    var variables: List<Variable> = emptyList()  // Add this
    val dateCreated: String = LocalDateTime.now().toString()
    var dateModified: String = LocalDateTime.now().toString()
}
