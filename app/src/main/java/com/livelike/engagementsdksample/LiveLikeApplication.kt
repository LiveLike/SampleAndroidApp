package com.livelike.engagementsdksample

import android.app.Application
import android.content.res.Configuration
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeContentSession

class LiveLikeApplication : Application() {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!

    private var contentSession: LiveLikeContentSession? = null
    lateinit var engagementSDK: EngagementSDK

    override fun onCreate() {
        super.onCreate()
        engagementSDK =
            EngagementSDK(BuildConfig.CLIENT_ID, this)

        contentSession =
            engagementSDK.createContentSession(com.livelike.engagementsdksample.BuildConfig.PROGRAM_ID)
        // Required initialization logic here!
    }

    fun getContentSession(): LiveLikeContentSession? {
        return contentSession
    }

    fun getEngagementSdk(): EngagementSDK {
        return engagementSDK
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    override fun onLowMemory() {
        super.onLowMemory()
    }
}
