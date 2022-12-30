package com.example.worldmemo.model

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class AudioModel(
    val id: String = getUuid(),
    val sentence: String,
    val translation: String,
    val country: String,
    val path: String,
    val createdDate:String = getCreatedDate(),
) {
    companion object {
        fun getUuid(): String {
            return UUID.randomUUID().toString()
        }

        fun getCreatedDate(): String {
            val tz = TimeZone.getTimeZone("UTC")
            val df: DateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss") // Quoted "Z" to indicate UTC, no timezone offset

            df.timeZone = tz
           return df.format(Date())
        }
    }
}
