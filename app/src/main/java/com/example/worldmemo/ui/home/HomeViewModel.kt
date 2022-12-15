package com.example.worldmemo.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _text:MutableLiveData<String> = MutableLiveData("This is the home page")

    private val textArray:Array<String> = arrayOf("bonjour a tous", "cette app est incroyable", "je comprends rien a android")
    private var currentIndex = 0

    private fun incrementCurrentIndex(){
        if(currentIndex == textArray.size -1){
             currentIndex = 0
         }else{
             currentIndex++
         }
    }
    fun changeText(){
        incrementCurrentIndex()
        _text.value = textArray[currentIndex]
    }

    fun changeText(newText:String){
        _text.value = newText
    }
    val text: LiveData<String> = _text
}