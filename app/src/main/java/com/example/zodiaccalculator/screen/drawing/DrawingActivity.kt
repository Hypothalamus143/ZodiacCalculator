package com.example.zodiaccalculator.screen.drawing

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.zodiaccalculator.R
import com.example.zodiaccalculator.data.models.Stroke
import com.example.zodiaccalculator.utils.Extensions.app
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DrawingActivity : Activity(), DrawingContract.View {

    private lateinit var presenter: DrawingPresenter
    private lateinit var drawingView: DrawingView
    private lateinit var buttonPen: Button
    private lateinit var buttonEraser: Button
    private lateinit var buttonHand: Button
    private lateinit var buttonClear: Button
    private lateinit var buttonResetView: FloatingActionButton
    private lateinit var buttonBack: ImageView
    private lateinit var textViewToolMode: TextView

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
        textViewToolMode = findViewById(R.id.textViewToolMode)

        val app = app()
        val model = DrawingModel(app)
        presenter = DrawingPresenter(this, model)

        // Load current calculation's drawing (same pattern as EquationDashboardActivity)
        presenter.loadCurrentCalculation()

        setupClickListeners()

        // Set up drawing view callback to notify presenter of changes
        drawingView.setOnStrokeListener(object : DrawingView.OnStrokeListener {
            override fun onStrokeAdded(stroke: Stroke) {
                presenter.onStrokeAdded(stroke)
            }

            override fun onStrokeRemoved(strokeId: String) {
                presenter.onStrokeRemoved(strokeId)
            }
        })
    }

    private fun setupClickListeners() {
        buttonPen.setOnClickListener {
            drawingView.setPenMode()
            textViewToolMode.text="Pen"
        }

        buttonEraser.setOnClickListener {
            drawingView.setEraserMode()
            textViewToolMode.text="Eraser"
        }

        buttonHand.setOnClickListener {
            drawingView.setHandMode()
            textViewToolMode.text="Pan"
        }

        buttonClear.setOnClickListener {
            presenter.onClearClick()
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