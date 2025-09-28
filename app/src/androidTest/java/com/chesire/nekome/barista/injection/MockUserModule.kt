package com.chesire.nekome.barista.injection

import com.chesire.nekome.datasource.user.remote.UserApi
import com.chesire.nekome.injection.UserModule
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.mockk

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UserModule::class]
)
class MockUserModule {

    @Provides
    @Reusable
    fun provideApi() = mockk<UserApi>()
}
