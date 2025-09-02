package com.kamaz.weather.di

import com.kamaz.weather.data.dataBase.CityDao
import com.kamaz.weather.data.remote.services.WeatherApi
import com.kamaz.weather.data.dataBase.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideWeatherRepository(
        api: WeatherApi,
        cityDao: CityDao
    ): WeatherRepository {
        return WeatherRepository(api, cityDao)
    }
}

