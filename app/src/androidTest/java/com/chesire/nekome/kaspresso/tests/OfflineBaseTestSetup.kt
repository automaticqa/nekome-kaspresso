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
import com.kaspersky.components.alluresupport.withForcedAllureSupport
import com.kaspersky.components.composesupport.config.addComposeSupport
import com.kaspersky.components.composesupport.interceptors.behavior.impl.failure.FailureLoggingSemanticsBehaviorInterceptor
import com.kaspersky.kaspresso.kaspresso.Kaspresso
import com.kaspersky.kaspresso.params.FlakySafetyParams
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import com.chesire.nekome.core.preferences.ApplicationPreferences
import com.chesire.nekome.core.preferences.SeriesPreferences
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
import com.chesire.nekome.binders.UserProviderBinder
import com.chesire.nekome.database.RoomDB
import com.chesire.nekome.injection.AuthModule
import com.chesire.nekome.injection.DatabaseModule
import com.chesire.nekome.injection.LibraryModule
import com.chesire.nekome.injection.SeriesModule
import com.chesire.nekome.injection.SearchModule
import com.chesire.nekome.injection.TrendingModule
import com.chesire.nekome.injection.UserModule
import com.chesire.nekome.kaspresso.common.MockWebServerHolder
import com.chesire.nekome.kaspresso.common.NetworkMode
import com.chesire.nekome.kaspresso.common.NetworkModeHolder
import com.chesire.nekome.ui.MainActivity
import com.chesire.nekome.kitsu.KITSU_URL
import com.chesire.nekome.kitsu.adapters.ImageModelAdapter
import com.chesire.nekome.kitsu.adapters.SeriesStatusAdapter
import com.chesire.nekome.kitsu.adapters.SeriesTypeAdapter
import com.chesire.nekome.kitsu.adapters.SubtypeAdapter
import com.chesire.nekome.kitsu.auth.KitsuAuth
import com.chesire.nekome.kitsu.auth.KitsuAuthService
import com.chesire.nekome.kitsu.library.KitsuLibrary
import com.chesire.nekome.kitsu.library.KitsuLibraryService
import com.chesire.nekome.kitsu.library.adapter.UserSeriesStatusAdapter
import com.chesire.nekome.kitsu.search.KitsuSearch
import com.chesire.nekome.kitsu.search.KitsuSearchService
import com.chesire.nekome.kitsu.trending.KitsuTrending
import com.chesire.nekome.kitsu.trending.KitsuTrendingService
import com.chesire.nekome.kitsu.user.KitsuUser
import com.chesire.nekome.kitsu.user.KitsuUserService
import com.chesire.nekome.kitsu.user.adapter.RatingSystemAdapter
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule

@HiltAndroidTest
@UninstallModules(
    AuthModule::class,
    UserModule::class,
    LibraryModule::class,
    SeriesModule::class,
    SearchModule::class,
    TrendingModule::class,
    DatabaseModule::class
)
abstract class OfflineBaseTestSetup(
    open val networkMode: NetworkMode = NetworkMode.OFFLINE
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

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val clearDatabaseRule = ClearDatabaseRule()

    @get:Rule(order = 2)
    val clearPreferencesRule = ClearPreferencesRule()

    @get:Rule(order = 3)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 4)
    val runtimePermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(*providePermissions())

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
        NetworkModeHolder.mode = networkMode
        
        if (networkMode == NetworkMode.OFFLINE) {
            MockWebServerHolder.start()
        }
        
        // only now perform hilt injectionn
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
            MockWebServerHolder.shutdown()
        }
    }

    // TODO: REFACTOR IT
    fun setOffline() {
        NetworkModeHolder.mode = NetworkMode.OFFLINE
    }

    protected open fun providePermissions(): Array<String> {
        val commonPermissions = mutableListOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
        )

        // Add notification permission for Android 13+
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



    // TODO: MOVE IT TO SEPARATED MODULE
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class KaspressoNetworkModule {
        
        @Binds
        abstract fun bindAuthApi(api: KitsuAuth): AuthApi
        
        @Binds
        abstract fun bindUserApi(api: KitsuUser): UserApi
        
        @Binds
        abstract fun bindSeriesApi(api: KitsuLibrary): SeriesApi
        
        @Binds
        abstract fun bindSearchApi(api: KitsuSearch): SearchApi
        
        @Binds
        abstract fun bindTrendingApi(api: KitsuTrending): TrendingApi
        
        @Binds
        abstract fun bindUserProvider(binder: UserProviderBinder): UserProvider
        
        companion object {
            
            @Provides
            @Reusable
            fun provideSeriesRepository(
                dao: SeriesDao,
                api: SeriesApi,
                user: UserProvider,
                map: SeriesMapper
            ) = SeriesRepository(dao, api, user, map)
            
            @Provides
            @Reusable
            fun provideSeriesMapper() = SeriesMapper()
            
            private fun getBaseUrl(): String {
                return when (NetworkModeHolder.mode) {
                    NetworkMode.ONLINE -> KITSU_URL
                    NetworkMode.OFFLINE -> MockWebServerHolder.baseUrl.toString()
                }
            }
            
            @Provides
            @Reusable
            fun providesAuthService(): KitsuAuthService {
                return Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(OkHttpClient())
                    .addConverterFactory(
                        MoshiConverterFactory.create(Moshi.Builder().build())
                    )
                    .build()
                    .create(KitsuAuthService::class.java)
            }
            
            @Provides
            @Reusable
            fun providesUserService(
                httpClient: OkHttpClient
            ): KitsuUserService {
                val moshi = Moshi.Builder()
                    .add(RatingSystemAdapter())
                    .add(ImageModelAdapter())
                    .build()
                
                return Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(httpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(KitsuUserService::class.java)
            }
            
            @Provides
            @Reusable
            fun providesLibraryService(
                httpClient: OkHttpClient
            ): KitsuLibraryService {
                val moshi = Moshi.Builder()
                    .add(ImageModelAdapter())
                    .add(SeriesStatusAdapter())
                    .add(SeriesTypeAdapter())
                    .add(SubtypeAdapter())
                    .add(UserSeriesStatusAdapter())
                    .build()
                
                return Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(httpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(KitsuLibraryService::class.java)
            }
            
            @Provides
            @Reusable
            fun providesSearchService(
                httpClient: OkHttpClient
            ): KitsuSearchService {
                val moshi = Moshi.Builder()
                    .add(ImageModelAdapter())
                    .add(SeriesStatusAdapter())
                    .add(SeriesTypeAdapter())
                    .add(SubtypeAdapter())
                    .build()
                
                return Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(httpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(KitsuSearchService::class.java)
            }
            
            @Provides
            @Reusable
            fun providesTrendingService(
                httpClient: OkHttpClient
            ): KitsuTrendingService {
                val moshi = Moshi.Builder()
                    .add(ImageModelAdapter())
                    .add(SeriesStatusAdapter())
                    .add(SeriesTypeAdapter())
                    .add(SubtypeAdapter())
                    .build()
                
                return Retrofit.Builder()
                    .baseUrl(getBaseUrl())
                    .client(httpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(KitsuTrendingService::class.java)
            }
        }
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object KaspressoDatabaseModule {
        
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