package com.livelike.engagementsdksample.timeline

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.livelike.engagementsdk.widget.timeline.WidgetTimeLineViewModel
import com.livelike.engagementsdk.widget.timeline.WidgetsTimeLineView
import com.livelike.engagementsdksample.LiveLikeApplication
import com.livelike.engagementsdksample.R
import kotlinx.android.synthetic.main.activity_time_line.*

class TimeLineActivity : AppCompatActivity() {

    var timeLineViewModel: WidgetTimeLineViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_line)

        val engagementSDK = (application as LiveLikeApplication).getEngagementSdk()

        val contentSession = (application as LiveLikeApplication).getContentSession()

        contentSession?.let {
            timeLineViewModel = WidgetTimeLineViewModel(contentSession)
            val timeLineView = WidgetsTimeLineView(
                this,
                timeLineViewModel!!,
                engagementSDK
            )
            timeLineView.widgetViewFactory =
                TimeLineWidgetFactory(
                    this,
                    timeLineViewModel!!.timeLineWidgets
                )
            container.addView(timeLineView)
        }

    }
}