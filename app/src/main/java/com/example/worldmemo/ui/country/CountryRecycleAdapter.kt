package com.example.worldmemo.ui.country

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.worldmemo.R

class CountryRecycleAdapter(
    private val country : MutableList<CountryModel>,
): RecyclerView.Adapter<CountryRecycleAdapter.CountryViewHolder>() {


    inner class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        val countryName:TextView = itemView.findViewById(R.id.country_name)
        private val countryCard:CardView = itemView.findViewById(R.id.country_card_view)

        init {
            countryCard.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val countryName:String = view.findViewById<TextView?>(R.id.country_name).text.toString()
            val action = CountriesFragmentDirections.actionNavigationCountryToCountryFragment(countryName)
            Navigation.findNavController(view).navigate(action)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryRecycleAdapter.CountryViewHolder {
        return CountryViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.country_view_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val curCountry = country[position]

        holder.countryName.text = curCountry.name
    }

    override fun getItemCount(): Int {
        return country.size
    }


}