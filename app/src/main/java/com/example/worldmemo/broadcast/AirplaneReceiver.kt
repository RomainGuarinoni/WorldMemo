package com.example.worldmemo.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import android.view.MenuItem

class AirplaneReceiver : BroadcastReceiver() {

    val intentFilter = IntentFilter("android.intent.action.AIRPLANE_MODE")

    private val menuItem = ArrayList<MenuItem>()

    override fun onReceive(context: Context?, p1: Intent?) {
        menuItem.forEach {
            it.isVisible = isAirplaneModeOn(context)
        }
    }

    fun addMenuItem(item: MenuItem) {
        this.menuItem.add(item)
    }

    fun isAirplaneModeOn(context: Context?): Boolean {
        return Settings.System.getInt(
            context?.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0
        ) != 0
    }
}