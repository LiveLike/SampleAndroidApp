package com.livelike.engagementsdksample.yinzcam

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.yinzcam.ui.main.SectionsPagerAdapter

class YinzCamActivity : AppCompatActivity() {

    lateinit var session: LiveLikeContentSession
    lateinit var sdk: EngagementSDK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yinz_cam)
        sdk = EngagementSDK("8PqSNDgIVHnXuJuGte1HdvOjOqhCFE1ZCR3qhqaS", applicationContext)
        session = sdk.createContentSession("09d93835-ee52-4757-976c-ea09d6a5798c")
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

}