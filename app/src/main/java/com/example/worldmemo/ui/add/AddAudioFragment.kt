package com.example.worldmemo.ui.add

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.worldmemo.R
import com.example.worldmemo.SQLiteHelper
import com.example.worldmemo.model.AudioModel


class AddAudioFragment : Fragment() {

    private lateinit var sentenceInput: EditText
    private lateinit var translationInput: EditText
    private lateinit var spinner: Spinner
    private lateinit var countryInput: String
    private lateinit var path: String
    private lateinit var sqLiteHelper: SQLiteHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_audio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sqLiteHelper = SQLiteHelper(requireActivity())

        sentenceInput = view.findViewById(R.id.add_country_sentence)
        translationInput = view.findViewById(R.id.add_country_translation)

        spinner = view.findViewById(R.id.add_country_select)

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            requireActivity(), R.array.countries_name, android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.println(Log.DEBUG, "drop down add audio", "nothing has been selected")
                countryInput = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                countryInput = parent!!.getItemAtPosition(position).toString()
            }

        }

        val addButton: Button = view.findViewById(R.id.add_country_button)

        addButton.setOnClickListener { addAudio() }

    }

    private fun addAudio() {

        val sentence = sentenceInput.text.toString()
        val translation = translationInput.text.toString()

        if (sentence.isEmpty() || translation.isEmpty() || countryInput.isEmpty()) {
            Toast.makeText(requireActivity(), "Enter all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        val audio =
            AudioModel(sentence = sentence, translation = translation, country = countryInput, path = path)

        val status = sqLiteHelper.addAudio(audio)

        if (status > sqLiteHelper.FAIL_STATUS) {
            Toast.makeText(requireActivity(), "The audio has been added", Toast.LENGTH_SHORT).show()
            resetForm()
        } else {
            Toast.makeText(requireActivity(), "Audio could not be saved ...", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun resetForm() {
        sentenceInput.setText("")
        translationInput.setText("")
    }


}