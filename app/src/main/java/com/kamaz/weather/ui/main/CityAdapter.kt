package com.kamaz.weather.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.kamaz.weather.R
import com.kamaz.weather.common.model.WeatherUiModel

class CityAdapter(
    private val onCityClick: (String) -> Unit
) : ListAdapter<WeatherUiModel, CityAdapter.CityViewHolder>(WeatherDiffCallback()) {

    private var expandedCity: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(getItem(position), getItem(position).cityName == expandedCity)
    }

    inner class CityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCityName: TextView = itemView.findViewById(R.id.tvCityName)
        private val tvTemperature: TextView = itemView.findViewById(R.id.tvTemperature)
        private val tvWindSpeed: TextView = itemView.findViewById(R.id.tvWindSpeed)
        private val tvWindDirection: TextView = itemView.findViewById(R.id.tvWindDirection)
        private val ivWeatherIcon: ImageView = itemView.findViewById(R.id.ivWeatherIcon)
        private val expandedContent: View = itemView.findViewById(R.id.expandedContent)

        fun bind(model: WeatherUiModel, isExpanded: Boolean) {
            tvCityName.text = model.cityName
            tvTemperature.text = model.temperature
            tvWindSpeed.text = model.windSpeed
            tvWindDirection.text = model.windDirection
            ivWeatherIcon.setImageResource(R.drawable.baseline_wb_sunny_24)

            expandedContent.visibility = if (isExpanded) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                expandedCity = if (isExpanded) null else model.cityName
                notifyDataSetChanged()
                onCityClick(model.cityName)
            }
        }
    }

    class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherUiModel>() {
        override fun areItemsTheSame(oldItem: WeatherUiModel, newItem: WeatherUiModel): Boolean =
            oldItem.cityName == newItem.cityName

        override fun areContentsTheSame(oldItem: WeatherUiModel, newItem: WeatherUiModel): Boolean =
            oldItem == newItem
    }
}



