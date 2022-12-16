package com.example.worldmemo.ui.home

import android.graphics.Color
import android.util.Log
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

    // We keep track to the position of the view because
    // the recycle view always re used view so we cannot
    // just apply change on the view during the click,
    // otherwise it will affect other views later during
    // the binding part. See https://stackoverflow.com/questions/55285596/changing-one-viewholder-item-also-affects-to-other-items
    // for better understanding
    private val selectedItemsPosition = ArrayList<Int>()

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener, View.OnLongClickListener {
        val countryView: TextView = itemView.findViewById(R.id.country_text)
        val sentenceView: TextView = itemView.findViewById(R.id.sentence_text)
        val translationView: TextView = itemView.findViewById(R.id.translation_text)

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View) {
            if (!isSelectionMode) return

            handleAudioClick(view, adapterPosition)

        }

        override fun onLongClick(view: View): Boolean {
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
                cardView.setCardBackgroundColor(Color.RED)

            }

            Log.println(
                Log.DEBUG, "size of the selected array", selectedItemsPosition.size.toString()
            )

            if (selectedItemsPosition.size == 0) {
                isSelectionMode = false
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

        val cardView: CardView = holder.itemView.findViewById(R.id.audio_card_view)
        if (selectedItemsPosition.contains(position)) {
            cardView.setCardBackgroundColor(Color.RED)
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


}