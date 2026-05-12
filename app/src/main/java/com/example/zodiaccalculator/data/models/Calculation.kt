package com.example.zodiaccalculator.data.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

class Calculation(val title: String) {

    @RequiresApi(Build.VERSION_CODES.O)
    val dateCreated: LocalDateTime = LocalDateTime.now();
    @RequiresApi(Build.VERSION_CODES.O)
    var dateModified: LocalDateTime = LocalDateTime.now()
}