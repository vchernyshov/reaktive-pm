package dev.garage.rpm.app.main.ui.country

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.garage.rpm.app.R
import dev.garage.rpm.app.main.extensions.inflate
import dev.garage.rpm.app.main.util.Country
import kotlinx.android.synthetic.main.item_country.view.*

class CountriesAdapter(
    private var countries: List<Country>?,
    private val itemClickListener: (country: Country) -> Unit
) : RecyclerView.Adapter<CountriesAdapter.ViewHolder>() {

    fun setData(countries: List<Country>) {
        this.countries = countries
        notifyDataSetChanged()
    }

    override fun getItemCount() = countries?.size ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_country))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(countries!![position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private lateinit var country: Country

        init {
            itemView.setOnClickListener {
                itemClickListener.invoke(country)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(country: Country) {
            this.country = country
            itemView.countryName.text = country.name
            itemView.countryCode.text = "+${country.countryCallingCode}"
        }
    }
}
