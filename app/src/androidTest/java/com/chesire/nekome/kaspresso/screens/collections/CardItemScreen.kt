package com.chesire.nekome.kaspresso.screens.collections

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.chesire.nekome.core.resources.R
import com.chesire.nekome.kaspresso.screens.BaseScreen
import com.chesire.nekome.kaspresso.utils.assertTextIsDisplayedInCompose
import com.chesire.nekome.resources.StringResource
import io.github.kakaocup.compose.node.element.KNode

class CardItemScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : BaseScreen<CardItemScreen>(semanticsProvider) {

    val backButton: KNode
        get() = child {
            hasContentDescription(StringResource.back)
        }

    val deleteButton: KNode
        get() = child {
            hasContentDescription(StringResource.series_detail_delete)
        }

    val currentStatusChip: KNode
        get() = child {
            hasText(R.string.filter_by_current)
        }

    val completedStatusChip: KNode
        get() = child {
            hasText(R.string.filter_by_completed)
        }

    val onHoldStatusChip: KNode
        get() = child {
            hasText(R.string.filter_by_on_hold)
        }

    val droppedStatusChip: KNode
        get() = child {
            hasText(R.string.filter_by_dropped)
        }

    val plannedStatusChip: KNode
        get() = child {
            hasText(R.string.filter_by_planned)
        }

    val progressTitle: KNode
        get() = child {
            hasText(R.string.series_detail_progress_title)
        }

    val ratingTitle: KNode
        get() = child {
            hasText(R.string.series_detail_rating)
        }

    val confirmButton: KNode
        get() = child {
            hasText(R.string.series_detail_confirm)
        }

    fun seriesCardIsDisplayedWithTitle(title: String) =
        assertTextIsDisplayedInCompose(title)

    override fun screenIsDisplayed() {
        deleteButton.assertIsDisplayed()
        progressTitle.assertIsDisplayed()
        confirmButton.assertIsDisplayed()
    }

    override fun allElementsAreDisplayed() {
        backButton.assertIsDisplayed()
        deleteButton.assertIsDisplayed()
        currentStatusChip.assertIsDisplayed()
        completedStatusChip.assertIsDisplayed()
        onHoldStatusChip.assertIsDisplayed()
        droppedStatusChip.assertIsDisplayed()
        plannedStatusChip.assertIsDisplayed()
        progressTitle.assertIsDisplayed()
        ratingTitle.assertIsDisplayed()
        confirmButton.assertIsDisplayed()
    }
}