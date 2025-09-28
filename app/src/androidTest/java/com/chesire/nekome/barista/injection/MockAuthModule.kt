package com.chesire.nekome.barista.injection

import com.chesire.nekome.datasource.auth.remote.AuthApi
import com.chesire.nekome.injection.AuthModule
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AuthModule::class]
)
class MockAuthModule {

    @Provides
    @Reusable
    fun provideApi() = mockk<AuthApi>()
}
