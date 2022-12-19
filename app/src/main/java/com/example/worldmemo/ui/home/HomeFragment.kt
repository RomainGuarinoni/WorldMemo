package com.example.worldmemo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldmemo.AudioModel
import com.example.worldmemo.R
import com.example.worldmemo.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), HomeRecyclerAdapter.HandleSelect {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var audioList: ArrayList<AudioModel>
    private lateinit var adapter: HomeRecyclerAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Get the ViewModel to handle the Data of this fragment
       /* val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)*/

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Get the UI element
        val searchView = binding.searchView
        val recycleView = binding.homeRecyclerView
        val buttonLayout = binding.homeDeleteButtonView
        val deleteButton = binding.homeDeleteButton
        recycleView.layoutManager = LinearLayoutManager(context)

        // Remove auto focus of the searchView
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(filter: String?): Boolean {
                filteredAudio(filter)
                return true
            }

        })

        buttonLayout.visibility = View.GONE

        deleteButton.setOnClickListener{
            adapter.deleteSelected()
        }


        // Insert the data for audio list
        audioList = ArrayList<AudioModel>()

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

        audioList.add(AudioModel("enorme dinguerie", "tu es le plus beau", "poland"))
        audioList.add(AudioModel("Obuche", "reveille toi", "poland"))
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


        adapter = HomeRecyclerAdapter(audioList, this)
        recycleView.adapter = adapter


        return root
    }


    override fun onSelectStart() {
        val buttonLayout = binding.homeDeleteButtonView

        buttonLayout.visibility = View.VISIBLE

        val animation = AnimationUtils.loadAnimation(context,R.anim.slide_up)

        buttonLayout.startAnimation(animation)

    }

    override fun onSelectEnd() {

        val buttonLayout = binding.homeDeleteButtonView


        val animation = AnimationUtils.loadAnimation(context,R.anim.slide_down)

        buttonLayout.startAnimation(animation)

        buttonLayout.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun filteredAudio(filter: String?) {

        if (filter == null) return

        val filterLower = filter.lowercase()

        val filteredList = ArrayList<AudioModel>()
        audioList.forEach {
            if (it.sentence.lowercase().contains(filterLower) || it.translation.lowercase()
                    .contains(filterLower) || it.country.lowercase().contains(filterLower)
            ) {
                filteredList.add(it)
            }
        }

        adapter.setFilteredList(filteredList)


    }
}


