package com.kamaz.weather.data.dataBase.repository

import com.kamaz.weather.common.model.City
import com.kamaz.weather.data.remote.services.WeatherApi
import com.kamaz.weather.common.model.WeatherUiModel
import com.kamaz.weather.data.dataBase.CityDao
import com.kamaz.weather.data.dataBase.CityEntity
import com.kamaz.weather.utils.mappers.toUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.Closeable
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.map

@Singleton
class WeatherRepository @Inject constructor(
    private val api: WeatherApi,
    private val cityDao: CityDao
) : Closeable {

    private val repoScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val allCitiesFlow: Flow<List<City>> =
        cityDao.observeAll()
            .map { list -> list.map { e -> City(e.englishName, e.latitude, e.longitude) } }

    val visibleCityNames: StateFlow<Set<String>> =
        cityDao.observeVisible()
            .map { list -> list.map { it.englishName }.toSet() }
            .stateIn(repoScope, SharingStarted.Eagerly, emptySet())

    private var lastWeatherList: List<WeatherUiModel> = emptyList()
    private val _allWeatherFlow = MutableStateFlow<List<WeatherUiModel>>(emptyList())
    val allWeatherFlow: StateFlow<List<WeatherUiModel>> = _allWeatherFlow

    val visibleWeatherFlow: Flow<List<WeatherUiModel>> =
        combine(allWeatherFlow, visibleCityNames) { all, vis ->
            if (vis.isEmpty()) emptyList() else all.filter { it.cityName in vis }
        }

    init {
        repoScope.launch {
            tickerFlow(periodMillis = 10000, emitImmediately = true).collect {
                fetchWeatherSafely()
            }
        }

        repoScope.launch {
            allCitiesFlow.drop(1).collect {
                fetchWeatherSafely()
            }
        }
    }

    private suspend fun fetchWeatherSafely() {
        runCatching { fetchWeather() }
            .onFailure { }
    }

    private suspend fun fetchWeather() {
        val cities = allCitiesFlow.first()
        if (cities.isEmpty()) {
            if (_allWeatherFlow.value.isNotEmpty()) _allWeatherFlow.value = emptyList()
            return
        }

        val list = coroutineScope {
            cities.map { c ->
                async {
                    runCatching {
                        val resp = api.getCurrentWeather(c.latitude, c.longitude)
                        resp.currentWeather.toUiModel(c.englishName)
                    }.getOrNull()
                }
            }.awaitAll().filterNotNull()
        }

        if (list != lastWeatherList) {
            lastWeatherList = list
            _allWeatherFlow.value = list
        }
    }


    suspend fun refreshAllWeather() = fetchWeatherSafely()

    suspend fun addCity(name: String, lat: Double, lon: Double): Boolean {
        val rows = cityDao.insertAll(
            listOf(CityEntity(name.trim(), lat, lon, isVisible = false, isUserAdded = true))
        )
        return rows.isNotEmpty()
    }

    suspend fun removeCity(name: String): Boolean =
        cityDao.deleteByName(name.trim()) > 0

    suspend fun setCityVisible(name: String, visible: Boolean) {
        cityDao.setVisible(name.trim(), visible)
    }

    override fun close() {
        repoScope.cancel()
    }

    private fun tickerFlow(periodMillis: Long, emitImmediately: Boolean) = flow {
        if (emitImmediately) emit(Unit)
        while (currentCoroutineContext().isActive) {
            delay(periodMillis)
            emit(Unit)
        }
    }

    suspend fun updateCityCoordinates(name: String, lat: Double, lon: Double): Boolean {
        val rows = cityDao.updateCoordinates(name.trim(), lat, lon)
        if (rows > 0) {
            refreshAllWeather()
            return true
        }
        return false
    }
}


