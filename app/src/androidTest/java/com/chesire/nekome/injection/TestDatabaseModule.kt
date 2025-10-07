package com.chesire.nekome.injection

import android.content.Context
import androidx.room.Room
import com.chesire.nekome.database.RoomDB
import com.chesire.nekome.database.dao.SeriesDao
import com.chesire.nekome.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {
    @Provides
    @Singleton
    fun provideInMemoryDatabase(
        @ApplicationContext context: Context
    ): RoomDB {
        println("okhttp.OkHttpClient: provideInMemoryDatabase() called")
        return Room.inMemoryDatabaseBuilder(
            context,
            RoomDB::class.java
        ).build()
    }

    @Provides
    @Singleton
    fun provideSeriesDao(db: RoomDB): SeriesDao {
        println("okhttp.OkHttpClient: provideSeriesDao() called")
        return db.series()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: RoomDB): UserDao {
        println("okhttp.OkHttpClient: provideUserDao() called")
        return db.user()
    }
}
