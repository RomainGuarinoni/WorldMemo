package com.example.worldmemo.ui.country

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.worldmemo.databinding.FragmentCountryBinding

class CountryFragment : Fragment() {

    private var _binding: FragmentCountryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var countryList : ArrayList<CountryModel>
    private lateinit var adapter : CountryRecycleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCountryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get the UI element
        val recycleView = binding.countryRecyclerView
        val searchView = binding.searchView

        recycleView.layoutManager = GridLayoutManager(context,2)


        // Remove auto focus of the searchView
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {

                //TODO filter country here
                return true
            }

        })

        countryList = ArrayList<CountryModel>()

        countryList.add(CountryModel("France"))
        countryList.add(CountryModel("Brazil"))
        countryList.add(CountryModel("Hong-Kong"))
        countryList.add(CountryModel("Poland"))
        countryList.add(CountryModel("Germany"))

        adapter = CountryRecycleAdapter(countryList)
        recycleView.adapter = adapter

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}