package com.kamaz.weather.data.dataBase

import androidx.room.*

@Entity(
    tableName = "cities",
    indices = [Index(value = ["englishName"], unique = true)]
)
data class CityEntity(
    val englishName: String,
    val latitude: Double,
    val longitude: Double,
    val isVisible: Boolean = false,
    val isUserAdded: Boolean = false,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)
