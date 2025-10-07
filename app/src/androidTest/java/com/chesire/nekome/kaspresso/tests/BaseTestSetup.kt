package com.chesire.nekome.kaspresso.tests

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import com.adevinta.android.barista.rule.cleardata.ClearDatabaseRule
import com.adevinta.android.barista.rule.cleardata.ClearPreferencesRule
import com.chesire.nekome.barista.helpers.createTestUser
import com.chesire.nekome.barista.helpers.login
import com.chesire.nekome.barista.helpers.logout
import com.chesire.nekome.barista.helpers.reset
import com.chesire.nekome.core.preferences.ApplicationPreferences
import com.chesire.nekome.core.preferences.SeriesPreferences
import com.chesire.nekome.database.dao.SeriesDao
import com.chesire.nekome.database.dao.UserDao
import com.chesire.nekome.datasource.auth.local.AuthProvider
import com.chesire.nekome.injection.TestFramework
import com.chesire.nekome.injection.TestFrameworkHolder
import com.chesire.nekome.kaspresso.common.NetworkMode
import com.chesire.nekome.kaspresso.common.NetworkModeHolder
import com.chesire.nekome.kaspresso.mock.MockWebServerManager
import com.chesire.nekome.ui.MainActivity
import com.kaspersky.components.alluresupport.withForcedAllureSupport
import com.kaspersky.components.composesupport.config.addComposeSupport
import com.kaspersky.components.composesupport.interceptors.behavior.impl.failure.FailureLoggingSemanticsBehaviorInterceptor
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.params.FlakySafetyParams
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
abstract class BaseTestSetup(
    open val networkMode: NetworkMode = NetworkMode.ONLINE
) : TestCase(
    kaspressoBuilder = Kaspresso.Builder
        .withForcedAllureSupport(shouldRecordVideo = false) {
            flakySafetyParams = FlakySafetyParams.custom(
                timeoutMs = 15_000L,
                intervalMs = 200L
            )
        }.apply {
            addComposeSupport { composeBuilder ->
                composeBuilder.semanticsBehaviorInterceptors =
                    composeBuilder.semanticsBehaviorInterceptors
                        .filterIsInstance<FailureLoggingSemanticsBehaviorInterceptor>()
                        .toMutableList()
            }

            testRunWatcherInterceptors.removeIf {
                val name = it.javaClass.simpleName
                name.contains("Screenshot", true) ||
                name.contains("DumpViews", true) ||
                name.contains("VideoRecording", true) ||
                name.contains("HackyVideoRecording", true) ||
                name.contains("DumpLogcat", true)
            }

            stepWatcherInterceptors.removeIf {
                it.javaClass.simpleName.contains("Screenshot", ignoreCase = true)
            }
        }
) {

    init {
        TestFrameworkHolder.framework = TestFramework.KASPRESSO
        NetworkModeHolder.mode = networkMode

        if (networkMode == NetworkMode.OFFLINE) {
            MockWebServerManager.start()
        }
    }

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val clearDatabaseRule = ClearDatabaseRule()

    @get:Rule(order = 2)
    val clearPreferencesRule = ClearPreferencesRule()

    @get:Rule(order = 3)
    val runtimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(*providePermissions())

    @get:Rule(order = 4)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 5)
    open val activityRule: ActivityTestRule<Activity> = activityTestRuleWithIntent(
        Intent(
            ApplicationProvider.getApplicationContext(),
            MainActivity::class.java
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    )

    @Inject
    lateinit var authProvider: AuthProvider

    @Inject
    lateinit var seriesDao: SeriesDao

    @Inject
    lateinit var userDao: UserDao

    @Inject
    lateinit var applicationPreferences: ApplicationPreferences

    @Inject
    lateinit var seriesPreferences: SeriesPreferences

    open val startLoggedIn: Boolean = false

    @Before
    open fun setUp() {
        TestFrameworkHolder.framework = TestFramework.KASPRESSO
        NetworkModeHolder.mode = networkMode

        if (networkMode == NetworkMode.OFFLINE) {
            MockWebServerManager.awaitReadyOrThrow(5_000)
        }

        hiltRule.inject()

        runBlocking {
            applicationPreferences.reset()
            seriesPreferences.reset()
        }

        setupAuthenticationState()
    }

    protected open fun setupAuthenticationState() {
        if (startLoggedIn) {
            authProvider.login()
            userDao.createTestUser()
        } else {
            authProvider.logout()
        }
    }

    @After
    open fun tearDown() {
        composeTestRule.waitForIdle()

        if (networkMode == NetworkMode.OFFLINE) {
            MockWebServerManager.shutdown()
        }
    }

    protected open fun providePermissions(): Array<String> {
        val commonPermissions = mutableListOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            commonPermissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        return commonPermissions.toTypedArray()
    }

    private inline fun <reified T : Activity> activityTestRuleWithIntent(
        intent: Intent,
        initialTouchMode: Boolean = false,
        launchActivity: Boolean = false
    ) = object : ActivityTestRule<T>(T::class.java, initialTouchMode, launchActivity) {
        override fun getActivityIntent(): Intent = intent
    }
}

