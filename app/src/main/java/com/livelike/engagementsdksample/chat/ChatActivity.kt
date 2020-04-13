package com.livelike.engagementsdksample.chat

import android.app.Application
import android.os.Bundle
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

//            // build alert dialog
//            val dialogBuilder = AlertDialog.Builder(this)
//
//            // set message of alert dialog
//            dialogBuilder.setMessage("Do you want to open private chat ?")
//                // if the dialog is cancelable
//                .setCancelable(false)
//                // positive button text and action
//                .setPositiveButton("Proceed", DialogInterface.OnClickListener { dialog, id ->
//
//                    mainViewModel?.getSession()
//                        ?.enterChatRoom(com.livelike.engagementsdksample.BuildConfig.CHATROOM_KEY)
//
//                    mainViewModel?.getSession()
//                        ?.joinChatRoom(com.livelike.engagementsdksample.BuildConfig.CHATROOM_KEY)
//                    chat_view.setSession(mainViewModel?.getSession()!!)
//                })
//                // negative button text and action
//                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
//                    dialog.cancel()
//                    mainViewModel!!.getSession()?.let { chat_view.setSession(it) }
//                })
//
//            // create dialog box
//            val alert = dialogBuilder.create()
//            // set title for alert dialog box
//            alert.setTitle("Select an option")
//            // show alert dialog
//            alert.show()
        mainViewModel!!.getSession()?.let {
            chat_view.setSession(it)
            chat_view
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
