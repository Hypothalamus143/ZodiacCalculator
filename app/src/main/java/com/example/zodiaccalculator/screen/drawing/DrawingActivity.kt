package com.example.zodiaccalculator.screen.drawing

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.utils.Extensions.app

class DrawingActivity : Activity(), DrawingContract.View {

    private lateinit var presenter: DrawingPresenter
    private lateinit var drawingView: DrawingView
    private lateinit var buttonPen: Button
    private lateinit var buttonEraser: Button
    private lateinit var buttonHand: Button
    private lateinit var buttonClear: Button
    private lateinit var buttonResetView: Button
    private lateinit var buttonBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        drawingView = findViewById(R.id.drawingView)
        buttonPen = findViewById(R.id.buttonPen)
        buttonEraser = findViewById(R.id.buttonEraser)
        buttonHand = findViewById(R.id.buttonHand)
        buttonClear = findViewById(R.id.buttonClear)
        buttonResetView = findViewById(R.id.buttonResetView)
        buttonBack = findViewById(R.id.buttonBack)

        val app = app()
        val model = DrawingModel(app)
        presenter = DrawingPresenter(this, model)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        buttonPen.setOnClickListener {
            drawingView.setPenMode()
            showSuccess("Pen mode")
        }

        buttonEraser.setOnClickListener {
            drawingView.setEraserMode()
            showSuccess("Eraser mode - tap/drag on strokes to delete")
        }

        buttonHand.setOnClickListener {
            drawingView.setHandMode()
            showSuccess("Hand mode - drag to pan, pinch to zoom")
        }

        buttonClear.setOnClickListener {
            drawingView.clearCanvas()
            showSuccess("Canvas cleared")
        }

        buttonResetView.setOnClickListener {
            drawingView.resetView()
            showSuccess("View reset")
        }

        buttonBack.setOnClickListener {
            presenter.onBackClick()
        }
    }

    override fun clearDrawing() {
        drawingView.clearCanvas()
    }

    override fun navigateBack() {
        finish()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSuccess(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}