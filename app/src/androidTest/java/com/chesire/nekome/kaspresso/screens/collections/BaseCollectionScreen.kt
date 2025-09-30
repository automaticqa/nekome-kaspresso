package com.chesire.nekome.kaspresso.screens.collections

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.chesire.nekome.app.series.collection.ui.SeriesCollectionTags
import com.chesire.nekome.kaspresso.screens.BaseScreen
import io.github.kakaocup.compose.node.element.KNode

abstract class BaseCollectionScreen<T : BaseCollectionScreen<T>>(
        semanticsProvider: SemanticsNodeInteractionsProvider
    ) : BaseScreen<T>(semanticsProvider) {

    open val appBarTitle: KNode
        get() = child {
            hasTestTag(SeriesCollectionTags.Title)
        }
}