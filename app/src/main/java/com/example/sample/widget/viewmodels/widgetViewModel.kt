package com.example.sample.widget.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeContentSession


public class widgetViewModel constructor(
    application: Application
) : AndroidViewModel(application) {

    val engagementSDK =
        EngagementSDK(com.example.sample.BuildConfig.CLIENT_ID, application.applicationContext)

    val contentSession =
        engagementSDK.createContentSession(com.example.sample.BuildConfig.PROGRAM_ID)

    fun getSession(): LiveLikeContentSession {
        return contentSession
    }

    fun pauseSession() {
        contentSession.pause()
    }

    fun resumeSession() {
        contentSession.resume()
    }

    fun closeSession() {
        contentSession.close()
    }



}