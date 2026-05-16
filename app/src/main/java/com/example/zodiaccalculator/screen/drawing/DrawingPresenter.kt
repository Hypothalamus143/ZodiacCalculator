package com.example.zodiaccalculator.screen.drawing

class DrawingPresenter(
    private val view: DrawingContract.View,
    private val model: DrawingModel
) : DrawingContract.Presenter {

    override fun onBackClick() {
        view.navigateBack()
    }
}