package com.example.zodiaccalculator.screen.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.example.zodiaccalculator.data.repositories.StrokeRepository
import java.util.*

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), StrokeRepository.StrokeObserver {

    // Drawing variables
    private var currentPath: Path? = null
    private var currentPaint: Paint? = null
    private var currentStrokeId: String? = null

    private val strokeColor = Color.BLACK
    private val strokeWidth = 20f
    private var currentTool = Tool.PEN

    enum class Tool {
        PEN, ERASER, HAND
    }

    // Canvas and bitmap for persistence (stores at original scale, no transformations)
    private var canvasBitmap: Bitmap? = null
    private var canvasPaint: Paint? = null

    // Pan & Zoom variables
    private var scaleFactor = 1f
    private var minScale = 0.5f
    private var maxScale = 3f
    private var translateX = 0f
    private var translateY = 0f

    // Touch handling for pan
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activePointerId = -1

    // Gesture detectors
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    // For continuous erasing (in canvas coordinates)
    private var lastEraseX = 0f
    private var lastEraseY = 0f
    private var isErasing = false

    // Matrix for converting screen coordinates to canvas coordinates
    private val transformMatrix = Matrix()
    private val inverseMatrix = Matrix()

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
                scaleFactor *= detector.scaleFactor
                scaleFactor = scaleFactor.coerceIn(minScale, maxScale)
                invalidate()
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

        // Save the canvas state
        canvas.save()

        // Apply transformations for view only (doesn't affect stored bitmap)
        canvas.translate(translateX, translateY)
        canvas.scale(scaleFactor, scaleFactor)

        // Draw the bitmap (which is already at original scale)
        canvasBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, 0f, 0f, canvasPaint)
        }

        // Draw current stroke while drawing (also transformed)
        currentPath?.let { path ->
            currentPaint?.let { paint ->
                canvas.drawPath(path, paint)
            }
        }

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Handle scale gesture first
        scaleGestureDetector.onTouchEvent(event)

        // Update matrices for coordinate conversion
        updateMatrices()

        // Convert screen coordinates to canvas coordinates (where strokes actually live)
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
                startNewStroke(canvasPoint.x, canvasPoint.y)
            }

            MotionEvent.ACTION_MOVE -> {
                currentPath?.lineTo(canvasPoint.x, canvasPoint.y)
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                if (currentPath != null && currentPaint != null && currentStrokeId != null) {
                    val stroke = StrokeRepository.Stroke(
                        id = currentStrokeId!!,
                        path = currentPath!!,
                        paint = currentPaint!!,
                        strokeWidth = strokeWidth
                    )
                    StrokeRepository.addStroke(stroke)
                    drawStrokeToBitmap(stroke)

                    currentPath = null
                    currentPaint = null
                    currentStrokeId = null
                    invalidate()
                }
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
                    StrokeRepository.removeStrokesIntersectingLine(
                        lastEraseX, lastEraseY, canvasPoint.x, canvasPoint.y
                    )
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

                    translateX += dx
                    translateY += dy

                    // Optional bounds to prevent panning too far
                    val maxPanX = width * (scaleFactor - 1) / 2
                    val maxPanY = height * (scaleFactor - 1) / 2
                    translateX = translateX.coerceIn(-maxPanX, maxPanX)
                    translateY = translateY.coerceIn(-maxPanY, maxPanY)

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

    private fun startNewStroke(x: Float, y: Float) {
        currentPath = Path()
        currentPath?.moveTo(x, y)
        currentPath?.lineTo(x, y)

        currentPaint = Paint().apply {
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = strokeWidth
            color = strokeColor
        }

        currentStrokeId = UUID.randomUUID().toString()
        invalidate()
    }

    // Convert screen coordinates to actual canvas coordinates (accounting for pan and zoom)
    private fun screenToCanvas(screenX: Float, screenY: Float): PointF {
        val point = floatArrayOf(screenX, screenY)
        inverseMatrix.mapPoints(point)
        return PointF(point[0], point[1])
    }

    // Update transformation matrices
    private fun updateMatrices() {
        transformMatrix.reset()
        transformMatrix.postTranslate(translateX, translateY)
        transformMatrix.postScale(scaleFactor, scaleFactor)
        transformMatrix.invert(inverseMatrix)
    }

    private fun drawStrokeToBitmap(stroke: StrokeRepository.Stroke) {
        canvasBitmap?.let { bitmap ->
            val canvas = Canvas(bitmap)
            canvas.drawPath(stroke.path, stroke.paint)
        }
    }

    private fun redrawAllStrokes() {
        canvasBitmap?.eraseColor(Color.TRANSPARENT)
        canvasBitmap?.let { bitmap ->
            val canvas = Canvas(bitmap)
            StrokeRepository.getAllStrokes().forEach { stroke ->
                canvas.drawPath(stroke.path, stroke.paint)
            }
        }
        invalidate()
    }

    // Observer callbacks
    override fun onStrokesChanged(strokes: List<StrokeRepository.Stroke>) {
        redrawAllStrokes()
    }

    override fun onStrokeAdded(stroke: StrokeRepository.Stroke) {}

    override fun onStrokeRemoved(strokeId: String) {
        redrawAllStrokes()
    }

    override fun onCanvasCleared() {
        redrawAllStrokes()
    }

    // Public methods for toolbar
    fun clearCanvas() {
        StrokeRepository.clearAllStrokes()
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
        scaleFactor = 1f
        translateX = 0f
        translateY = 0f
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        StrokeRepository.removeObserver(this)
    }
}