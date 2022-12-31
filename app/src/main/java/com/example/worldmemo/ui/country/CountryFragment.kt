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
import com.example.worldmemo.adapter.PhotoRecyclerAdapter
import com.example.worldmemo.databinding.FragmentCountryBinding
import com.example.worldmemo.model.AudioModel
import com.example.worldmemo.model.PhotoModel
import com.google.android.material.tabs.TabLayout


class CountryFragment : Fragment(), AudioRecyclerAdapter.Callbacks, PhotoRecyclerAdapter.Callbacks {

    private var _binding: FragmentCountryBinding? = null
    private val binding get() = _binding!!
    private lateinit var audioList: ArrayList<AudioModel>
    private lateinit var photoList: ArrayList<PhotoModel>
    private lateinit var audioAdapter: AudioRecyclerAdapter
    private lateinit var photoAdapter: PhotoRecyclerAdapter
    private lateinit var sqliteHelper: SQLiteHelper
    private lateinit var countryName: String
    private val args: CountryFragmentArgs by navArgs()

    private val AUDIO_TAB = 0
    private val PHOTO_TAB = 1
    private var currentTab = AUDIO_TAB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentCountryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sqliteHelper = SQLiteHelper(requireActivity())

        countryName = args.countryName

        audioList = sqliteHelper.getAudiosByCountry(countryName)
        photoList = sqliteHelper.getPhotosByCountry(countryName)

        // Get the UI views
        val searchView = binding.searchView
        val recycleView = binding.countryRecyclerView
        val buttonLayout = binding.countryDeleteButtonView
        val deleteButton = binding.countryDeleteButton
        val shareButton = binding.countryShareButton
        val tabLayout = binding.countryTableLayout

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
            if (currentTab == AUDIO_TAB) {
                audioAdapter.deleteSelected()
            } else if (currentTab == PHOTO_TAB) {
                photoAdapter.deleteSelected()
            }
        }
        shareButton.setOnClickListener {
            if(currentTab == AUDIO_TAB){
                audioAdapter.shareSelected()
            }
        }

        audioAdapter = AudioRecyclerAdapter(audioList, this, requireActivity())
        photoAdapter = PhotoRecyclerAdapter(photoList, this, requireActivity())
        recycleView.layoutManager = LinearLayoutManager(context)
        recycleView.adapter = audioAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tabLayout.selectedTabPosition == AUDIO_TAB) {
                    recycleView.adapter = audioAdapter
                    currentTab = AUDIO_TAB

                    if (audioAdapter.hasItemSelected() && buttonLayout.visibility == View.GONE) {
                        // we let the buttons share and deleted visible because there is item selected too
                        onSelectStart()
                    } else if (!audioAdapter.hasItemSelected() && buttonLayout.visibility == View.VISIBLE) {
                        // We hide the buttons because nothing is selected in this tab
                        onSelectEnd()
                    }

                } else if (tabLayout.selectedTabPosition == PHOTO_TAB) {
                    recycleView.adapter = photoAdapter
                    currentTab = PHOTO_TAB


                    if (photoAdapter.hasItemSelected() && buttonLayout.visibility == View.GONE) {
                        // we let the buttons share and deleted visible because there is item selected too
                        onSelectStart()
                    } else if (!photoAdapter.hasItemSelected() && buttonLayout.visibility == View.VISIBLE) {
                        // We hide the buttons because nothing is selected in this tab
                        onSelectEnd()
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

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

    override fun onDeletePhoto(photo: PhotoModel) {
        val status = sqliteHelper.deletePhoto(photo)

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

        val filteredAudioList = ArrayList<AudioModel>()
        val filteredPhotoList = ArrayList<PhotoModel>()



        audioList.forEach {
            if (it.sentence.lowercase().contains(filterLower) || it.translation.lowercase()
                    .contains(filterLower) || it.country.lowercase().contains(filterLower)
            ) {
                filteredAudioList.add(it)
            }
        }

        photoList.forEach {
            if (it.title.lowercase().contains(filterLower) || it.description.lowercase()
                    .contains(filterLower) || it.country.lowercase().contains(filterLower)
            ) {
                filteredPhotoList.add(it)
            }
        }


        audioAdapter.setFilteredList(filteredAudioList)
        photoAdapter.setFilteredList(filteredPhotoList)


    }

}