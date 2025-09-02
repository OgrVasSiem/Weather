package com.kamaz.weather.ui.cities

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputEditText
import com.kamaz.weather.R
import com.kamaz.weather.common.model.City
import com.kamaz.weather.databinding.FragmentCitiesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CitiesFragment : Fragment() {

    private var _binding: FragmentCitiesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CitiesViewModel by viewModels()
    private lateinit var adapter: CitiesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.fabAddCity.setOnClickListener { showAddDialog() }

        adapter = CitiesAdapter(
            onEdit = { city -> showEditDialog(city) },
            onDelete = { city -> confirmDelete(city) }
        )

        binding.recyclerCities.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerCities.setHasFixedSize(true)
        binding.recyclerCities.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.allCities.collect { cities ->
                adapter.submitList(cities)
            }
        }
    }

    private fun showAddDialog() {
        val ctx = requireContext()
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(0))
        }

        val nameInput = TextInputEditText(ctx).apply {
            hint = getString(R.string.city_name_hint)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        }
        val latInput = TextInputEditText(ctx).apply {
            hint = getString(R.string.latitude_hint)
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        val lonInput = TextInputEditText(ctx).apply {
            hint = getString(R.string.longitude_hint)
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        val visibleSwitch = MaterialSwitch(ctx).apply {
            text = getString(R.string.show_on_main)
        }

        container.addView(nameInput)
        container.addView(space(ctx, 8))
        container.addView(latInput)
        container.addView(space(ctx, 8))
        container.addView(lonInput)
        container.addView(space(ctx, 8))
        container.addView(visibleSwitch)

        MaterialAlertDialogBuilder(ctx)
            .setTitle(getString(R.string.dlg_add_title))
            .setView(container)
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setPositiveButton(getString(R.string.btn_add)) { _, _ ->
                val name = nameInput.text?.toString()?.trim().orEmpty()
                val lat = latInput.text?.toString()?.replace(',', '.')?.toDoubleOrNull()
                val lon = lonInput.text?.toString()?.replace(',', '.')?.toDoubleOrNull()
                val makeVisible = visibleSwitch.isChecked

                if (name.isBlank() || lat == null || lon == null || !isLatLonValid(lat, lon)) {
                    Toast.makeText(ctx, getString(R.string.msg_check_fields), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    val ok = viewModel.addCity(name, lat, lon, makeVisible)
                    if (!ok) {
                        Toast.makeText(ctx, getString(R.string.msg_add_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showEditDialog(city: City) {
        val ctx = requireContext()
        val container = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(0))
        }

        val title = TextView(ctx).apply {
            text = city.englishName
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium)
        }
        val latInput = TextInputEditText(ctx).apply {
            hint = getString(R.string.latitude_hint)
            setText(city.latitude.toString())
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
        }
        val lonInput = TextInputEditText(ctx).apply {
            hint = getString(R.string.longitude_hint)
            setText(city.longitude.toString())
            inputType = InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL or
                    InputType.TYPE_NUMBER_FLAG_SIGNED
        }

        container.addView(title)
        container.addView(space(ctx, 8))
        container.addView(latInput)
        container.addView(space(ctx, 8))
        container.addView(lonInput)

        MaterialAlertDialogBuilder(ctx)
            .setTitle(getString(R.string.dlg_edit_title))
            .setView(container)
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setPositiveButton(getString(R.string.btn_save)) { _, _ ->
                val lat = latInput.text?.toString()?.replace(',', '.')?.toDoubleOrNull()
                val lon = lonInput.text?.toString()?.replace(',', '.')?.toDoubleOrNull()
                if (lat == null || lon == null || !isLatLonValid(lat, lon)) {
                    Toast.makeText(ctx, getString(R.string.msg_invalid_coords), Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.editCityCoordinates(city.englishName, lat, lon)
                }
            }
            .show()
    }

    private fun confirmDelete(city: City) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dlg_delete_title))
            .setMessage(getString(R.string.dlg_delete_message, city.englishName))
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .setPositiveButton(getString(R.string.btn_delete)) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteCity(city.englishName)
                }
            }
            .show()
    }

    private fun isLatLonValid(lat: Double, lon: Double) =
        lat in -90.0..90.0 && lon in -180.0..180.0

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()

    private fun space(ctx: Context, dp: Int) = Space(ctx).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, this@CitiesFragment.dp(dp))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
