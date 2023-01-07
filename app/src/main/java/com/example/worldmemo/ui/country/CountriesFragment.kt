package com.example.worldmemo.ui.country

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.worldmemo.SQLiteHelper
import com.example.worldmemo.adapter.CountryRecycleAdapter
import com.example.worldmemo.databinding.FragmentCountriesBinding
import com.example.worldmemo.model.CountryModel

class CountriesFragment : Fragment() {

    private var _binding: FragmentCountriesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var countryList: ArrayList<CountryModel>
    private lateinit var adapter: CountryRecycleAdapter

    private lateinit var sqliteHelper: SQLiteHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCountriesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get the UI element
        val recycleView = binding.countryRecyclerView
        val searchView = binding.searchView

        recycleView.layoutManager = GridLayoutManager(context, 2)

        sqliteHelper = SQLiteHelper(requireActivity())

        // Remove auto focus of the searchView
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {

                filteredItems(filter)
                return true
            }

        })


        countryList = sqliteHelper.getAllCountries()

        adapter = CountryRecycleAdapter(countryList)
        recycleView.adapter = adapter

        return root
    }

    fun filteredItems(filter: String?) {

        if (filter == null) return

        val filterLower = filter.lowercase()

        val filteredCountryList = ArrayList<CountryModel>()

        countryList.forEach {
            if (it.name.lowercase().contains(filterLower) || it.code.lowercase()
                    .contains(filterLower)
            ) {
                filteredCountryList.add(it)
            }
        }

        adapter.setFilteredList(filteredCountryList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}