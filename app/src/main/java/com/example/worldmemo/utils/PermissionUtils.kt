package com.example.worldmemo.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager

class PermissionUtils(private val context: Context) {

    val PERMISSION_AUDIO = Manifest.permission.RECORD_AUDIO
    val PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    val PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE

    val permissions = arrayOf(
        PERMISSION_AUDIO,
        PERMISSION_WRITE_STORAGE,
        PERMISSION_READ_STORAGE,
    )


    fun isPermissionAllowed(permission: String): Boolean {
        if (permission !in permissions) return false

        return context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    }



}