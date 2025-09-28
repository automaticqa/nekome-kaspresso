package com.chesire.nekome.kaspresso.screens

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import io.github.kakaocup.compose.node.element.ComposeScreen

abstract class BaseScreen<T : BaseScreen<T>>(semanticsProvider: SemanticsNodeInteractionsProvider) :
    ComposeScreen<T>(semanticsProvider) {

    abstract fun screenIsDisplayed()

    open fun allElementsAreDisplayed() {
        throw NotImplementedError(
            "${this::class.simpleName}: 'allElementsAreDisplayed' is not implemented. Please implement it in the screen class if needed."
        )
    }
}