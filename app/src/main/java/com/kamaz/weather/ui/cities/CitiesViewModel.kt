package com.kamaz.weather.ui.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamaz.weather.common.model.City
import com.kamaz.weather.data.dataBase.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitiesViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {

    val allCities: StateFlow<List<City>> = repo.allCitiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun addCity(name: String, lat: Double, lon: Double, makeVisible: Boolean): Boolean {
        val ok = repo.addCity(name, lat, lon)
        if (ok && makeVisible) repo.setCityVisible(name, true)
        return ok
    }

    fun deleteCity(name: String) = viewModelScope.launch { repo.removeCity(name) }

    fun editCityCoordinates(name: String, lat: Double, lon: Double) =
        viewModelScope.launch { repo.updateCityCoordinates(name, lat, lon) }
}

