package com.example.worldmemo.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.worldmemo.AudioModel
import com.example.worldmemo.R

class HomeRecyclerAdapter(
    private var audios: MutableList<AudioModel>,
) : RecyclerView.Adapter<HomeRecyclerAdapter.AudioViewHolder>() {

    private var isSelectionMode = false
    private val selectedItems = ArrayList<AudioModel>()

    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val countryView: TextView = itemView.findViewById(R.id.country_text)
        val sentenceView: TextView = itemView.findViewById(R.id.sentence_text)
        val translationView: TextView = itemView.findViewById(R.id.translation_text)


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



        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                val cardView: CardView = it.findViewById(R.id.audio_card_view)
                val currentAudio = audios.get(position)
                if (selectedItems.contains(currentAudio)) {
                    cardView.setCardBackgroundColor(Color.WHITE)
                    selectedItems.remove(currentAudio)
                } else {
                    cardView.setCardBackgroundColor(Color.RED)
                    selectedItems.add(currentAudio)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return audios.size
    }

    fun setFilteredList(filteredList: ArrayList<AudioModel>) {
        this.audios = filteredList
        notifyDataSetChanged()
    }

}