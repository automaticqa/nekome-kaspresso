package com.chesire.nekome.kaspresso.screens.syncing

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.chesire.nekome.core.resources.R
import com.chesire.nekome.kaspresso.screens.BaseScreen
import io.github.kakaocup.compose.node.element.KNode

class SyncingScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : BaseScreen<SyncingScreen>(semanticsProvider) {

    val syncingTitle: KNode
        get() = child {
            hasText(R.string.syncing_syncing)
        }

    override fun screenIsDisplayed() {
        syncingTitle.assertIsDisplayed()
    }
}