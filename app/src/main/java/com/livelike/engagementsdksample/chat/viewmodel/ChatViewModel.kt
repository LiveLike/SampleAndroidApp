package com.livelike.engagementsdksample.chat.viewmodel

import androidx.lifecycle.AndroidViewModel
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdksample.LiveLikeApplication

class ChatViewModel constructor(
    application: LiveLikeApplication
) : AndroidViewModel(application) {

    val engagementSDK = application.getEngagementSdk()

    val contentSession = application.getContentSession()


    fun getSession(): LiveLikeContentSession? {
        return contentSession
    }


    fun pauseSession() {
        contentSession?.pause()
    }

    fun resumeSession() {
        contentSession?.resume()
    }

    fun closeSession() {
        contentSession?.close()
    }

    fun setChatNickName(name: String) {
        engagementSDK.updateChatNickname(name)
    }
}
