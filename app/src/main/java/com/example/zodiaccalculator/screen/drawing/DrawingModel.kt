package com.example.zodiaccalculator.screen.drawing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zodiaccalculator.app.ZodiacCalculator
import com.example.zodiaccalculator.data.models.Drawing
import com.example.zodiaccalculator.data.repositories.StrokeRepository
import com.example.zodiaccalculator.data.repositories.UserRepository
import kotlinx.coroutines.launch

class DrawingModel(private val app: ZodiacCalculator) : ViewModel() {

    fun getCurrentDrawing(): Drawing = StrokeRepository.getCurrentDrawing()

    fun getStrokeCount(): Int = StrokeRepository.getStrokeCount()

    fun addStroke(stroke: com.example.zodiaccalculator.data.models.Stroke): Boolean {
        return StrokeRepository.addStroke(stroke)
    }

    fun removeStroke(strokeId: String): Boolean {
        return StrokeRepository.removeStroke(strokeId)
    }

    fun clearAllStrokes() {
        StrokeRepository.clearAllStrokes()
    }

    // ========== CALCULATION METHODS (following the same pattern as EquationDashboardModel) ==========

    fun getCurrentCalculationId(): String? {
        return app.currentCalculationId
    }

    fun loadCurrentCalculation() {
        val calculationId = app.currentCalculationId
        val calculation = app.currentUser?.calculations?.find { it.id == calculationId }
        StrokeRepository.loadFromCalculation(calculation)
    }

    fun autoSaveCurrentCalculation() {
        val calculationId = app.currentCalculationId
        if (calculationId != null) {
            val currentDrawing = getCurrentDrawing()
            val calculation = app.currentUser?.calculations?.find { it.id == calculationId }
            calculation?.let {
                it.drawing = currentDrawing  // Save the current drawing
                it.dateModified = java.time.LocalDateTime.now().toString()
            }
        }
        viewModelScope.launch {
            UserRepository.saveUser(app.currentUser!!)
        }
    }
}