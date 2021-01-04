package com.livelike.engagementsdksample

import android.app.Application
import android.content.res.Configuration
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdk.core.utils.LogLevel
import com.livelike.engagementsdk.core.utils.minimumLogLevel

class LiveLikeApplication : Application() {
    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!

    private var contentSession: LiveLikeContentSession? = null
    lateinit var engagementSDK: EngagementSDK
    var programId: String? = null
    var clientId: String? = null

    override fun onCreate() {
        super.onCreate()
        // Required initialization logic here!
        // setUpEngagementSDK()
        minimumLogLevel= LogLevel.Verbose
    }

    fun setProgramCode(id: String) {
        programId = id
        setContentSession()
    }

    fun setId(id: String) {
        clientId = id
        setUpEngagementSDK()
    }

    fun setUpEngagementSDK() {
        engagementSDK =
            clientId?.let { EngagementSDK(it, this) }!!
    }

    fun setContentSession() {
        engagementSDK =
            EngagementSDK(clientId!!, this)

        contentSession =
            programId?.let { engagementSDK.createContentSession(it) }
        println("LiveLikeApplication.setContentSession>>>$contentSession")
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
