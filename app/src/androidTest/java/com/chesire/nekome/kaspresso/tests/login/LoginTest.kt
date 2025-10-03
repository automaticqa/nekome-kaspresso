package com.chesire.nekome.kaspresso.tests.login

import dagger.hilt.android.testing.HiltAndroidTest
import com.chesire.nekome.kaspresso.screens.collections.AnimeCollectionScreen
import com.chesire.nekome.kaspresso.screens.login.LoginFormScreen
import com.chesire.nekome.kaspresso.tests.OnlineBaseTestSetup
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class LoginTest : OnlineBaseTestSetup() {

    override val startLoggedIn = false

    @Before
    fun before() {
        //startApp()
    }

    /**
     * This test runs in online mode and, of course, it is not written
     * for the real backend, but rather considering the case
     * if we had a real test backend where we could send real requests
     * and run end-to-end tests.
     * Naturally, in real production,
     * on the actual production backend, running such an online test is not desirable
     */
    @Test
    fun verify_login_form_screen_is_displayed() = run {
        step("Verify Login Form screen is displayed") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }

        step("Verify Login Form screen is displayed") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                emailField.performTextInput("testnekome@gmail.com")
                passwordField.performTextInput("emokentset$2025")
                loginButton.performClick()
            }
        }

        step("Verify Login Form screen is displayed") {
            onComposeScreen<AnimeCollectionScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }
    }
}