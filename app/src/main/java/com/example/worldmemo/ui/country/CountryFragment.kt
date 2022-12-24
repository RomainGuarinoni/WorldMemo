package com.example.worldmemo.ui.country

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldmemo.R
import com.example.worldmemo.SQLiteHelper
import com.example.worldmemo.adapter.AudioRecyclerAdapter
import com.example.worldmemo.databinding.FragmentCountryBinding
import com.example.worldmemo.model.AudioModel


class CountryFragment : Fragment(), AudioRecyclerAdapter.Callbacks {

    private var _binding: FragmentCountryBinding? = null
    private val binding get() = _binding!!
    private lateinit var audioList: ArrayList<AudioModel>
    private lateinit var adapter: AudioRecyclerAdapter
    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var countryName: String
    private val args: CountryFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentCountryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sqliteHelper = SQLiteHelper(requireActivity())

        countryName = args.countryName

        audioList = sqliteHelper.getAudiosByCountry(countryName)

        // Get the UI views
        val searchView = binding.searchView
        val recycleView = binding.countryRecyclerView
        val buttonLayout = binding.countryDeleteButtonView
        val deleteButton = binding.countryDeleteButton

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
        deleteButton.setOnClickListener {
            adapter.deleteSelected()
        }

        adapter = AudioRecyclerAdapter(audioList, this)
        recycleView.layoutManager = LinearLayoutManager(context)
        recycleView.adapter = adapter

        return root
    }

    override fun onSelectStart() {
        val buttonLayout = binding.countryDeleteButtonView

        buttonLayout.visibility = View.VISIBLE

        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)

        buttonLayout.startAnimation(animation)

    }

    override fun onSelectEnd() {

        val buttonLayout = binding.countryDeleteButtonView


        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)

        buttonLayout.startAnimation(animation)

        buttonLayout.visibility = View.GONE

        Log.println(Log.INFO, "audio list size", audioList.size.toString())


    }

    override fun onDeleteAudio(audio: AudioModel) {
        val status = sqliteHelper.deleteAudio(audio)

        if (status == sqliteHelper.FAIL_STATUS) {
            Toast.makeText(requireActivity(), "Audio could not be deleted ...", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.println(Log.INFO, "life cycle", "Home fragment has been destroyed")
        _binding = null
    }

    fun filteredAudio(filter: String?) {

        if (filter == null) return

        val filterLower = filter.lowercase()

        val filteredList = ArrayList<AudioModel>()

        Log.println(Log.INFO, "audio list size", audioList.size.toString())


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