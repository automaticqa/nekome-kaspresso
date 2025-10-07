package com.chesire.nekome.kaspresso.screens.dialog

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.chesire.nekome.core.compose.composables.DialogTags
import com.chesire.nekome.kaspresso.screens.BaseScreen
import com.chesire.nekome.kaspresso.utils.assertTextIsDisplayedInCompose
import com.chesire.nekome.resources.StringResource
import io.github.kakaocup.compose.node.element.KNode
import io.github.kakaocup.kakao.common.utilities.getResourceString

class DeleteSeriesDialogScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : BaseScreen<DeleteSeriesDialogScreen>(semanticsProvider) {

    val confirmButton: KNode
        get() = child {
            hasTestTag(DialogTags.OkButton)
        }

    val cancelButton: KNode
        get() = child {
            hasTestTag(DialogTags.CancelButton)
        }

    fun seriesDeleteTitleIsDisplayed(seriesTitle: String) {
        val deleteQuestion = getResourceString(StringResource.series_list_delete_title, seriesTitle)
        assertTextIsDisplayedInCompose(deleteQuestion)
    }

    override fun screenIsDisplayed() {
        confirmButton.assertIsDisplayed()
        cancelButton.assertIsDisplayed()
    }
}