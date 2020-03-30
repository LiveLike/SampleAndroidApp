package com.example.sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.utils.LogLevel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // when user clicks on Chat option
        chatLayout.setOnClickListener {
            val intent = Intent(this, EngagementActivity::class.java)
            intent.putExtra("functionalitySelected", EngagementActivity.CHATCLICKED)
            startActivity(intent)
        }
        // when user clicks on Widget option
        widgetLayout.setOnClickListener {
            val intent = Intent(this, EngagementActivity::class.java)
            intent.putExtra("functionalitySelected", EngagementActivity.WIDGETCLICKED)
            startActivity(intent)
        }







    }


}
