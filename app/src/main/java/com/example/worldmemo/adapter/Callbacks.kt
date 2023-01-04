package com.example.worldmemo.adapter

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import com.example.worldmemo.R

class Callbacks<T>(
    private val context: Context,
    private val buttonLayout: View,
    private val updateButton:Button,
    private val delete: (item: T) -> Unit
) : SelectableAdapter.Callbacks<T> {
    override fun onSelectStart() {
        buttonLayout.visibility = View.VISIBLE

        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_up)

        buttonLayout.startAnimation(animation)
    }

    override fun onSelectEnd() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_down)

        buttonLayout.startAnimation(animation)

        buttonLayout.visibility = View.GONE
    }

    override fun onDelete(item: T) {
        delete(item)
    }

    override fun onOneItemSelected() {
        updateButton.visibility=Button.VISIBLE
    }

    override fun onMultipleItemSelected() {
        updateButton.visibility=Button.GONE

    }
}