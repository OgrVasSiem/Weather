package com.kamaz.weather.data.repository

import com.kamaz.weather.data.remote.services.WeatherApi
import com.kamaz.weather.common.model.WeatherUiModel
import com.kamaz.weather.utils.mappers.toUiModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class City(
    val englishName: String,

    val latitude: Double,
    val longitude: Double
)

@Singleton
class WeatherRepository @Inject constructor(
    private val api: WeatherApi
) {
    private val cities = listOf(
        City("Moscow", 55.7558, 37.6173),
        City("Saint Petersburg",  59.9343, 30.3351),
        City("Novosibirsk", 55.0084, 82.9357),
        City("Yekaterinburg", 56.8389, 60.6057),
        City("Kazan", 55.7961, 49.1064),
        City("Nizhny Novgorod", 56.2965, 43.9361),
        City("Chelyabinsk",  55.1644, 61.4368),
        City("Samara", 53.2415, 50.2212),
        City("Omsk", 54.9924, 73.3686),
        City("Rostov-on-Don", 47.2357, 39.7015),
        City("Ufa", 54.7388, 55.9721),
        City("Krasnoyarsk", 56.0091, 92.7917),
        City("Voronezh",  51.6608, 39.2003),
        City("Perm", 58.0105, 56.2502),
        City("Volgograd",  48.7071, 44.5168)
    )

    // Последние данные для сравнения
    private var lastWeatherList: List<WeatherUiModel> = emptyList()

    // Видимые города по умолчанию (три)
    private val _visibleCityNames = MutableStateFlow(
        listOf("Moscow", "Saint Petersburg", "Novosibirsk")
    )
    val visibleCityNames: StateFlow<List<String>> = _visibleCityNames.asStateFlow()

    // Mutable Flow для всех погодных данных
    private val _allWeatherFlow = MutableStateFlow<List<WeatherUiModel>>(emptyList())
    val allWeatherFlow: StateFlow<List<WeatherUiModel>> = _allWeatherFlow

    init {
        startAutoRefresh()
    }

    // Автообновление каждые 5 секунд
    private fun startAutoRefresh() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                fetchWeather()
                delay(60 * 60 * 1000L)
            }
        }
    }

    // Получение данных с API
    private suspend fun fetchWeather() {
        val currentList = cities.map { city ->
            val response = api.getCurrentWeather(city.latitude, city.longitude)
            response.currentWeather.toUiModel(city.englishName)
        }

        if (currentList != lastWeatherList) {
            lastWeatherList = currentList
            _allWeatherFlow.value = currentList
        }
    }

    // Flow с видимыми городами для UI
    val visibleWeatherFlow: Flow<List<WeatherUiModel>> =
        combine(allWeatherFlow, visibleCityNames) { allWeather, visibleNames ->
            allWeather.filter { it.cityName in visibleNames }
        }

    // Добавить город в видимые
    fun addVisibleCity(name: String) {
        if (!_visibleCityNames.value.contains(name)) {
            _visibleCityNames.value = _visibleCityNames.value + name
        }
    }

    // Убрать город из видимых
    fun removeVisibleCity(name: String) {
        _visibleCityNames.value = _visibleCityNames.value - name
    }

    // Ручное обновление данных (вызывается по кнопке)
    suspend fun refreshAllWeather() {
        fetchWeather()
    }

    // Получить список всех городов
    fun getAllCities(): List<City> = cities
}
