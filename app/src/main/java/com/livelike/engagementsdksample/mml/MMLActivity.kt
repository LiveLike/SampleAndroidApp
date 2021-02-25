package com.livelike.engagementsdksample.mml

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.livelike.engagementsdksample.R
import com.mml.mmlengagementsdk.LiveLikeSDKIntegrationManager

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
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = mmlPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }
}