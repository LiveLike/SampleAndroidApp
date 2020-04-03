package com.example.sample.chat.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatViewModelFactory(
    private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        require(modelClass == ChatViewModel::class.java) { "Unknown ViewModel class" }
        return ChatViewModel(
            application
        ) as T
    }
}