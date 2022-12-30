package com.example.worldmemo.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.worldmemo.R
import com.example.worldmemo.model.AudioModel
import java.io.File
import java.io.IOException

class AudioRecyclerAdapter(
    private var audios: MutableList<AudioModel>, val callbacks: Callbacks, val context: Context
) : RecyclerView.Adapter<AudioRecyclerAdapter.AudioViewHolder>() {

    private var isSelectionMode = false
    private var selectedColor = Color.rgb(91, 149, 244)
    private val baseUrl = "https://countryflagsapi.com/png/"

    private var player: MediaPlayer? = null
    private var curAudioPlayed: AudioModel? = null
    private var curAudioPlayedView: AudioViewHolder? = null


    // We keep track to the position of the view because
    // the recycle view always re used view so we cannot
    // just apply change on the view during the click,
    // otherwise it will affect other views later during
    // the binding part. See https://stackoverflow.com/questions/55285596/changing-one-viewholder-item-also-affects-to-other-items
    // for better understanding
    private var selectedItemsPosition = ArrayList<Int>()

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        val countryView: TextView = itemView.findViewById(R.id.country_text)
        val sentenceView: TextView = itemView.findViewById(R.id.sentence_text)
        val translationView: TextView = itemView.findViewById(R.id.translation_text)
        val countryFlag: ImageView = itemView.findViewById(R.id.country_flag)
        val playButton: Button = itemView.findViewById(R.id.audio_play_button)
        val stopButton: Button = itemView.findViewById(R.id.audio_pause_button)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }


        override fun onClick(view: View) {
            if (!isSelectionMode) return

            handleAudioClick(view, adapterPosition)

        }

        override fun onLongClick(view: View): Boolean {

            if (!isSelectionMode) callbacks.onSelectStart()

            isSelectionMode = true

            return handleAudioClick(view, adapterPosition)
        }

        private fun handleAudioClick(view: View, position: Int): Boolean {
            val cardView: CardView = view.findViewById(R.id.audio_card_view)

            if (selectedItemsPosition.contains(position)) {
                selectedItemsPosition.remove(position)
                cardView.setCardBackgroundColor(Color.WHITE)

            } else {
                selectedItemsPosition.add(position)
                cardView.setCardBackgroundColor(selectedColor)

            }

            Log.println(
                Log.DEBUG, "size of the selected array", selectedItemsPosition.size.toString()
            )

            if (selectedItemsPosition.size == 0) {
                isSelectionMode = false
                callbacks.onSelectEnd()
                return false
            }

            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {



        return AudioViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {

        val curAudio = audios[position]

        holder.countryView.text = curAudio.country
        holder.sentenceView.text = curAudio.sentence
        holder.translationView.text = curAudio.translation

        holder.countryFlag.load(baseUrl + curAudio.country) {
            placeholder(R.drawable.ic_image)
        }

        holder.playButton.setOnClickListener { playAudio(curAudio, holder) }
        holder.stopButton.setOnClickListener { pauseAudio() }

        val cardView: CardView = holder.itemView.findViewById(R.id.audio_card_view)
        if (selectedItemsPosition.contains(position)) {
            cardView.setCardBackgroundColor(selectedColor)
        } else {
            cardView.setCardBackgroundColor(Color.WHITE)

        }
    }

    override fun getItemCount(): Int {
        return audios.size
    }

    fun setFilteredList(filteredList: ArrayList<AudioModel>) {
        this.audios = filteredList
        notifyDataSetChanged()
    }

    fun deleteSelected() {
        selectedItemsPosition.sortDescending()
        selectedItemsPosition.forEach {
            val audio = audios[it]
            callbacks.onDeleteAudio(audio)
            audios.removeAt(it)
            notifyItemRemoved(it)
        }
        selectedItemsPosition = ArrayList()
        callbacks.onSelectEnd()
        isSelectionMode = false
    }

    fun shareSelected() {

        val fileUris: ArrayList<Uri> = ArrayList()

        selectedItemsPosition.forEach {
            val selectedAudio = audios[it]

            val requestFile = File(selectedAudio.path)

            // Use the FileProvider to get a content URI
            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                requestFile
            )

            fileUris.add(fileUri)
        }


        val share = Intent(Intent.ACTION_SEND_MULTIPLE)
        share.type = "audio/aac"
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris)
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(share, "Share Sound File"))

    }

    private fun playAudio(audio: AudioModel, holder: AudioViewHolder) {

        // If the player is not null and the audio is the same that the curAudioPlayed, then
        // We just want to resume the audio
        if (player != null && curAudioPlayed != null && audio.id == curAudioPlayed!!.id) {
            Log.d("resume audio", audio.sentence)
            player!!.start()
            holder.playButton.visibility = Button.INVISIBLE
            holder.stopButton.visibility = Button.VISIBLE
            return
        }

        player?.release()

        player = null

        if (curAudioPlayed != null) {
            curAudioPlayedView?.playButton?.visibility = Button.VISIBLE
            curAudioPlayedView?.stopButton?.visibility = Button.INVISIBLE
        }

        curAudioPlayed = audio
        curAudioPlayedView = holder

        holder.playButton.visibility = Button.INVISIBLE
        holder.stopButton.visibility = Button.VISIBLE

        player = MediaPlayer().apply {
            try {
                setDataSource(audio.path)
                setOnCompletionListener {
                    holder.playButton.visibility = Button.VISIBLE
                    holder.stopButton.visibility = Button.INVISIBLE
                    curAudioPlayed = null
                }
                prepare()
                start()
                player?.release()
                player = null
            } catch (e: IOException) {
                Log.println(Log.ERROR, "audio player", "failed to prepare the audio")
                Log.println(Log.ERROR, "audio player", e.stackTraceToString())

            }
        }
    }

    private fun pauseAudio() {

        if (player != null && player!!.isPlaying) {
            player!!.pause()
            curAudioPlayedView?.playButton?.visibility = Button.VISIBLE
            curAudioPlayedView?.stopButton?.visibility = Button.INVISIBLE
        }
    }

    interface Callbacks {
        fun onSelectStart()
        fun onSelectEnd()
        fun onDeleteAudio(audio: AudioModel)
    }

}