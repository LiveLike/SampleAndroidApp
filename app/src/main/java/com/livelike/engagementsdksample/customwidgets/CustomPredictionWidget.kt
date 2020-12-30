package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.livelike.engagementsdk.widget.widgetModel.PredictionWidgetViewModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.parseDuration
import com.livelike.engagementsdksample.widget.model.LiveLikeWidgetOption
import kotlinx.android.synthetic.main.custom_prediction_widget.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

const val DEFAULT_INTERACTION_TIME = 5000L

class CustomPredictionWidget(context : Context, val predictionWidgetViewModel: PredictionWidgetViewModel): FrameLayout(context)  {

    private lateinit var imageOptionsWidgetAdapter: ImageOptionsWidgetAdapter

    init {
        inflate(context, R.layout.custom_prediction_widget, this)
        predictionWidgetViewModel?.widgetData?.let { liveLikeWidget ->
            val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: DEFAULT_INTERACTION_TIME
            time_bar.startTimer(timeMillis)
            widget_title.text = liveLikeWidget.question

            (context as AppCompatActivity).lifecycleScope.async {
                delay(timeMillis)
                predictionWidgetViewModel?.finish()
            }

            liveLikeWidget.options?.let {
                imageOptionsWidgetAdapter =
                    ImageOptionsWidgetAdapter(context, ArrayList(it.map { item -> LiveLikeWidgetOption(item?.id!!,item?.description?:"",false,item.imageUrl,item.answerCount) })
                    ) { option ->
                        predictionWidgetViewModel?.lockInVote(option.id ?: "")
                    }
                widget_rv.layoutManager = GridLayoutManager(context,2)
                widget_rv.adapter = imageOptionsWidgetAdapter
            }
        }
    }


}