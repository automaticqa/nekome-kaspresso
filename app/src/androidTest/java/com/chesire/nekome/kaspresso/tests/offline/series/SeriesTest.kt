package com.chesire.nekome.kaspresso.tests.offline.series

import androidx.test.espresso.Espresso.pressBack
import dagger.hilt.android.testing.HiltAndroidTest
import com.chesire.nekome.kaspresso.mock.MockTestData
import com.chesire.nekome.kaspresso.mock.MockWebServerManager
import com.chesire.nekome.kaspresso.mock.mockDeleteAnime
import com.chesire.nekome.kaspresso.mock.mockLoginWithSingleAnimeInTrack
import com.chesire.nekome.kaspresso.mock.mockSearchSeries
import com.chesire.nekome.kaspresso.screens.collections.AnimeCollectionScreen
import com.chesire.nekome.kaspresso.screens.collections.CardItemScreen
import com.chesire.nekome.kaspresso.screens.dialog.DeleteSeriesDialogScreen
import com.chesire.nekome.kaspresso.screens.login.LoginFormScreen
import com.chesire.nekome.kaspresso.screens.search.SearchSeriesScreen
import com.chesire.nekome.kaspresso.screens.syncing.SyncingScreen
import com.chesire.nekome.kaspresso.tests.BaseTestSetup
import com.chesire.nekome.kaspresso.common.NetworkMode
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Test

@HiltAndroidTest
class SeriesTest : BaseTestSetup(networkMode = NetworkMode.OFFLINE) {

    @Test
    fun add_anime_series_in_track() = run {
        val firstPosition = 0
        val expectedSeriesTitle = MockTestData.TestCollectionTitles.ATTACK_ON_TITAN_2
        val attackOnTitans2AnimeId = MockTestData.AttackOnTitanSeason2.ANIME_ID

        step("Setup mock login and search responses") {
            MockWebServerManager.mockSearchSeries()
        }

        step("Verify Login Form screen is displayed") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Perform login with valid credentials") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                emailField.performTextInput(MockTestData.TestUser.USERNAME)
                passwordField.performTextInput(MockTestData.TestUser.PASSWORD)
                loginButton.performClick()
            }
        }

        step("Verify successful login leads to syncing screen") {
            onComposeScreen<SyncingScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Verify Anime Collection screen is displayed after sync") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Open search screen by clicking add button") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                addNewButton.performClick()
            }
        }

        step("Search for anime series") {
            onComposeScreen<SearchSeriesScreen>(composeTestRule) {
                searchInput.performClick()
                searchInput.performTextInput(MockTestData.TestCollectionTitles.ATTACK_ON_TITAN)
                searchSearchButton.performClick()
            }
        }

        step("Verify search results list is displayed") {
            onComposeScreen<SearchSeriesScreen>(composeTestRule) {
                flakySafely {
                    resultsList.assertIsDisplayed()
                }
            }
        }

        step("Verify first result contains expected title") {
            onComposeScreen<SearchSeriesScreen>(composeTestRule) {
                flakySafely {
                    assertResultAtPositionContainsTitle(
                        composeTestRule,
                        firstPosition,
                        expectedSeriesTitle
                    )
                }
            }
        }

        step("Add first result to tracking list") {
            onComposeScreen<SearchSeriesScreen>(composeTestRule) {
                flakySafely {
                    clickAddButtonForFirstResultWithTitle(
                        composeTestRule,
                        expectedSeriesTitle
                    )
                }
            }
        }

        step("Verify add button disappears after adding the result under track") {
            onComposeScreen<SearchSeriesScreen>(composeTestRule) {
                flakySafely {
                    assertAddButtonGoneByItemId(composeTestRule, attackOnTitans2AnimeId)
                }
            }
        }

        step("Navigate back to Anime Collection screen") {
            pressBack()
        }

        step("Verify returned to Anime Collection screen") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Verify added series is displayed in collection") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                assertSeriesWithTitleDisplayed(composeTestRule, expectedSeriesTitle)
            }
        }
    }

    @Test
    fun delete_anime_series_from_track() = run {
        val expectedSeriesTitle = MockTestData.TestCollectionTitles.ATTACK_ON_TITAN_2
        val attackOnTitans2LibraryEntryId = MockTestData.AttackOnTitanSeason2.LIBRARY_ENTRY_ID

        step("Setup mock login with single anime in track and DELETE endpoint") {
            MockWebServerManager.mockLoginWithSingleAnimeInTrack()
            MockWebServerManager.mockDeleteAnime(attackOnTitans2LibraryEntryId)
        }

        step("Verify Login Form screen is displayed") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Perform login with valid credentials") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                emailField.performTextInput(MockTestData.TestUser.USERNAME)
                passwordField.performTextInput(MockTestData.TestUser.PASSWORD)
                loginButton.performClick()
            }
        }

        step("Verify successful login leads to syncing screen") {
            onComposeScreen<SyncingScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Verify Anime Collection screen is displayed after sync") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Verify anime series is displayed in collection") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                assertSeriesWithTitleDisplayed(composeTestRule, expectedSeriesTitle)
            }
        }

        step("Click on anime series to open card detail screen") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                clickSeriesWithTitle(expectedSeriesTitle)
            }
        }

        step("Verify all elements of CardItemScreen are displayed") {
            onComposeScreen<CardItemScreen>(composeTestRule) {
                flakySafely {
                    allElementsAreDisplayed()
                }
            }
        }

        step("Verify series title is displayed on card") {
            onComposeScreen<CardItemScreen>(composeTestRule) {
                seriesCardIsDisplayedWithTitle(expectedSeriesTitle)
            }
        }

        step("Click delete button to remove series from tracking") {
            onComposeScreen<CardItemScreen>(composeTestRule) {
                deleteButton.performClick()
            }
        }

        step("Verify delete confirmation dialog is displayed") {
            onComposeScreen<DeleteSeriesDialogScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                    seriesDeleteTitleIsDisplayed(expectedSeriesTitle)
                }
            }
        }

        step("Confirm deletion in dialog") {
            onComposeScreen<DeleteSeriesDialogScreen>(composeTestRule) {
                flakySafely {
                    confirmButton.performClick()
                }
            }
        }

        step("Verify returned to Anime Collection screen with empty collection") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                flakySafely {
                    emptyView.assertIsDisplayed()
                }
            }
        }
    }
}