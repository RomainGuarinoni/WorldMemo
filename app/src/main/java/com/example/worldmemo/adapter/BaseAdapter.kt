package com.example.worldmemo.adapter

import android.graphics.Color
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.worldmemo.R

/**
 * This class is the base class for the adapter that handle photos and audios
 * it provide the functionalities to handle selected items, delete them
 */
abstract class BaseAdapter<T : RecyclerView.ViewHolder, U>(
    private val callbacks: Callbacks<U>, private var list: MutableList<U>
) : RecyclerView.Adapter<T>(
) {

    var isSelectionMode = false
    var selectedColor = Color.rgb(91, 149, 244)

    /**
     * We keep track to the position of the view because
     * the recycle view always re used view so we cannot
     * just apply change on the view during the click,
     * otherwise it will affect other views later during
     * the binding part. See https://stackoverflow.com/questions/55285596/changing-one-viewholder-item-also-affects-to-other-items
     * for better understanding
     */
    var selectedItemsPosition = ArrayList<Int>()

    /**
     * The Base URL for the flag api
     */
    val baseUrl = "https://countryflagsapi.com/png/"

    /**
     * Add or remove an item from the selected list depending
     * on its status (already selected or not)
     */
    fun handleViewClick(cardView: CardView, position: Int): Boolean {
        if (selectedItemsPosition.contains(position)) {
            selectedItemsPosition.remove(position)
            cardView.setCardBackgroundColor(Color.WHITE)

        } else {
            selectedItemsPosition.add(position)
            cardView.setCardBackgroundColor(selectedColor)

        }

        if (selectedItemsPosition.size == 0) {
            isSelectionMode = false
            callbacks.onSelectEnd()
            return false
        }

        return true
    }

    /**
     * Load the country's flag into an image view
     */
    fun loadFlag(country: String, imageView: ImageView) {
        imageView.load(baseUrl + country) {
            placeholder(R.drawable.ic_image)
        }

    }

    /**
     * Set the background color of the cardView depending if
     * it is selected or not.
     *
     */
    fun setCardBackgroundColor(position: Int, cardView: CardView) {
        if (selectedItemsPosition.contains(position)) {
            cardView.setCardBackgroundColor(selectedColor)
        } else {
            cardView.setCardBackgroundColor(Color.WHITE)
        }
    }

    /**
     * update the list based on a filtered list
     */
    fun setFilteredList(filteredList: ArrayList<U>) {
        list = filteredList
        notifyDataSetChanged()
    }

    /**
     * Delete the selected items
     */
    fun deleteSelected() {
        selectedItemsPosition.sortDescending()
        selectedItemsPosition.forEach {
            val item = list[it]
            callbacks.onDelete(item)
            list.removeAt(it)
            notifyItemRemoved(it)
        }
        selectedItemsPosition = ArrayList()
        callbacks.onSelectEnd()
        isSelectionMode = false
    }

    fun hasItemSelected(): Boolean {
        return selectedItemsPosition.size != 0
    }

    interface Callbacks<U> {
        fun onSelectStart()
        fun onSelectEnd()
        fun onDelete(item: U)
    }


}