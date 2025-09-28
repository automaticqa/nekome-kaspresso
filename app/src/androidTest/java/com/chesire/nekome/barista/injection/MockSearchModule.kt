package com.chesire.nekome.barista.injection

import com.chesire.nekome.datasource.search.remote.SearchApi
import com.chesire.nekome.injection.SearchModule
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SearchModule::class]
)
class MockSearchModule {

    @Provides
    @Reusable
    fun provideApi() = mockk<SearchApi>()
}
