package com.example.worldmemo.ui.add

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.worldmemo.R
import com.example.worldmemo.SQLiteHelper
import com.example.worldmemo.model.AudioModel
import com.example.worldmemo.utils.Countries
import com.example.worldmemo.utils.FileUtils
import java.io.File
import java.io.IOException


class AddAudioFragment : Fragment() {

    private lateinit var sentenceInput: EditText
    private lateinit var translationInput: EditText
    private lateinit var spinner: Spinner
    private lateinit var countryName: String
    private lateinit var countryCode: String
    private lateinit var startRecordButton: Button
    private lateinit var stopRecordButton: Button
    private lateinit var playRecordButton: Button
    private lateinit var deleteRecordButton: Button
    private var path: String = ""
    private lateinit var sqLiteHelper: SQLiteHelper
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var isAudioRecorded = false

    private var PATH_KEY = "path"
    private var PATH_IS_AUDIO_RECORDED = "isAudioRecorded"

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(PATH_KEY, path)
        outState.putBoolean(PATH_IS_AUDIO_RECORDED, isAudioRecorded)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (path == "") {
            path = savedInstanceState?.getString(PATH_KEY) ?: ""
        }
        if (!isAudioRecorded) {
            isAudioRecorded = savedInstanceState?.getBoolean(PATH_IS_AUDIO_RECORDED) ?: false
        }

        if (isAudioRecorded) {
            startRecordButton.visibility = Button.INVISIBLE
            stopRecordButton.visibility = Button.INVISIBLE
            playRecordButton.visibility = Button.VISIBLE
            deleteRecordButton.visibility = Button.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_audio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sqLiteHelper = SQLiteHelper(requireActivity())

        // Get UI element
        sentenceInput = view.findViewById(R.id.add_country_sentence)
        translationInput = view.findViewById(R.id.add_country_translation)
        spinner = view.findViewById(R.id.add_country_select)
        startRecordButton = view.findViewById(R.id.add_audio_start_record_button)
        stopRecordButton = view.findViewById(R.id.add_audio_stop_record_button)
        playRecordButton = view.findViewById(R.id.add_audio_play_record_button)
        deleteRecordButton = view.findViewById(R.id.add_audio_delete_record_button)
        val addButton: Button = view.findViewById(R.id.add_country_button)

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireActivity(), android.R.layout.simple_spinner_item, Countries.getCountries()
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.println(Log.DEBUG, "drop down add audio", "nothing has been selected")
                countryName = ""
                countryCode= ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {

                val country =parent!!.getItemAtPosition(position).toString()

                countryName = country
                countryCode = Countries.getCountryCode(country)

            }

        }


        addButton.setOnClickListener { addAudio() }

        startRecordButton.setOnClickListener {
            startRecording()
        }
        stopRecordButton.setOnClickListener {
            stopRecording()

        }
        playRecordButton.setOnClickListener {
            playAudio()
        }
        deleteRecordButton.setOnClickListener {
            deleteAudio()
        }

        // Handle a possible intent
        val intent = requireActivity().intent

        if (intent.type != null && intent.type!!.startsWith("audio/")) {
            (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {


                path = FileUtils.saveUriToFile(requireActivity(), it, FileUtils.FileType.AUDIO)


                isAudioRecorded = true

                startRecordButton.visibility = Button.INVISIBLE
                stopRecordButton.visibility = Button.INVISIBLE
                playRecordButton.visibility = Button.VISIBLE
                deleteRecordButton.visibility = Button.VISIBLE

            }
        }

    }

    private fun startRecording() {

        path = FileUtils.createFile(FileUtils.FileType.AUDIO, requireActivity())

        recorder = MediaRecorder(requireActivity()).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(16)
            setOutputFile(path)
            try {
                prepare()
            } catch (e: IOException) {
                Log.println(Log.ERROR, "audio recorder", "failed to prepare the audio")
            }

            startRecordButton.isEnabled = false

            start()

            Toast.makeText(requireActivity(), "Record start", Toast.LENGTH_SHORT).show()

            startRecordButton.visibility = Button.INVISIBLE
            startRecordButton.isEnabled = true
            stopRecordButton.visibility = Button.VISIBLE
        }


    }

    private fun stopRecording() {

        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null
        isAudioRecorded = true

        stopRecordButton.visibility = Button.INVISIBLE
        playRecordButton.visibility = Button.VISIBLE
        deleteRecordButton.visibility = Button.VISIBLE
    }

    private fun playAudio() {


        player = MediaPlayer().apply {
            try {
                setDataSource(path)
                prepare()
                start()
            } catch (e: IOException) {
                Log.println(Log.ERROR, "audio player", "failed to prepare the audio")
                Log.println(Log.ERROR, "audio player path", path)
                Log.println(Log.ERROR, "audio player", e.stackTraceToString())

            }
        }


    }

    private fun deleteAudio() {

        val file = File(path)

        val success = file.delete()

        if (success) {
            Toast.makeText(requireActivity(), "Record deleted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireActivity(), "impossible to delete", Toast.LENGTH_SHORT).show()
        }

        if (player != null && player!!.isPlaying) {
            player!!.stop()
        }

        isAudioRecorded = false


        startRecordButton.visibility = Button.VISIBLE
        stopRecordButton.visibility = Button.INVISIBLE
        playRecordButton.visibility = Button.INVISIBLE
        deleteRecordButton.visibility = Button.INVISIBLE

    }


    private fun addAudio() {

        val sentence = sentenceInput.text.toString()
        val translation = translationInput.text.toString()

        if (sentence.isEmpty() || translation.isEmpty() || countryCode.isEmpty()|| countryName.isEmpty()) {
            Toast.makeText(requireActivity(), "Please, enter all the fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (!isAudioRecorded || path == "") {
            Toast.makeText(requireActivity(), "PLease, record an audio", Toast.LENGTH_SHORT).show()
            return
        }


        val audio = AudioModel(
            sentence = sentence, translation = translation, country = countryName, countryCode =  countryCode, path = path
        )

        val status = sqLiteHelper.addAudio(audio)

        if (status > sqLiteHelper.FAIL_STATUS) {
            Toast.makeText(requireActivity(), "The audio has been added", Toast.LENGTH_SHORT).show()
            resetForm()
        } else {
            Toast.makeText(requireActivity(), "Audio could not be saved ...", Toast.LENGTH_SHORT)
                .show()
        }

        startRecordButton.visibility = Button.VISIBLE
        stopRecordButton.visibility = Button.INVISIBLE
        playRecordButton.visibility = Button.INVISIBLE
        deleteRecordButton.visibility = Button.INVISIBLE

        path = ""

    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        recorder?.release()


        player = null
        recorder = null

    }


    private fun resetForm() {
        sentenceInput.setText("")
        translationInput.setText("")
    }


}