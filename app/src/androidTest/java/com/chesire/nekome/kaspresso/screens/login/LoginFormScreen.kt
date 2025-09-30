package com.chesire.nekome.kaspresso.screens.login

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import com.chesire.nekome.app.login.credentials.ui.CredentialsTags.ForgotPasswordButton
import com.chesire.nekome.app.login.credentials.ui.CredentialsTags.LoginButton
import com.chesire.nekome.app.login.credentials.ui.CredentialsTags.Password
import com.chesire.nekome.app.login.credentials.ui.CredentialsTags.Username
import com.chesire.nekome.kaspresso.screens.BaseScreen
import io.github.kakaocup.compose.node.element.KNode
import com.chesire.nekome.core.resources.R

class LoginFormScreen(
    semanticsProvider: SemanticsNodeInteractionsProvider
) : BaseScreen<LoginFormScreen>(semanticsProvider) {

    val emailField: KNode
        get() = child {
            hasTestTag(Username)
            hasText(R.string.login_username)
        }

    val passwordField: KNode
        get() = child {
            hasTestTag(Password)
            hasText(R.string.login_password)
        }

    val loginButton: KNode
        get() = child {
            hasTestTag(LoginButton)
            hasText(R.string.login_login)
        }

    val forgotPasswordButton: KNode
        get() = child {
            hasTestTag(ForgotPasswordButton)
            hasText(R.string.login_forgot_password)
        }

    override fun screenIsDisplayed() {
        emailField.assertIsDisplayed()
        passwordField.assertIsDisplayed()
        loginButton.assertIsDisplayed()
        forgotPasswordButton.assertIsDisplayed()
    }
}