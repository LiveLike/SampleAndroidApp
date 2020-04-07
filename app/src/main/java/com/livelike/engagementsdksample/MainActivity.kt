package com.livelike.engagementsdksample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.livelike.engagementsdksample.chat.ChatActivity
import com.livelike.engagementsdksample.widget.WidgetActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
