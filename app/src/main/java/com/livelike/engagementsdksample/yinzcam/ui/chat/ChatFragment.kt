package com.livelike.engagementsdksample.yinzcam.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.yinzcam.YinzCamActivity
import kotlinx.android.synthetic.main.fragment_chat.*

/**
 * A placeholder fragment containing a simple view.
 */
class ChatFragment : Fragment() {

    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? YinzCamActivity)?.let {
            chat_view.setSession(it.session.chatSession)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
}