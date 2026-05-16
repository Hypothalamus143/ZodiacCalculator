package com.example.zodiaccalculator.utils.Extensions

import android.app.Activity
import android.widget.Toast
import com.example.zodiaccalculator.app.ZodiacCalculator

fun Activity.app(): ZodiacCalculator = application as ZodiacCalculator
fun Activity.toastText(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}