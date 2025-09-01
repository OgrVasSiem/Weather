package com.kamaz.weather.common.model

data class WeatherUiModel(
    val cityName: String,
    val temperature: String,
    val windSpeed: String,
    val windDirection: String,
    val weatherCode: Int,
    val time: String
)

