package com.example.worldmemo

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import coil.load

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val fullScreenImageView: ImageView = findViewById(R.id.fullScreenImageView)

        val callingActivityIntent = intent

        if (callingActivityIntent != null) {
            val imageUri = callingActivityIntent.data
            fullScreenImageView.load(imageUri)
        }


    }


}