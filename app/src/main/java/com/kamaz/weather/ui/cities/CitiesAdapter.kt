package com.kamaz.weather.ui.cities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kamaz.weather.R
import com.kamaz.weather.common.model.City

class CitiesAdapter(
    private val onEdit: (City) -> Unit,
    private val onDelete: (City) -> Unit
) : ListAdapter<City, CitiesAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<City>() {
        override fun areItemsTheSame(oldItem: City, newItem: City) =
            oldItem.englishName == newItem.englishName

        override fun areContentsTheSame(oldItem: City, newItem: City) =
            oldItem == newItem
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCityName = itemView.findViewById<TextView>(R.id.tvCityName)
        private val tvLat = itemView.findViewById<TextView>(R.id.tvLat)
        private val tvLon = itemView.findViewById<TextView>(R.id.tvLon)

        fun bind(city: City) {
            tvCityName.text = city.englishName
            tvLat.text = itemView.context.getString(R.string.lat_format, city.latitude)
            tvLon.text = itemView.context.getString(R.string.lon_format, city.longitude)

            itemView.setOnClickListener { onEdit(city) }
            itemView.setOnLongClickListener { onDelete(city); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_city_name, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))
}
