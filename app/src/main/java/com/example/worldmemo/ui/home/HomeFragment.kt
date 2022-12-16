package com.example.worldmemo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldmemo.AudioModel
import com.example.worldmemo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var audioList : ArrayList<AudioModel>
    private lateinit var adapter : HomeRecyclerAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get the ViewModel to handle the Data of this fragment
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get the UI element
        val searchView = binding.searchView
        val recycleView = binding.homeRecyclerView
        recycleView.layoutManager = LinearLayoutManager(context)

        // Remove auto focus of the searchView
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                filteredAudio(filter)
                return true
            }

        })

        // Insert the data for audio list
        audioList = ArrayList<AudioModel>();

        audioList.add(AudioModel("Meu nome e", "Mon nom est", "Brazil"))
        audioList.add(AudioModel("what is thiss", "qu est ce aue cest ", "England"))
        audioList.add(AudioModel("spierdalaj kurwa", "tu es le plus beau", "poland"))
        audioList.add(AudioModel("Obuche", "reveille toi", "poland"))
        audioList.add(AudioModel("Meu nome e", "Mon nom est", "Brazil"))
        audioList.add(AudioModel("what is thiss", "qu est ce aue cest ", "England"))
        audioList.add(AudioModel("spierdalaj kurwa", "tu es le plus beau", "poland"))
        audioList.add(AudioModel("Obuche", "reveille toi", "poland"))
        audioList.add(AudioModel("Meu nome e", "Mon nom est", "Brazil"))
        audioList.add(AudioModel("what is thiss", "qu est ce aue cest ", "England"))
        audioList.add(AudioModel("spierdalaj kurwa", "tu es le plus beau", "poland"))
        audioList.add(AudioModel("Obuche", "reveille toi", "poland"))

        adapter = HomeRecyclerAdapter(audioList)
        recycleView.adapter=adapter


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun filteredAudio(filter:String?){

        if(filter == null) return

        val filterLower = filter.lowercase()

        val filteredList = ArrayList<AudioModel>()
        audioList.forEach {
            if(it.sentence.lowercase().contains(filterLower) ||it.translation.lowercase().contains(filterLower) || it.country.lowercase().contains(filterLower) ){
                filteredList.add(it)
            }
        }

        adapter.setFilteredList(filteredList)



    }
}


