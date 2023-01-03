package com.example.worldmemo.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.MenuItem
import com.example.worldmemo.utils.NetworkUtils

class NetworkReceiver : BroadcastReceiver() {

    val intentFilter = arrayOf<IntentFilter>(
        IntentFilter("android.intent.action.AIRPLANE_MODE"),
        IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"),
        IntentFilter("android.net.wifi.WIFI_STATE_CHANGED")
    )


    private val menuItem = ArrayList<MenuItem>()

    override fun onReceive(context: Context?, p1: Intent?) {

        val isConnected:Boolean = if(NetworkUtils.isAirplaneModeOn(context)){
             false
        } else NetworkUtils.isInternetAvailable(context)

        Log.d("airplane mod", NetworkUtils.isAirplaneModeOn(context).toString())
        Log.d("internet available", NetworkUtils.isInternetAvailable(context).toString())



        menuItem.forEach {
            // We display the icon only if we are not connected to warn the user
            it.isVisible = !isConnected
        }
    }

    fun addMenuItem(item: MenuItem) {
        this.menuItem.add(item)
    }


}