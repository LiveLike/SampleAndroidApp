package com.example.sample.widget.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class EngagementViewModelFactory(
    private val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        require(modelClass == widgetViewModel::class.java) { "Unknown ViewModel class" }
        return widgetViewModel(
            application
        ) as T
    }
}