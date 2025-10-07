package com.chesire.nekome.kaspresso.screens.navigation

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.chesire.nekome.kaspresso.screens.BaseScreen
import com.chesire.nekome.ui.MainActivityTags
import io.github.kakaocup.compose.node.element.KNode

abstract class BaseBottomNavigationScreen<T : BaseScreen<T>>(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : BaseScreen<T>(semanticsProvider) {

    val animeButton: KNode
        get() = child {
            hasTestTag(MainActivityTags.Anime)
        }

    val mangaButton: KNode
        get() = child {
            hasTestTag(MainActivityTags.Manga)
        }

    val settingsButton: KNode
        get() = child {
            hasTestTag(MainActivityTags.Settings)
        }
}