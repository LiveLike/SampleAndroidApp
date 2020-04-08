package com.livelike.engagementsdksample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.livelike.engagementsdksample.chat.ChatActivity
import com.livelike.engagementsdksample.widget.WidgetActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        llTextView.text = "LL SDK [ ${BuildConfig.VERSION_CODE} ]"

        // when user clicks on Chat option
        chatLayout.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }
        // when user clicks on Widget option
        widgetLayout.setOnClickListener {
            val intent = Intent(this, WidgetActivity::class.java)
            startActivity(intent)
        }
    }
}
