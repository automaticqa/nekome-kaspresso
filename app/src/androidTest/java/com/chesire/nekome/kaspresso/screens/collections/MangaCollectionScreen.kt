package com.chesire.nekome.kaspresso.screens.collections

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.chesire.nekome.core.resources.R

class MangaCollectionScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : BaseCollectionScreen<MangaCollectionScreen>(semanticsProvider) {

    override fun screenIsDisplayed() {
        appBarTitle.assertTextContains(R.string.nav_manga)
            .apply { assertIsDisplayed() }
    }
}