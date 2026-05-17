package com.example.zodiaccalculator.screen.drawing

class DrawingContract {
    interface View {
        fun clearDrawing()
        fun navigateBack()
        fun showError(message: String)
        fun showSuccess(message: String)
    }

    interface Presenter {
        fun onClearClick()
        fun onBackClick()
        fun onStrokeAdded(stroke: com.example.zodiaccalculator.data.models.Stroke)
        fun onStrokeRemoved(strokeId: String)
    }
}