package com.livelike.engagementsdksample.chat

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.chat.viewmodel.ChatViewModel
import com.livelike.engagementsdksample.chat.viewmodel.ChatViewModelFactory
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    var mainViewModel: ChatViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // This will create an instance of Engagement viewmodel which can be used for creating session and initialization
        mainViewModel = ViewModelProvider(
            this,
            ChatViewModelFactory(this.applicationContext as Application)
        ).get(ChatViewModel::class.java)
        // Check whether chat or widget is selected
        mainViewModel?.setChatNickName("Kanav")
        mainViewModel!!.getSession()?.let {
            chat_view.setSession(it.chatSession)
            chat_view
        }
        mainViewModel?.getSession()?.analyticService?.setEventObserver { eventKey, eventJson ->
            Log.d("Sample_Events",eventKey)
        }
    }

    override fun onPause() {
        super.onPause()
        mainViewModel?.pauseSession()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel?.resumeSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel?.closeSession()
    }
}
