package com.livelike.engagementsdksample.widget

import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.livelike.engagementsdk.core.services.messaging.proxies.LiveLikeWidgetEntity
import com.livelike.engagementsdk.core.services.messaging.proxies.WidgetInterceptor
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.widget.viewmodels.EngagementViewModelFactory
import com.livelike.engagementsdksample.widget.viewmodels.widgetViewModel
import kotlinx.android.synthetic.main.activity_widget.*

class WidgetActivity : AppCompatActivity() {

    var mainViewModel: widgetViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget)

        // to get intent received from MainActivity

        // This will create an instance of Engagement viewmodel which can be used to creating session and initialization
        mainViewModel = ViewModelProvider(
            this,
            EngagementViewModelFactory(this.applicationContext as Application)
        ).get(widgetViewModel::class.java)
        // Check whether chat or widget is selected

        mainViewModel!!.getSession()?.let { widget_view.setSession(it) }

        // Example of Widget Interceptor showing a dialog
        val interceptor = object : WidgetInterceptor() {
            override fun widgetWantsToShow(widgetData: LiveLikeWidgetEntity) {
                AlertDialog.Builder(this@WidgetActivity).apply {
                    setMessage("You received a Widget, what do you want to do?")
                    setPositiveButton("Show") { _, _ ->
                        showWidget() // Releases the widget
                    }
                    setNegativeButton("Dismiss") { _, _ ->
                        dismissWidget() // Discards the widget
                    }
                    create()
                }.show()
            }
        }

        // You just need to add it on your session instance
        mainViewModel?.getSession()?.widgetInterceptor = interceptor

        mainViewModel?.getSession()?.analyticService?.setEventObserver { eventKey, eventJson ->
           Log.d("Sample_Events",eventKey)
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
