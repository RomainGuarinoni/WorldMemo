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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AddAudioFragment : Fragment() {

    private lateinit var sentenceInput: EditText
    private lateinit var translationInput: EditText
    private lateinit var spinner: Spinner
    private lateinit var countryInput: String
    private lateinit var startRecordButton: Button
    private lateinit var stopRecordButton: Button
    private lateinit var playRecordButton: Button
    private lateinit var deleteRecordButton: Button
    private lateinit var path: String
    private lateinit var sqLiteHelper: SQLiteHelper
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    private var isAudioRecorded = false


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

                saveUriToFile(it)

                isAudioRecorded = true

                startRecordButton.visibility = Button.INVISIBLE
                stopRecordButton.visibility = Button.INVISIBLE
                playRecordButton.visibility = Button.VISIBLE
                deleteRecordButton.visibility = Button.VISIBLE

            }
        }

    }

    private fun createAudioFile(): String {

        val formatter = DateTimeFormatter.ofPattern("YYYY_MM_DD_HH_MM_SS")
        val currentDate = LocalDateTime.now().format(formatter)
        val fileName = "${currentDate}_audio.aac"

        return "${requireActivity().getExternalFilesDir(null)?.absolutePath}/$fileName"

    }

    private fun startRecording() {

        path = createAudioFile()

        recorder = MediaRecorder(requireActivity()).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setAudioSamplingRate(44100);
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

        if (sentence.isEmpty() || translation.isEmpty() || countryInput.isEmpty()) {
            Toast.makeText(requireActivity(), "Please, enter all the fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (!isAudioRecorded) {
            Toast.makeText(requireActivity(), "PLease, record an audio", Toast.LENGTH_SHORT).show()
            return
        }


        val audio = AudioModel(
            sentence = sentence, translation = translation, country = countryInput, path = path
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

    private fun saveUriToFile(uri: Uri) {
        val inputStream = requireActivity().contentResolver.openInputStream(uri)!!
        val filePath = createAudioFile()
        val out: OutputStream = FileOutputStream(File(filePath))
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        out.close()
        inputStream.close()
        path = filePath
    }


}