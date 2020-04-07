package com.livelike.engagementsdksample.widget

import com.livelike.engagementsdksample.R

import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.livelike.engagementsdksample.widget.viewmodels.widgetViewModel
import com.livelike.engagementsdksample.widget.viewmodels.EngagementViewModelFactory
import com.livelike.engagementsdk.services.messaging.proxies.WidgetInterceptor
import kotlinx.android.synthetic.main.activity_widget.*


class WidgetActivity : AppCompatActivity() {

    var mainViewModel: widgetViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget)

        // to get intent received from MainActivity

        //This will create an instance of Engagement viewmodel which can be used to creating session and initialization
        mainViewModel = ViewModelProvider(
            this,
            EngagementViewModelFactory(this.applicationContext as Application)
        ).get(widgetViewModel::class.java)
        // Check whether chat or widget is selected

        mainViewModel!!.getSession()?.let { widget_view.setSession(it) }

            // Example of Widget Interceptor showing a dialog
            val interceptor = object : WidgetInterceptor() {
                override fun widgetWantsToShow() {
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
