package com.example.zodiaccalculator.screen.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.example.zodiaccalculator.data.models.Point
import com.example.zodiaccalculator.data.models.Stroke
import com.example.zodiaccalculator.data.repositories.StrokeRepository
import java.util.*

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), StrokeRepository.StrokeObserver {

    interface OnStrokeListener {
        fun onStrokeAdded(stroke: Stroke)
        fun onStrokeRemoved(strokeId: String)
    }

    private var strokeListener: OnStrokeListener? = null

    fun setOnStrokeListener(listener: OnStrokeListener) {
        this.strokeListener = listener
    }

    private var currentPoints = mutableListOf<Point>()
    private val strokeColor = Color.BLACK
    private val strokeWidth = 20f
    private var currentTool = Tool.PEN

    enum class Tool {
        PEN, ERASER, HAND
    }

    private var canvasBitmap: Bitmap? = null
    private var canvasPaint: Paint? = null

    private var canvasOffsetX = 0f
    private var canvasOffsetY = 0f
    private var canvasScale = 1f

    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activePointerId = -1

    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private var lastEraseX = 0f
    private var lastEraseY = 0f
    private var isErasing = false

    private val tempPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = strokeWidth
        color = strokeColor
    }

    init {
        setupDrawing()
        setupGestures()
        StrokeRepository.addObserver(this)
    }

    private fun setupDrawing() {
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    private fun setupGestures() {
        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val prevScale = canvasScale
                val newScale = (canvasScale * detector.scaleFactor).coerceIn(0.5f, 3f)

                if (newScale != prevScale) {
                    val focalX = detector.focusX
                    val focalY = detector.focusY

                    canvasOffsetX = (canvasOffsetX - focalX) * (newScale / prevScale) + focalX
                    canvasOffsetY = (canvasOffsetY - focalY) * (newScale / prevScale) + focalY
                    canvasScale = newScale

                    invalidate()
                }
                return true
            }

            override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                return true
            }
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap?.recycle()
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        redrawAllStrokes()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(canvasOffsetX, canvasOffsetY)
        canvas.scale(canvasScale, canvasScale)

        canvasBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, 0f, 0f, canvasPaint)
        }

        if (currentPoints.size > 1 && currentTool == Tool.PEN) {
            val path = pointsToPath(currentPoints)
            canvas.drawPath(path, tempPaint)
        }

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)

        val canvasPoint = screenToCanvas(event.x, event.y)

        when (currentTool) {
            Tool.PEN -> handlePenTool(event, canvasPoint)
            Tool.ERASER -> handleEraserTool(event, canvasPoint)
            Tool.HAND -> handleHandTool(event)
        }

        return true
    }

    private fun handlePenTool(event: MotionEvent, canvasPoint: PointF) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                currentPoints.clear()
                currentPoints.add(Point(canvasPoint.x, canvasPoint.y))
                invalidate()
            }

            MotionEvent.ACTION_MOVE -> {
                currentPoints.add(Point(canvasPoint.x, canvasPoint.y))
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (currentPoints.size > 1) {
                    val stroke = Stroke(
                        strokeWidth = strokeWidth,
                        color = strokeColor,
                        points = currentPoints.toList()
                    )
                    StrokeRepository.addStroke(stroke)
                    strokeListener?.onStrokeAdded(stroke)
                    drawStrokeToBitmap(stroke)
                }
                currentPoints.clear()
                invalidate()
            }
        }
    }

    private fun handleEraserTool(event: MotionEvent, canvasPoint: PointF) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isErasing = true
                lastEraseX = canvasPoint.x
                lastEraseY = canvasPoint.y
                StrokeRepository.removeStrokeAtPoint(canvasPoint.x, canvasPoint.y)
            }

            MotionEvent.ACTION_MOVE -> {
                if (isErasing) {
                    val removedIds = StrokeRepository.removeStrokesIntersectingLine(
                        lastEraseX, lastEraseY, canvasPoint.x, canvasPoint.y
                    )
                    removedIds.forEach { strokeListener?.onStrokeRemoved(it) }
                    lastEraseX = canvasPoint.x
                    lastEraseY = canvasPoint.y
                }
            }

            MotionEvent.ACTION_UP -> {
                isErasing = false
            }
        }
    }

    private fun handleHandTool(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
                lastTouchY = event.y
                activePointerId = event.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerId)
                if (pointerIndex != -1) {
                    val dx = event.getX(pointerIndex) - lastTouchX
                    val dy = event.getY(pointerIndex) - lastTouchY

                    canvasOffsetX += dx
                    canvasOffsetY += dy

                    lastTouchX = event.getX(pointerIndex)
                    lastTouchY = event.getY(pointerIndex)
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                activePointerId = -1
            }
        }
    }

    private fun screenToCanvas(screenX: Float, screenY: Float): PointF {
        val canvasX = (screenX - canvasOffsetX) / canvasScale
        val canvasY = (screenY - canvasOffsetY) / canvasScale
        return PointF(canvasX, canvasY)
    }

    private fun pointsToPath(points: List<Point>): Path {
        val path = Path()
        if (points.isNotEmpty()) {
            path.moveTo(points[0].x, points[0].y)
            for (i in 1 until points.size) {
                path.lineTo(points[i].x, points[i].y)
            }
        }
        return path
    }

    private fun drawStrokeToBitmap(stroke: Stroke) {
        canvasBitmap?.let { bitmap ->
            val canvas = Canvas(bitmap)
            val path = stroke.toPath()
            val paint = stroke.toPaint()
            canvas.drawPath(path, paint)
        }
    }

    private fun redrawAllStrokes() {
        canvasBitmap?.eraseColor(Color.TRANSPARENT)
        canvasBitmap?.let { bitmap ->
            val canvas = Canvas(bitmap)
            StrokeRepository.getAllStrokes().forEach { stroke ->
                val path = stroke.toPath()
                val paint = stroke.toPaint()
                canvas.drawPath(path, paint)
            }
        }
        invalidate()
    }

    override fun onDrawingChanged(drawing: com.example.zodiaccalculator.data.models.Drawing) {
        redrawAllStrokes()
    }

    override fun onStrokeAdded(stroke: Stroke) {}

    override fun onStrokeRemoved(strokeId: String) {
        redrawAllStrokes()
    }

    override fun onCanvasCleared() {
        redrawAllStrokes()
    }

    fun clearCanvas() {
        StrokeRepository.clearAllStrokes()
        StrokeRepository.getAllStrokes().forEach { stroke ->
            strokeListener?.onStrokeRemoved(stroke.id)
        }
    }

    fun setPenMode() {
        currentTool = Tool.PEN
    }

    fun setEraserMode() {
        currentTool = Tool.ERASER
    }

    fun setHandMode() {
        currentTool = Tool.HAND
    }

    fun resetView() {
        canvasScale = 1f
        canvasOffsetX = 0f
        canvasOffsetY = 0f
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        StrokeRepository.removeObserver(this)
    }
}