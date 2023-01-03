package com.example.worldmemo.ui.add

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.example.worldmemo.R
import com.example.worldmemo.SQLiteHelper
import com.example.worldmemo.model.PhotoModel
import com.example.worldmemo.utils.Countries
import com.example.worldmemo.utils.FileUtils
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File


class AddPhotoFragment : Fragment() {

    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var spinner: Spinner
    private lateinit var countryName: String
    private lateinit var countryCode: String
    private lateinit var imagePreview: ImageView
    private var uri: Uri? = null
    private lateinit var sqLiteHelper: SQLiteHelper


    private var PATH_URI = "path"

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(PATH_URI, uri.toString())
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (uri == null && savedInstanceState?.getString(PATH_URI) != null) {
            uri = Uri.parse(savedInstanceState.getString(PATH_URI))
            imagePreview.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sqLiteHelper = SQLiteHelper(requireActivity())


        // Get UI element
        titleInput = view.findViewById(R.id.add_country_title)
        descriptionInput = view.findViewById(R.id.add_country_description)
        spinner = view.findViewById(R.id.add_country_select)
        imagePreview = view.findViewById(R.id.add_photo_preview)

        val addButton: Button = view.findViewById(R.id.add_country_button)
        val takePictureButton: Button = view.findViewById(R.id.take_photo_button)


        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireActivity(), android.R.layout.simple_spinner_item, Countries.getCountries()
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.println(Log.DEBUG, "drop down add audio", "nothing has been selected")
                countryName = ""
                countryCode = ""
            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val country = parent!!.getItemAtPosition(position).toString()

                countryName = country
                countryCode = Countries.getCountryCode(country)
            }

        }



        addButton.setOnClickListener { addPhoto() }

        takePictureButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080, 1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .start()
        }


        // Handle a possible intent
        val intent = requireActivity().intent

        if (intent.type != null && intent.type!!.startsWith("image/")) {
            (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {

                val filePath =
                    FileUtils.saveUriToFile(requireActivity(), it, FileUtils.FileType.PHOTO)

                uri = File(filePath).toUri()

                imagePreview.setImageURI(uri)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            uri = data?.data!!


            // Use Uri object instead of File to avoid storage permissions
            imagePreview.setImageURI(uri)
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPhoto() {

        val title = titleInput.text.toString()
        val description = descriptionInput.text.toString()

        if (title.isEmpty() || description.isEmpty() || countryCode.isEmpty() || countryName.isEmpty()) {
            Toast.makeText(requireActivity(), "Please, enter all the fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (uri == null) {
            Toast.makeText(requireActivity(), "PLease, select or take a photo", Toast.LENGTH_SHORT)
                .show()
            return
        }


        val photo = PhotoModel(
            title = title,
            description = description,
            country = countryName,
            countryCode = countryCode,
            path = uri.toString()
        )

        val status = sqLiteHelper.addPhoto(photo)

        if (status > sqLiteHelper.FAIL_STATUS) {
            Toast.makeText(requireActivity(), "The photo has been added", Toast.LENGTH_SHORT).show()
            resetForm()
        } else {
            Toast.makeText(requireActivity(), "Photo could not be saved ...", Toast.LENGTH_SHORT)
                .show()
        }

        uri = null
        imagePreview.setImageURI(null)

    }

    private fun resetForm() {
        titleInput.setText("")
        descriptionInput.setText("")
    }


}