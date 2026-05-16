package com.example.zodiaccalculator.data.models

class User(val username: String = "", val password: String = ""){
    var calculations : MutableList<Calculation> = mutableListOf();
    var calculationID : Int = 0;
}
