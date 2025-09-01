package com.kamaz.weather.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foresko.atom.core.common.utils.connectivityObserver.ConnectivityObserver
import com.kamaz.weather.data.repository.WeatherRepository
import com.kamaz.weather.common.model.WeatherUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val visibleCities: StateFlow<List<WeatherUiModel>> =
        repository.visibleWeatherFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    val isConnected: StateFlow<Boolean> =
        connectivityObserver.isConnected
            .stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun refreshWeather() {
        viewModelScope.launch {
            repository.refreshAllWeather()
        }
    }
}

