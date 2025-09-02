package com.kamaz.weather.data.dataBase

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {

    @Query("SELECT COUNT(*) FROM cities")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(entities: List<CityEntity>): List<Long>

    @Query("SELECT * FROM cities ORDER BY englishName ASC")
    fun observeAll(): Flow<List<CityEntity>>

    @Query("SELECT * FROM cities WHERE isVisible = 1 ORDER BY englishName ASC")
    fun observeVisible(): Flow<List<CityEntity>>

    @Query("DELETE FROM cities WHERE englishName = :name")
    suspend fun deleteByName(name: String): Int

    @Query("UPDATE cities SET isVisible = :visible WHERE englishName = :name")
    suspend fun setVisible(name: String, visible: Boolean)

    @Query("UPDATE cities SET latitude = :lat, longitude = :lon WHERE englishName = :name")
    suspend fun updateCoordinates(name: String, lat: Double, lon: Double): Int

}