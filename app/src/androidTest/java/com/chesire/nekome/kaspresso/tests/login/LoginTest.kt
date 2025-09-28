package com.chesire.nekome.kaspresso.tests.login

import dagger.hilt.android.testing.HiltAndroidTest
import com.chesire.nekome.datasource.auth.remote.AuthApi
import com.chesire.nekome.kaspresso.screens.login.LoginFormScreen
import com.chesire.nekome.kaspresso.tests.BaseTestSetup
import io.github.kakaocup.compose.node.element.ComposeScreen.Companion.onComposeScreen
import org.junit.Before
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class LoginTest : BaseTestSetup() {

    override val startLoggedIn = false

    @Inject
    lateinit var authApi: AuthApi

    @Before
    fun before() {
        startApp()
    }

    @Test
    fun verify_login_form_screen_is_displayed() = run {
        step("Verify Login Form screen is displayed") {
            onComposeScreen<LoginFormScreen>(composeTestRule) {
                flakySafely {
                    screenIsDisplayed()
                }
            }
        }
    }
}