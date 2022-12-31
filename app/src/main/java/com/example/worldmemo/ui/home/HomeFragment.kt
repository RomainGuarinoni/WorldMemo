package com.example.worldmemo.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldmemo.R
import com.example.worldmemo.SQLiteHelper
import com.example.worldmemo.adapter.AudioRecyclerAdapter
import com.example.worldmemo.adapter.PhotoRecyclerAdapter
import com.example.worldmemo.databinding.FragmentHomeBinding
import com.example.worldmemo.model.AudioModel
import com.example.worldmemo.model.PhotoModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class HomeFragment : Fragment(), AudioRecyclerAdapter.Callbacks, PhotoRecyclerAdapter.Callbacks {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var audioList: ArrayList<AudioModel>
    private lateinit var photoList: ArrayList<PhotoModel>
    private lateinit var audioAdapter: AudioRecyclerAdapter
    private lateinit var photoAdapter: PhotoRecyclerAdapter
    private lateinit var sqliteHelper: SQLiteHelper

    private val AUDIO_TAB = 0
    private val PHOTO_TAB = 1
    private var currentTab = AUDIO_TAB


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Get the ViewModel to handle the Data of this fragment
        /* val homeViewModel =
             ViewModelProvider(this).get(HomeViewModel::class.java)*/

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sqliteHelper = SQLiteHelper(requireActivity())

        // Get the UI views
        val searchView = binding.searchView
        val recycleView = binding.homeRecyclerView
        val buttonLayout = binding.homeDeleteButtonView
        val deleteButton = binding.homeDeleteButton
        val shareButton = binding.homeShareButton
        val tabLayout = binding.homeTableLayout

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
            if (currentTab == AUDIO_TAB) {
                audioAdapter.shareSelected()
            }
        }


        // Insert the data for audio list and photo list
        audioList = sqliteHelper.getAllAudio()
        photoList = sqliteHelper.getAllPhotos()



        audioAdapter = AudioRecyclerAdapter(audioList, this, requireActivity())
        photoAdapter = PhotoRecyclerAdapter(photoList, this, requireActivity())
        recycleView.layoutManager = LinearLayoutManager(context)
        recycleView.adapter = audioAdapter

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
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
        val buttonLayout = binding.homeDeleteButtonView

        buttonLayout.visibility = View.VISIBLE

        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)

        buttonLayout.startAnimation(animation)

    }


    override fun onSelectEnd() {

        val buttonLayout = binding.homeDeleteButtonView


        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)

        buttonLayout.startAnimation(animation)

        buttonLayout.visibility = View.GONE

        Log.println(Log.INFO, "audio list size", audioList.size.toString())


    }

    override fun onDeletePhoto(photo: PhotoModel) {
        val status = sqliteHelper.deletePhoto(photo)

        if (status == sqliteHelper.FAIL_STATUS) {
            Toast.makeText(requireActivity(), "Audio could not be deleted ...", Toast.LENGTH_SHORT)
                .show()
        }
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


