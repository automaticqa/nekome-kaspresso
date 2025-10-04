package com.chesire.nekome.kaspresso.screens.collections

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.chesire.nekome.app.series.collection.ui.SeriesCollectionTags
import com.chesire.nekome.kaspresso.screens.BaseScreen
import com.chesire.nekome.kaspresso.screens.navigation.BaseBottomNavigationScreen
import com.chesire.nekome.kaspresso.utils.assertNodeWithTextAndAncestorDisplayed
import com.chesire.nekome.kaspresso.utils.clickItemWithTitle
import io.github.kakaocup.compose.node.element.KNode

abstract class BaseCollectionScreen<T : BaseScreen<T>>(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : BaseBottomNavigationScreen<T>(semanticsProvider) {

    open val appBarTitle: KNode
        get() = child {
            hasTestTag(SeriesCollectionTags.SeriesCollectionTitle)
        }

    val menuFilterButton: KNode
        get() = child {
            hasTestTag(SeriesCollectionTags.MenuFilter)
        }

    val menuSortButton: KNode
        get() = child {
            hasTestTag(SeriesCollectionTags.MenuSort)
        }

    val menuRefreshButton: KNode
        get() = child {
            hasTestTag(SeriesCollectionTags.MenuSort)
        }

    val addNewButton: KNode
        get() = child {
            hasTestTag(SeriesCollectionTags.SearchFab)
        }

    val emptyView: KNode
        get() = child {
            hasTestTag(SeriesCollectionTags.EmptyView)
        }

    val seriesItem: KNode
        get() = child {
            hasTestTag(SeriesCollectionTags.SeriesItem)
        }

    fun assertSeriesWithTitleDisplayed(composeTestRule: ComposeTestRule, title: String) {
        assertNodeWithTextAndAncestorDisplayed(
            composeTestRule,
            title,
            SeriesCollectionTags.Root
        )
    }

    fun clickSeriesWithTitle(title: String) = clickItemWithTitle(title)
}