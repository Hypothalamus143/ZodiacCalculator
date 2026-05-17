package com.example.zodiaccalculator.screen.drawing

class DrawingPresenter(
    private val view: DrawingContract.View,
    private val model: DrawingModel
) : DrawingContract.Presenter {

    fun loadCurrentCalculation() {
        model.loadCurrentCalculation()
    }

    private fun autoSave() {
        model.autoSaveCurrentCalculation()
    }

    override fun onClearClick() {
        model.clearAllStrokes()
        autoSave()  // Auto-save after clearing
        view.clearDrawing()
        view.showSuccess("Canvas cleared")
    }

    override fun onBackClick() {
        autoSave()  // Auto-save before leaving
        view.navigateBack()
    }

    // Called from DrawingView when a stroke is added
    override fun onStrokeAdded(stroke: com.example.zodiaccalculator.data.models.Stroke) {
        model.addStroke(stroke)
        autoSave()  // Auto-save after each stroke
    }

    // Called from DrawingView when a stroke is removed
    override fun onStrokeRemoved(strokeId: String) {
        model.removeStroke(strokeId)
        autoSave()  // Auto-save after erasing
    }
}