package com.kamaz.weather.common.connectivityObserver

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface ConnectivityObserver {val isConnected: Flow<Boolean> }

@Singleton
class AndroidConnectivityObserver @Inject constructor(
    @ApplicationContext context: Context
) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(isCurrentlyConnected())
    override val isConnected: Flow<Boolean> = _isConnected.asStateFlow()

    init {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isConnected.value = true
            }

            override fun onLost(network: Network) {
                _isConnected.value = false
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                _isConnected.value = hasValidated
            }
        }

        try {
            connectivityManager.registerDefaultNetworkCallback(callback)
        } catch (e: Exception) {
            Log.e("ConnectivityObserver", "Failed to register callback", e)
        }
    }

    private fun isCurrentlyConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
