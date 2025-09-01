package com.foresko.atom.core.common.utils.connectivityObserver.di

import android.content.Context
import com.foresko.atom.core.common.utils.connectivityObserver.AndroidConnectivityObserver
import com.foresko.atom.core.common.utils.connectivityObserver.ConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun providesAndroidConnectivityObserver(
        @ApplicationContext context: Context
    ): ConnectivityObserver = AndroidConnectivityObserver(context)
}