package com.example.worldmemo.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
            val formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD HH:MM:SS")
            return  LocalDateTime.now().format(formatter)
        }
    }
}
