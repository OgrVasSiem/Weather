package com.kamaz.weather.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
                ?: return
        val navController = navHostFragment.navController

        lifecycleScope.launch {
            val (login, password) = authorizationDataStore.data.first()

            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

            navGraph.setStartDestination(
                if (login.isNotEmpty() && password.isNotEmpty()) {
                    R.id.mainFragment
                } else {
                    R.id.loginFragment
                }
            )

            navController.graph = navGraph
        }
    }
}
