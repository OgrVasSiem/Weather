package com.kamaz.weather.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherApiResponse(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("current_weather")
    val currentWeather: CurrentWeatherDto
)

data class CurrentWeatherDto(
    @SerializedName("temperature")
    val temperature: Double,

    @SerializedName("windspeed")
    val windSpeed: Double,

    @SerializedName("winddirection")
    val windDirection: Double,

    @SerializedName("weathercode")
    val weatherCode: Int,

    @SerializedName("time")
    val time: String
)