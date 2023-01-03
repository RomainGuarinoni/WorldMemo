package com.example.worldmemo.adapter

import android.content.Context
import android.content.Intent
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
import com.example.worldmemo.R
import com.example.worldmemo.model.AudioModel
import java.io.File
import java.io.IOException

class AudioRecyclerAdapter(
    private var audios: MutableList<AudioModel>,
    val callbacks: Callbacks<AudioModel>,
    val context: Context
) : SelectableAdapter<AudioRecyclerAdapter.AudioViewHolder, AudioModel>(
    callbacks, audios
) {
    private var player: MediaPlayer? = null
    private var curAudioPlayed: AudioModel? = null
    private var curAudioPlayedView: AudioViewHolder? = null

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

            val cardView: CardView = view.findViewById(R.id.audio_card_view)

            handleViewClick(cardView, adapterPosition)

        }

        override fun onLongClick(view: View): Boolean {

            if (!isSelectionMode) callbacks.onSelectStart()

            isSelectionMode = true

            val cardView: CardView = view.findViewById(R.id.audio_card_view)


            return handleViewClick(cardView, adapterPosition)
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

        loadFlag(curAudio.countryCode, holder.countryFlag)

        holder.playButton.setOnClickListener { playAudio(curAudio, holder) }
        holder.stopButton.setOnClickListener { pauseAudio() }

        val cardView: CardView = holder.itemView.findViewById(R.id.audio_card_view)
        setCardBackgroundColor(position, cardView)
    }

    /**
     * update the list based on a filtered list
     */
    fun setFilteredListAudio(filteredList: ArrayList<AudioModel>) {
        audios = filteredList
        setFilteredList(filteredList)
    }


    fun shareSelected() {
        val fileUris: ArrayList<Uri> = ArrayList()
        selectedItemsPosition.forEach {
            val selectedAudio = audios[it]

            val requestFile = File(selectedAudio.path)

            // Use the FileProvider to get a content URI
            val fileUri: Uri = FileProvider.getUriForFile(
                context, context.applicationContext.packageName + ".provider", requestFile
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

}
