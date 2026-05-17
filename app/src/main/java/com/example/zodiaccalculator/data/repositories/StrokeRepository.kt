package com.example.zodiaccalculator.data.repositories

import com.example.zodiaccalculator.data.models.Calculation
import com.example.zodiaccalculator.data.models.Drawing
import com.example.zodiaccalculator.data.models.Stroke

object StrokeRepository {

    private var currentDrawing: Drawing = Drawing()

    private val observers = mutableListOf<StrokeObserver>()

    interface StrokeObserver {
        fun onDrawingChanged(drawing: Drawing)
        fun onStrokeAdded(stroke: Stroke)
        fun onStrokeRemoved(strokeId: String)
        fun onCanvasCleared()
    }

    fun addObserver(observer: StrokeObserver) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }

    fun removeObserver(observer: StrokeObserver) {
        observers.remove(observer)
    }

    fun getCurrentDrawing(): Drawing = currentDrawing

    fun getAllStrokes(): List<Stroke> = currentDrawing.strokes

    fun getStrokeCount(): Int = currentDrawing.strokes.size

    fun addStroke(stroke: Stroke): Boolean {
        return try {
            currentDrawing = Drawing(currentDrawing.strokes + stroke)
            notifyStrokeAdded(stroke)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun removeStroke(strokeId: String): Boolean {
        val strokeExists = currentDrawing.strokes.any { it.id == strokeId }
        return if (strokeExists) {
            currentDrawing = Drawing(currentDrawing.strokes.filter { it.id != strokeId })
            notifyStrokeRemoved(strokeId)
            true
        } else {
            false
        }
    }

    fun removeStrokeAtPoint(x: Float, y: Float, touchRadius: Float = 30f): String? {
        val strokeToRemove = currentDrawing.strokes.find { stroke ->
            isPointNearStroke(x, y, stroke, touchRadius)
        }
        return strokeToRemove?.let {
            removeStroke(it.id)
            it.id
        }
    }

    fun removeStrokesIntersectingLine(x1: Float, y1: Float, x2: Float, y2: Float, radius: Float = 30f): List<String> {
        val strokesToRemove = currentDrawing.strokes.filter { stroke ->
            isLineIntersectingStroke(x1, y1, x2, y2, stroke, radius)
        }

        val removedIds = strokesToRemove.map { it.id }
        currentDrawing = Drawing(currentDrawing.strokes.filter { stroke -> !removedIds.contains(stroke.id) })

        removedIds.forEach { notifyStrokeRemoved(it) }
        return removedIds
    }

    fun clearAllStrokes() {
        currentDrawing = Drawing()
        notifyCanvasCleared()
    }

    // Load drawing from a Calculation object (similar to VariableRepository.loadFromCalculation)
    fun loadFromCalculation(calculation: Calculation?) {
        if (calculation != null) {
            currentDrawing = calculation.drawing
        } else {
            currentDrawing = Drawing()  // Empty drawing with 0 strokes
        }
        notifyDrawingChanged(currentDrawing)
    }

    private fun isPointNearStroke(x: Float, y: Float, stroke: Stroke, radius: Float): Boolean {
        val path = stroke.toPath()
        val pathMeasure = android.graphics.PathMeasure(path, false)
        val point = FloatArray(2)

        var distance = 0f
        val pathLength = pathMeasure.length

        while (distance <= pathLength) {
            if (pathMeasure.getPosTan(distance, point, null)) {
                val dx = point[0] - x
                val dy = point[1] - y
                if (Math.hypot(dx.toDouble(), dy.toDouble()) <= radius) {
                    return true
                }
            }
            distance += 5f
        }
        return false
    }

    private fun isLineIntersectingStroke(x1: Float, y1: Float, x2: Float, y2: Float, stroke: Stroke, radius: Float): Boolean {
        val steps = 10
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val x = x1 + (x2 - x1) * t
            val y = y1 + (y2 - y1) * t
            if (isPointNearStroke(x, y, stroke, radius)) {
                return true
            }
        }
        return false
    }

    private fun notifyDrawingChanged(drawing: Drawing) {
        observers.forEach { it.onDrawingChanged(drawing) }
    }

    private fun notifyStrokeAdded(stroke: Stroke) {
        observers.forEach { it.onStrokeAdded(stroke) }
        observers.forEach { it.onDrawingChanged(currentDrawing) }
    }

    private fun notifyStrokeRemoved(strokeId: String) {
        observers.forEach { it.onStrokeRemoved(strokeId) }
        observers.forEach { it.onDrawingChanged(currentDrawing) }
    }

    private fun notifyCanvasCleared() {
        observers.forEach { it.onCanvasCleared() }
        observers.forEach { it.onDrawingChanged(currentDrawing) }
    }
}