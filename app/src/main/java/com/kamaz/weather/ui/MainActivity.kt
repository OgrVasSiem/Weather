package com.kamaz.weather.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kamaz.weather.R
import com.kamaz.weather.data.dataStore.AuthorizationDataStore
import com.kamaz.weather.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var authorizationDataStore: AuthorizationDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.findNavController() ?: return

        lifecycleScope.launch {
            val (login, password) = authorizationDataStore.data.first()
            if (login.isNotEmpty() && password.isNotEmpty()) {
                navController.navigate(R.id.mainFragment)
            } else {
                navController.navigate(R.id.loginFragment)
            }
        }
    }
}
