package com.example.zodiaccalculator.data.models

import android.graphics.Paint
import android.graphics.Path
import java.util.UUID

data class Stroke(
    val id: String = UUID.randomUUID().toString(),
    val strokeWidth: Float,
    val color: Int,
    val points: List<Point>
) {
    fun toPath(): Path {
        val path = Path()
        if (points.isNotEmpty()) {
            path.moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }
        }
        return path
    }

    fun toPaint(): Paint {
        return Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = strokeWidth
            color = color
        }
    }
}

data class Point(
    val x: Float,
    val y: Float
)