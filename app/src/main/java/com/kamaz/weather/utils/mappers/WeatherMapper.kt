package com.kamaz.weather.utils.mappers

import com.kamaz.weather.data.remote.model.CurrentWeatherDto
import com.kamaz.weather.common.model.WeatherUiModel

fun CurrentWeatherDto.toUiModel(displayNameRes: String): WeatherUiModel {
    return WeatherUiModel(
        cityName = displayNameRes,  // получаем русский текст сразу
        temperature = "$temperature°C",
        windSpeed = "$windSpeed м/с",
        windDirection = "ветер $windDirection°",
        weatherCode = weatherCode,
        time = time
    )
}


fun Double.toCardinalDirection(): String {
    val directions = listOf("С", "СВ", "В", "ЮВ", "Ю", "ЮЗ", "З", "СЗ")
    val index = ((this / 45.0) + 0.5).toInt() % 8
    return directions[index]
}