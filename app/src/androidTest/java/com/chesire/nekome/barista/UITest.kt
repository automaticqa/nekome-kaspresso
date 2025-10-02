package com.chesire.nekome.barista

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.adevinta.android.barista.rule.cleardata.ClearDatabaseRule
import com.adevinta.android.barista.rule.cleardata.ClearPreferencesRule
import com.chesire.nekome.core.preferences.ApplicationPreferences
import com.chesire.nekome.core.preferences.SeriesPreferences
import com.chesire.nekome.database.RoomDB
import com.chesire.nekome.database.dao.SeriesDao
import com.chesire.nekome.database.dao.UserDao
import com.chesire.nekome.datasource.auth.local.AuthProvider
import com.chesire.nekome.datasource.auth.remote.AuthApi
import com.chesire.nekome.datasource.search.remote.SearchApi
import com.chesire.nekome.datasource.series.remote.SeriesApi
import com.chesire.nekome.datasource.trending.remote.TrendingApi
import com.chesire.nekome.datasource.user.remote.UserApi
import com.chesire.nekome.datasource.series.SeriesRepository
import com.chesire.nekome.datasource.series.UserProvider
import com.chesire.nekome.datasource.series.SeriesMapper
import com.chesire.nekome.barista.helpers.createTestUser
import com.chesire.nekome.barista.helpers.login
import com.chesire.nekome.barista.helpers.logout
import com.chesire.nekome.barista.helpers.reset
import com.chesire.nekome.injection.AuthModule
import com.chesire.nekome.injection.DatabaseModule
import com.chesire.nekome.injection.SeriesModule
import com.chesire.nekome.injection.SearchModule
import com.chesire.nekome.injection.TrendingModule
import com.chesire.nekome.injection.UserModule
import com.chesire.nekome.ui.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.mockk.mockk
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

/**
 * Provides a base class to use for all UI tests.
 */
@HiltAndroidTest
@UninstallModules(
    AuthModule::class,
    UserModule::class,
    SeriesModule::class,
    SearchModule::class,
    TrendingModule::class,
    DatabaseModule::class
)
@RunWith(AndroidJUnit4::class)
abstract class UITest {

    @Suppress("LeakingThis")
    @get:Rule
    val hilt = HiltAndroidRule(this)

    @get:Rule
    val clearDatabase = ClearDatabaseRule()

    @get:Rule
    val clearPreferences = ClearPreferencesRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var authProvider: AuthProvider

    @Inject
    lateinit var series: SeriesDao

    @Inject
    lateinit var user: UserDao

    @Inject
    lateinit var applicationPreferences: ApplicationPreferences

    @Inject
    lateinit var seriesPreferences: SeriesPreferences

    /**
     * Flag for if the test should start with a logged in user.
     * Defaults to `true`, override to force the user to be logged out.
     */
    open val startLoggedIn: Boolean = true

    /**
     * Initial setup method.
     */
    @Before
    open fun setUp() {
        hilt.inject()

        runBlocking {
            applicationPreferences.reset()
            seriesPreferences.reset()
        }

        if (startLoggedIn) {
            authProvider.login()
            user.createTestUser()
        } else {
            authProvider.logout()
        }
    }

    /**
     * Launches the [Activity] using the [ActivityScenario].
     */
    protected fun launchActivity() {
        ActivityScenario.launch(MainActivity::class.java)
        // Not the nicest solution, but it keeps compose views a bit happier when they launch.
        Thread.sleep(200)
    }

    /**
     * Nested Hilt module providing MockK-backed API fakes for UI tests.
     * Registered into the SingletonComponent and visible only in androidTest.
     */
    @Module
    @InstallIn(SingletonComponent::class)
    object BaristaMocksModule {
        
        @Provides
        @Singleton
        fun provideAuthApi(): AuthApi = mockk(relaxed = true)
        
        @Provides
        @Singleton
        fun provideUserApi(): UserApi = mockk(relaxed = true)
        
        @Provides
        @Singleton
        fun provideSeriesApi(): SeriesApi = mockk(relaxed = true)
        
        @Provides
        @Singleton
        fun provideSearchApi(): SearchApi = mockk(relaxed = true)
        
        @Provides
        @Singleton
        fun provideTrendingApi(): TrendingApi = mockk(relaxed = true)
        
        @Provides
        @Singleton
        fun provideUserProvider(): UserProvider = mockk(relaxed = true)
        
        @Provides
        @Singleton
        fun provideSeriesMapper(): SeriesMapper = mockk(relaxed = true)
        
        @Provides
        @Singleton
        fun provideSeriesRepository(): SeriesRepository = mockk(relaxed = true)
    }

    /**
     * Nested Hilt module providing MockK-backed API fakes for UI tests.
     * Registered into the SingletonComponent and visible only in androidTest
     */
    @Module
    @InstallIn(SingletonComponent::class)
    object BaristaInMemoryDbModule {
        
        @Provides
        @Singleton
        fun provideInMemoryDatabase(
            @ApplicationContext context: Context
        ): RoomDB = Room.inMemoryDatabaseBuilder(
            context,
            RoomDB::class.java
        ).build()
        
        @Provides
        @Singleton
        fun provideSeriesDao(db: RoomDB): SeriesDao = db.series()
        
        @Provides
        @Singleton
        fun provideUserDao(db: RoomDB): UserDao = db.user()
    }
}
