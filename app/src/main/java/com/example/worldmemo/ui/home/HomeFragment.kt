package com.example.worldmemo.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.worldmemo.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

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
        val textView: TextView = binding.textHome
        val button:Button = binding.changeText
        val searchView = binding.searchView

        // Set the observer for the text
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        // Set the event listener on the button
        button.setOnClickListener{
            homeViewModel.changeText()
        }


        // Remove auto focus of the searchView
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0 != null) homeViewModel.changeText(p0)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


