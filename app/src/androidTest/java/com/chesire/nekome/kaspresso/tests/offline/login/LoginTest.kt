package com.chesire.nekome.kaspresso.tests.offline.login

import dagger.hilt.android.testing.HiltAndroidTest
import com.chesire.nekome.kaspresso.mock.MockWebServerManager
import com.chesire.nekome.kaspresso.mock.mockSuccessfulLogin
import com.chesire.nekome.kaspresso.screens.login.LoginFormScreen
import com.chesire.nekome.kaspresso.tests.BaseTestSetup
import com.chesire.nekome.kaspresso.common.NetworkMode
import com.chesire.nekome.kaspresso.screens.collections.AnimeCollectionScreen
import com.chesire.nekome.kaspresso.screens.syncing.SyncingScreen
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Test

@HiltAndroidTest
class LoginTest : BaseTestSetup(networkMode = NetworkMode.OFFLINE) {

    @Test
    fun verify_offline_login_flow_with_mock_server() = run {
        step("Setup mock login response") {
            MockWebServerManager.mockSuccessfulLogin()
        }

        step("Verify Login Form is shown after clearing auth") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Perform login with MockWebServer") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                emailField.performTextInput("testnekome@gmail.com")
                passwordField.performTextInput("emokentset$2025")
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

        step("Verify Anime Collection screen is displayed") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }
    }
}