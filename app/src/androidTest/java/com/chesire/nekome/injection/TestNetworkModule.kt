package com.chesire.nekome.injection

import javax.inject.Qualifier
import com.chesire.nekome.binders.UserProviderBinder
import com.chesire.nekome.database.dao.SeriesDao
import com.chesire.nekome.datasource.auth.remote.AuthApi
import com.chesire.nekome.datasource.search.remote.SearchApi
import com.chesire.nekome.datasource.series.SeriesMapper
import com.chesire.nekome.datasource.series.SeriesRepository
import com.chesire.nekome.datasource.series.UserProvider
import com.chesire.nekome.datasource.series.remote.SeriesApi
import com.chesire.nekome.datasource.trending.remote.TrendingApi
import com.chesire.nekome.datasource.user.remote.UserApi
import com.chesire.nekome.kaspresso.common.NetworkMode
import com.chesire.nekome.kaspresso.common.NetworkModeHolder
import com.chesire.nekome.kaspresso.mock.MockWebServerManager
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
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedClient

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [
        AuthModule::class,
        UserModule::class,
        LibraryModule::class,
        SeriesModule::class,
        SearchModule::class,
        TrendingModule::class,
        ServerModule::class
    ]
)
object TestNetworkModule {

    private fun isKaspressoTest(): Boolean {
        return TestFrameworkHolder.framework == TestFramework.KASPRESSO
    }

    @Provides
    fun provideAuthApi(kitsuAuth: KitsuAuth): AuthApi {
        return if (isKaspressoTest()) {
            kitsuAuth
        } else {
            mockk(relaxed = true)
        }
    }

    @Provides
    fun provideUserApi(kitsuUser: KitsuUser): UserApi {
        return if (isKaspressoTest()) {
            kitsuUser
        } else {
            mockk(relaxed = true)
        }
    }

    @Provides
    fun provideSeriesApi(kitsuLibrary: KitsuLibrary): SeriesApi {
        return if (isKaspressoTest()) {
            kitsuLibrary
        } else {
            mockk(relaxed = true)
        }
    }

    @Provides
    fun provideSearchApi(kitsuSearch: KitsuSearch): SearchApi {
        return if (isKaspressoTest()) {
            kitsuSearch
        } else {
            mockk(relaxed = true)
        }
    }

    @Provides
    fun provideTrendingApi(kitsuTrending: KitsuTrending): TrendingApi {
        return if (isKaspressoTest()) {
            kitsuTrending
        } else {
            mockk(relaxed = true)
        }
    }

    @Provides
    fun provideUserProvider(binder: UserProviderBinder): UserProvider {
        return if (isKaspressoTest()) {
            binder
        } else {
            mockk(relaxed = true)
        }
    }

    private fun getBaseUrl(): String {
        return when (NetworkModeHolder.mode) {
            NetworkMode.ONLINE -> KITSU_URL
            NetworkMode.OFFLINE -> MockWebServerManager.awaitAndGetBaseUrl(5_000)
        }
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                println("[http] --> ${request.method} ${request.url}")

                try {
                    val response = chain.proceed(request)
                    println("[http] <-- ${response.code} ${response.message} ${request.url}")

                    val responseBody = response.body
                    if (responseBody != null) {
                        val source = responseBody.source()
                        source.request(Long.MAX_VALUE)
                        val buffer = source.buffer
                        val bodyString = buffer.clone().readUtf8()
                        println("[http] <-- Response body:\n$bodyString")
                    }

                    response
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }
            .build()
    }

    @Provides
    @AuthenticatedClient
    fun provideAuthenticatedOkHttpClient(
        baseClient: OkHttpClient,
        authInjection: com.chesire.nekome.datasource.auth.remote.AuthInjectionInterceptor,
        authRefresh: com.chesire.nekome.datasource.auth.remote.AuthRefreshInterceptor
    ): OkHttpClient {
        return baseClient.newBuilder()
            .addInterceptor(authInjection)
            .addInterceptor(authRefresh)
            .build()
    }

    @Provides
    fun provideSeriesRepository(
        dao: SeriesDao,
        api: SeriesApi,
        user: UserProvider,
        map: SeriesMapper
    ): SeriesRepository {
        return if (isKaspressoTest()) {
            SeriesRepository(dao, api, user, map)
        } else {
            mockk(relaxed = true)
        }
    }

    @Provides
    fun provideSeriesMapper(): SeriesMapper {
        return if (isKaspressoTest()) {
            SeriesMapper()
        } else {
            mockk(relaxed = true)
        }
    }

    @Provides
    fun providesAuthService(httpClient: OkHttpClient): KitsuAuthService {
        return Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(httpClient)
            .addConverterFactory(
                MoshiConverterFactory.create(Moshi.Builder().build())
            )
            .build()
            .create(KitsuAuthService::class.java)
    }

    @Provides
    fun providesUserService(@AuthenticatedClient httpClient: OkHttpClient): KitsuUserService {
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
    fun providesLibraryService(@AuthenticatedClient httpClient: OkHttpClient): KitsuLibraryService {
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
    fun providesSearchService(@AuthenticatedClient httpClient: OkHttpClient): KitsuSearchService {
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
    fun providesTrendingService(@AuthenticatedClient httpClient: OkHttpClient): KitsuTrendingService {
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
