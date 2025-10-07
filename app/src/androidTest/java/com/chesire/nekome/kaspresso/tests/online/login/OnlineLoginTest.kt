package com.chesire.nekome.kaspresso.tests.online.login

import dagger.hilt.android.testing.HiltAndroidTest
import com.chesire.nekome.kaspresso.common.NetworkMode
import com.chesire.nekome.kaspresso.mock.MockTestData
import com.chesire.nekome.kaspresso.screens.collections.AnimeCollectionScreen
import com.chesire.nekome.kaspresso.screens.login.LoginFormScreen
import com.chesire.nekome.kaspresso.screens.search.SearchSeriesScreen
import com.chesire.nekome.kaspresso.screens.syncing.SyncingScreen
import com.chesire.nekome.kaspresso.tests.BaseTestSetup
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Test

@HiltAndroidTest
class OnlineLoginTest : BaseTestSetup(networkMode = NetworkMode.ONLINE) {

    override val startLoggedIn = false

    @Test
    fun verify_login() = run {
        step("Verify Login Form screen is displayed") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Verify Login Form screen is displayed") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                emailField.performTextInput(MockTestData.OnlineTestUser.EMAIL)
                passwordField.performTextInput(MockTestData.OnlineTestUser.PASSWORD)
                loginButton.performClick()
            }
        }

        step("Verify SyncingScreen is displayed") {
            onComposeScreen<SyncingScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Verify Anime Collection screen is displayed") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }
    }

    @Test
    fun online_verify_search_series() = run {
        step("Verify Login Form screen is displayed") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Perform login with valid credentials") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                emailField.performTextInput(MockTestData.OnlineTestUser.EMAIL)
                passwordField.performTextInput(MockTestData.OnlineTestUser.PASSWORD)
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
            val firstPosition = 0
            onComposeScreen<SearchSeriesScreen>(composeTestRule) {
                flakySafely {
                    assertResultAtPositionContainsTitle(
                        composeTestRule,
                        firstPosition,
                        MockTestData.TestCollectionTitles.ATTACK_ON_TITAN_2
                    )
                }
            }
        }

        step("Verify second result contains expected title") {
            val secondPosition = 1
            onComposeScreen<SearchSeriesScreen>(composeTestRule) {
                flakySafely {
                    assertResultAtPositionContainsTitle(
                        composeTestRule,
                        secondPosition,
                        MockTestData.TestCollectionTitles.ATTACK_ON_TITAN_3
                    )
                }
            }
        }
    }
}