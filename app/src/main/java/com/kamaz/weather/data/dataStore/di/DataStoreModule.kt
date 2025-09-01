package com.kamaz.weather.data.dataStore.di

import android.content.Context
import com.kamaz.weather.data.dataStore.AuthorizationDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.builtins.serializer
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun provideAuthorizationDataStore(
        @ApplicationContext context: Context
    ): AuthorizationDataStore {
        return AuthorizationDataStore(context = context)
    }
}
