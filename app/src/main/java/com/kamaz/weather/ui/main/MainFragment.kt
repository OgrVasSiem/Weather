package com.kamaz.weather.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kamaz.weather.R
import com.kamaz.weather.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: CityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CityAdapter { cityName ->
            // просто лог для проверки
            Log.d("MainFragment", "Clicked $cityName")
        }

        binding.rvVisibleCities.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVisibleCities.adapter = adapter

        // подписка на Flow видимых городов
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.visibleCities.collect { list ->
                adapter.submitList(list)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isConnected.collect { connected ->
                if (!connected) showSnackbar(isNoInternet = true)
            }
        }

        // Переход на экран "Города"
        binding.ivGoToCities.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_cities)
        }

        // Ручное обновление данных
        binding.ivRefresh.setOnClickListener {
            lifecycleScope.launch {
                viewModel.refreshWeather()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isConnected.collect { connected ->
                if (!connected) showSnackbar(isNoInternet = true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSnackbar(isNoInternet: Boolean) {
        val container = binding.snackbarLayout.snackbarContainer
        val tvNoConnection = container.findViewById<TextView>(R.id.tvNoConnection)
        val tvServerError = container.findViewById<TextView>(R.id.tvServerError)

        // Скрываем оба текста
        tvNoConnection.visibility = View.GONE
        tvServerError.visibility = View.GONE

        // Отображаем только нужный
        if (isNoInternet) {
            tvNoConnection.visibility = View.VISIBLE
        } else {
            tvServerError.visibility = View.VISIBLE
        }

        // Показываем контейнер
        container.visibility = View.VISIBLE

        // Автоматически скрываем через 3 секунды
        container.postDelayed({
            container.visibility = View.GONE
        }, 3000)
    }

}


