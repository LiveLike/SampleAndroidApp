package com.livelike.engagementsdksample.mml

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.livelike.engagementsdksample.R
import com.mml.mmlengagementsdk.LiveLikeSDKIntegrationManager
import kotlinx.android.synthetic.main.activity_m_m_l.*

class MMLActivity : AppCompatActivity() {
    lateinit var liveLikeSDKIntegrationManager: LiveLikeSDKIntegrationManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m_m_l)
        liveLikeSDKIntegrationManager = LiveLikeSDKIntegrationManager(
            applicationContext,
            "3WtkbrjmyPFUHTSckcVVUlikAAdHEy1P0zqqczF0",
            "000301a4-34ca-4e8c-9e4d-da05499c0bf2",
            "61ce5e0b-c7a9-4333-a5be-9a533e582747"
        )
        val mmlPagerAdapter = MMLPagerAdapter(this, liveLikeSDKIntegrationManager)
//        view_pager.offscreenPageLimit = 4
        view_pager.adapter = mmlPagerAdapter
        TabLayoutMediator(tabs, view_pager) { tabs, position ->
            tabs.text = when (position) {
                0 -> "Chat 1"
                1 -> "Widget1"
                2 -> "Chat"
                3 -> "Widget"
                else -> ""
            }
            view_pager.setCurrentItem(tabs.position, true)
        }.attach()

    }
}