package com.example.worldmemo.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.worldmemo.ImageActivity
import com.example.worldmemo.R
import com.example.worldmemo.model.PhotoModel
import java.io.File


class PhotoRecyclerAdapter(
    private var photos: MutableList<PhotoModel>,
    val callbacks: Callbacks<PhotoModel>,
    val context: Context
) : SelectableAdapter<PhotoRecyclerAdapter.PhotoViewHolder, PhotoModel>(callbacks, photos) {


    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        val countryView: TextView = itemView.findViewById(R.id.country_text)
        val titleView: TextView = itemView.findViewById(R.id.title_text)
        val descriptionView: TextView = itemView.findViewById(R.id.description_text)
        val countryFlag: ImageView = itemView.findViewById(R.id.country_flag)
        val imageView: ImageView = itemView.findViewById(R.id.photo_view)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }


        override fun onClick(view: View) {
            if (!isSelectionMode) {
                val fullScreenIntent = Intent(context, ImageActivity::class.java)
                fullScreenIntent.data = Uri.parse(photos[adapterPosition].path)
                context.startActivity(fullScreenIntent)
            } else {
                val cardView: CardView = view.findViewById(R.id.photo_card_view)
                handleViewClick(cardView, adapterPosition)
            }
        }

        override fun onLongClick(view: View): Boolean {

            if (!isSelectionMode) callbacks.onSelectStart()

            isSelectionMode = true

            val cardView: CardView = view.findViewById(R.id.photo_card_view)


            return handleViewClick(cardView, adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_photo_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

        val curPhoto = photos[position]

        holder.countryView.text = curPhoto.country
        holder.titleView.text = curPhoto.title
        holder.descriptionView.text = curPhoto.description

        loadFlag(curPhoto.country, holder.countryFlag)

        holder.imageView.setImageURI(Uri.parse(curPhoto.path))

        val cardView: CardView = holder.itemView.findViewById(R.id.photo_card_view)
        if (selectedItemsPosition.contains(position)) {
            cardView.setCardBackgroundColor(selectedColor)
        } else {
            cardView.setCardBackgroundColor(Color.WHITE)
        }
    }

    /**
     * update the list based on a filtered list
     */
    fun setFilteredListPhoto(filteredList: ArrayList<PhotoModel>) {
        photos = filteredList
        setFilteredList(filteredList)
    }


    fun shareSelected() {
        if (selectedItemsPosition.size == 1) shareOneImage()
        else shareMultipleImage()
    }

    private fun shareOneImage() {

        val share = Intent(Intent.ACTION_SEND)

        val curPhoto = photos[selectedItemsPosition[0]]

        val requestFile = File(Uri.parse(curPhoto.path)!!.path!!)

        // Use the FileProvider to get a content URI
        val fileUri: Uri = FileProvider.getUriForFile(
            context, context.applicationContext.packageName + ".provider", requestFile
        )

        share.putExtra(Intent.EXTRA_TEXT, "${curPhoto.title} \n ${curPhoto.description}")
        share.putExtra(Intent.EXTRA_STREAM, fileUri)
        share.type = "image/*"
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(share, "Share photo File"))
    }

    private fun shareMultipleImage() {
        val fileUris: ArrayList<Uri> = ArrayList()
        var descriptionString = ""
        var counter = 0
        selectedItemsPosition.forEach {
            val selectedPhoto = photos[it]
            counter++

            val requestFile = File(Uri.parse(selectedPhoto.path)!!.path!!)

            // Use the FileProvider to get a content URI
            val fileUri: Uri = FileProvider.getUriForFile(
                context, context.applicationContext.packageName + ".provider", requestFile
            )

            descriptionString += "photo $counter : ${selectedPhoto.title} \n ${selectedPhoto.description} \n"

            fileUris.add(fileUri)
        }

        val share = Intent(Intent.ACTION_SEND_MULTIPLE)
        share.putExtra(Intent.EXTRA_TEXT, descriptionString)
        share.type = "image/*"
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(share, "Share photos File"))
    }


}