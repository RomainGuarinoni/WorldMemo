package com.example.worldmemo.model

import java.util.UUID

data class AudioModel(
    val id:String = getUuid(),
    val sentence:String,
    val translation:String,
    val country:String
){
    companion object{
        fun getUuid():String{
            return UUID.randomUUID().toString()
        }
    }
}
