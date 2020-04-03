package com.example.sample.chat.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.sample.BuildConfig
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeContentSession

 class ChatViewModel constructor(
    application: Application
) : AndroidViewModel(application) {

    val engagementSDK =
        EngagementSDK(BuildConfig.CLIENT_ID, application.applicationContext)

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

    fun setChatNickName(name: String) {
        engagementSDK.updateChatNickname(name)
    }


}