package com.example.zodiaccalculator.data.repositories

import android.graphics.Paint
import android.graphics.Path

object StrokeRepository {

    data class Stroke(
        val id: String,
        val path: Path,
        val paint: Paint,
        val strokeWidth: Float,
        val timestamp: Long = System.currentTimeMillis()
    )

    private val strokes = mutableListOf<Stroke>()

    // Observers for UI updates
    private val observers = mutableListOf<StrokeObserver>()

    interface StrokeObserver {
        fun onStrokesChanged(strokes: List<Stroke>)
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

    fun getAllStrokes(): List<Stroke> = strokes.toList()

    fun getStrokeCount(): Int = strokes.size

    fun addStroke(stroke: Stroke): Boolean {
        return try {
            strokes.add(stroke)
            notifyStrokeAdded(stroke)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun removeStroke(strokeId: String): Boolean {
        val index = strokes.indexOfFirst { it.id == strokeId }
        return if (index != -1) {
            strokes.removeAt(index)
            notifyStrokeRemoved(strokeId)
            true
        } else {
            false
        }
    }

    fun removeStrokeAtPoint(x: Float, y: Float, touchRadius: Float = 30f): String? {
        val strokeToRemove = strokes.find { stroke ->
            isPointNearPath(x, y, stroke.path, touchRadius)
        }
        return strokeToRemove?.let {
            removeStroke(it.id)
            it.id
        }
    }

    fun removeStrokesIntersectingLine(x1: Float, y1: Float, x2: Float, y2: Float, radius: Float = 30f): List<String> {
        val removedIds = mutableListOf<String>()
        val iterator = strokes.iterator()

        while (iterator.hasNext()) {
            val stroke = iterator.next()
            if (isLineIntersectingStroke(x1, y1, x2, y2, stroke.path, radius)) {
                iterator.remove()
                removedIds.add(stroke.id)
                notifyStrokeRemoved(stroke.id)
            }
        }

        return removedIds
    }

    fun clearAllStrokes() {
        strokes.clear()
        notifyCanvasCleared()
    }

    private fun isPointNearPath(x: Float, y: Float, path: Path, radius: Float): Boolean {
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

    private fun isLineIntersectingStroke(x1: Float, y1: Float, x2: Float, y2: Float, path: Path, radius: Float): Boolean {
        val steps = 10
        for (i in 0..steps) {
            val t = i.toFloat() / steps
            val x = x1 + (x2 - x1) * t
            val y = y1 + (y2 - y1) * t
            if (isPointNearPath(x, y, path, radius)) {
                return true
            }
        }
        return false
    }

    private fun notifyStrokeAdded(stroke: Stroke) {
        observers.forEach { it.onStrokeAdded(stroke) }
        observers.forEach { it.onStrokesChanged(getAllStrokes()) }
    }

    private fun notifyStrokeRemoved(strokeId: String) {
        observers.forEach { it.onStrokeRemoved(strokeId) }
        observers.forEach { it.onStrokesChanged(getAllStrokes()) }
    }

    private fun notifyCanvasCleared() {
        observers.forEach { it.onCanvasCleared() }
        observers.forEach { it.onStrokesChanged(getAllStrokes()) }
    }
}