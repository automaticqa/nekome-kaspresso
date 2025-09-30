package com.chesire.nekome.kaspresso.screens.collections

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.chesire.nekome.core.resources.R

class AnimeCollectionScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : BaseCollectionScreen<AnimeCollectionScreen>(semanticsProvider) {

    override fun screenIsDisplayed() {
        appBarTitle.assertTextContains(R.string.nav_anime)
            .apply { assertIsDisplayed() }
    }
}