package com.chesire.nekome.kaspresso.screens.search

import android.content.Context
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.chesire.nekome.app.search.search.ui.SearchTags
import com.chesire.nekome.kaspresso.screens.BaseScreen
import io.github.kakaocup.compose.node.element.KNode
import com.chesire.nekome.core.resources.R
import com.chesire.nekome.resources.StringResource

class SearchSeriesScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : BaseScreen<SearchSeriesScreen>(semanticsProvider) {

    val searchInput: KNode
        get() = child {
            hasTestTag(SearchTags.Input)
            hasText(R.string.search_series_title)
        }

    val searchAnimeButton: KNode
        get() = child {
            hasTestTag(SearchTags.Anime)
            hasText(R.string.nav_anime)
        }

    val searchMangaButton: KNode
        get() = child {
            hasTestTag(SearchTags.Manga)
            hasText(R.string.nav_manga)
        }

    val searchSearchButton: KNode
        get() = child {
            hasTestTag(SearchTags.Search)
            hasText(R.string.nav_search)
        }

    val resultsList: KNode
        get() = child {
            hasTestTag(SearchTags.ResultsList)
        }

    fun clickAddButtonForFirstResultWithTitle(composeTestRule: ComposeTestRule, title: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val addButtonDescription = context.getString(StringResource.results_track_series)

        composeTestRule
            .onAllNodes(
                hasContentDescription(addButtonDescription)
                    .and(
                        hasAnyAncestor(
                            hasAnyDescendant(hasText(title, substring = true))
                        )
                    )
            )
            .onFirst()
            .performClick()
    }

    fun assertAddButtonGoneByItemId(rule: ComposeTestRule, itemId: Int) {
        rule.onNode(hasTestTag("${SearchTags.ResultItemAddButton}_$itemId"))
            .assertDoesNotExist()
    }

    fun assertResultAtPositionContainsTitle(
        composeTestRule: ComposeTestRule,
        position: Int,
        title: String
    ) {
        composeTestRule
            .onAllNodes(
                hasText(title, substring = true)
                    .and(hasAnyAncestor(hasTestTag(SearchTags.ResultsList)))
            )[position]
            .assertIsDisplayed()
    }

    override fun screenIsDisplayed() {
        searchInput.assertIsDisplayed()
        searchAnimeButton.assertIsDisplayed()
        searchMangaButton.assertIsDisplayed()
        searchSearchButton.assertIsDisplayed()
    }
}