package com.example.worldmemo.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.worldmemo.SQLiteHelper
import com.example.worldmemo.adapter.AudioRecyclerAdapter
import com.example.worldmemo.adapter.Callbacks
import com.example.worldmemo.adapter.PhotoRecyclerAdapter
import com.example.worldmemo.databinding.FragmentHomeBinding
import com.example.worldmemo.model.AudioModel
import com.example.worldmemo.model.PhotoModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class HomeFragment : Fragment() {

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
        val updateButton = binding.homeUpdateButton
        val tabLayout = binding.homeTableLayout

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
            } else if (currentTab == PHOTO_TAB) {
                photoAdapter.shareSelected()
            }
        }

        updateButton.setOnClickListener {

            val action = if (currentTab == AUDIO_TAB) {
                val selectedId = audioAdapter.getUpdateId()

                HomeFragmentDirections.actionNavigationHomeToAddAudioFragment(selectedId)

            } else {
                val selectedId = photoAdapter.getUpdateId()

                HomeFragmentDirections.actionNavigationHomeToAddPhotoFragment2(selectedId)
            }

            Navigation.findNavController(root).navigate(action)
        }

        // Insert the data for audio list and photo list
        audioList = sqliteHelper.getAllAudio()
        photoList = sqliteHelper.getAllPhotos()

        val audioCallbacks = Callbacks<AudioModel>(requireActivity(), buttonLayout, updateButton) {
            val status = sqliteHelper.deleteAudio(it)

            if (status == sqliteHelper.FAIL_STATUS) {
                Toast.makeText(
                    requireActivity(), "Audio could not be deleted ...", Toast.LENGTH_SHORT
                ).show()
            }
        }

        val photoCallbacks = Callbacks<PhotoModel>(requireActivity(), buttonLayout, updateButton) {
            val status = sqliteHelper.deletePhoto(it)

            if (status == sqliteHelper.FAIL_STATUS) {
                Toast.makeText(
                    requireActivity(), "Audio could not be deleted ...", Toast.LENGTH_SHORT
                ).show()
            }
        }

        audioAdapter = AudioRecyclerAdapter(audioList, audioCallbacks, requireActivity())
        photoAdapter = PhotoRecyclerAdapter(photoList, photoCallbacks, requireActivity())
        recycleView.layoutManager = LinearLayoutManager(context)
        recycleView.adapter = audioAdapter

        if (audioList.size == 0 && photoList.size > 0) {
            recycleView.adapter = photoAdapter
            currentTab = PHOTO_TAB
            tabLayout.getTabAt(PHOTO_TAB)?.select()
        }

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tabLayout.selectedTabPosition == AUDIO_TAB) {
                    recycleView.adapter = audioAdapter
                    currentTab = AUDIO_TAB

                    if (audioAdapter.hasItemSelected() && buttonLayout.visibility == View.GONE) {
                        // we let the buttons share and deleted visible because there is item selected too
                        audioCallbacks.onSelectStart()


                    } else if (!audioAdapter.hasItemSelected() && buttonLayout.visibility == View.VISIBLE) {
                        // We hide the buttons because nothing is selected in this tab
                        audioCallbacks.onSelectEnd()
                    }

                    // We want to hide the update button if there is multiple item selected
                    if (audioAdapter.hasMultipleItemSelected() && updateButton.visibility == Button.VISIBLE) {
                        audioCallbacks.onMultipleItemSelected()
                    } else if (!audioAdapter.hasMultipleItemSelected() && updateButton.visibility == Button.GONE) {
                        audioCallbacks.onOneItemSelected()
                    }

                } else if (tabLayout.selectedTabPosition == PHOTO_TAB) {
                    recycleView.adapter = photoAdapter
                    currentTab = PHOTO_TAB


                    if (photoAdapter.hasItemSelected() && buttonLayout.visibility == View.GONE) {
                        // we let the buttons share and deleted visible because there is item selected too
                        audioCallbacks.onSelectStart()


                    } else if (!photoAdapter.hasItemSelected() && buttonLayout.visibility == View.VISIBLE) {
                        // We hide the buttons because nothing is selected in this tab
                        audioCallbacks.onSelectEnd()
                    }

                    // We want to hide the update button if there is multiple item selected
                    if (photoAdapter.hasMultipleItemSelected() && updateButton.visibility == Button.VISIBLE) {
                        audioCallbacks.onMultipleItemSelected()
                    } else if (!photoAdapter.hasMultipleItemSelected() && updateButton.visibility == Button.GONE) {
                        audioCallbacks.onOneItemSelected()
                    }

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.println(Log.INFO, "life cycle", "Home fragment has been destroyed")
        _binding = null
    }

    fun filteredItems(filter: String?) {

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


        audioAdapter.setFilteredListAudio(filteredAudioList)
        photoAdapter.setFilteredListPhoto(filteredPhotoList)


    }


}


