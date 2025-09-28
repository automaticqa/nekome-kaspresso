package com.chesire.nekome.barista.injection

import com.chesire.nekome.datasource.series.remote.SeriesApi
import com.chesire.nekome.injection.LibraryModule
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [LibraryModule::class]
)
class MockLibraryModule {

    @Provides
    @Reusable
    fun provideApi() = mockk<SeriesApi>()
}
