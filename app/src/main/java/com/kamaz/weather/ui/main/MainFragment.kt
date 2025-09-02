package com.kamaz.weather.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
    private lateinit var adapter: MainScreenAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = MainScreenAdapter { cityName ->
        }
        binding.rvVisibleCities.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MainFragment.adapter
            setHasFixedSize(true)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.visibleCities.collect { list ->
                        adapter.submitList(list)
                    }
                }
                launch {
                    viewModel.isConnected.collect { connected ->
                        if (!connected) showSnackbar(isNoInternet = true)
                    }
                }
            }
        }
        binding.ivGoToCities.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_cities)
        }

        binding.ivRefresh.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch { viewModel.refreshWeather() }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.refreshWeather()
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

        tvNoConnection.visibility = View.GONE
        tvServerError.visibility = View.GONE
        if (isNoInternet) tvNoConnection.visibility = View.VISIBLE else tvServerError.visibility = View.VISIBLE
        container.visibility = View.VISIBLE
        container.postDelayed({ container.visibility = View.GONE }, 3000)
    }
}
