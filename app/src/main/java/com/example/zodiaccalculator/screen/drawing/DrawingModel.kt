package com.example.zodiaccalculator.screen.drawing

import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.models.Stroke
import com.example.zodiaccalculator.data.repositories.StrokeRepository

class DrawingModel(private val app: ZodiacCalculator) {

    fun getStrokeCount(): Int {
        return StrokeRepository.getStrokeCount()
    }

    fun getAllStrokes(): List<Stroke> {
        return StrokeRepository.getAllStrokes()
    }

    fun clearAllStrokes() {
        StrokeRepository.clearAllStrokes()
    }

    // Future: Save/load from database
    fun saveToDatabase() {
        // TODO: Save strokes to database when ready
    }

    fun loadFromDatabase() {
        // TODO: Load strokes from database when ready
    }
}