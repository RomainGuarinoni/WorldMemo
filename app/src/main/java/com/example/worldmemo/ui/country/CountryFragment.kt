package com.example.worldmemo.ui.country

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.worldmemo.R
import com.example.worldmemo.SQLiteHelper


class CountryFragment : Fragment() {

    private lateinit var sqLiteHelper: SQLiteHelper

    private lateinit var countryName:String

    private val args : CountryFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_country, container, false)

        sqLiteHelper=SQLiteHelper(requireActivity())

        countryName = args.countryName

        return view
    }


}