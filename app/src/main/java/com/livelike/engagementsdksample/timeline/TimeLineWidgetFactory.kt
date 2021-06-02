package com.livelike.engagementsdksample.timeline

import android.content.Context
import android.view.View
import com.livelike.engagementsdk.widget.LiveLikeWidgetViewFactory
import com.livelike.engagementsdk.widget.timeline.TimelineWidgetResource
import com.livelike.engagementsdk.widget.viewModel.LiveLikeWidgetMediator
import com.livelike.engagementsdk.widget.viewModel.WidgetStates
import com.livelike.engagementsdk.widget.widgetModel.*
import com.livelike.engagementsdksample.customwidgets.*
import com.livelike.engagementsdksample.customwidgets.poll.CustomPollWidget

class TimeLineWidgetFactory(
    val context: Context,
    private val widgetList: List<TimelineWidgetResource>?
) : LiveLikeWidgetViewFactory {

    override fun createCheerMeterView(cheerMeterWidgetModel: CheerMeterWidgetmodel): View? {
        return CustomCheerMeter(context).apply {
            this.cheerMeterWidgetmodel = cheerMeterWidgetmodel
        }
    }

    override fun createAlertWidgetView(alertWidgetModel: AlertWidgetModel): View? {
        return CustomAlert(context).apply {
            this.alertModel = alertWidgetModel
        }
    }

    override fun createQuizWidgetView(
        quizWidgetModel: QuizWidgetModel,
        isImage: Boolean
    ): View? {
        return CustomQuizView(context).apply {
            this.quizWidgetModel = quizWidgetModel
        }
    }

    override fun createPredictionWidgetView(
        predictionViewModel: PredictionWidgetViewModel,
        isImage: Boolean
    ): View? {
        return CustomPredictionWidget(context).apply {
            predictionWidgetViewModel = predictionViewModel
        }
    }

    override fun createPredictionFollowupWidgetView(
        followUpWidgetViewModel: FollowUpWidgetViewModel,
        isImage: Boolean
    ): View? {
        return CustomPredictionFollowUpWidget(context).apply {
            this.followUpWidgetViewModel = followUpWidgetViewModel
        }
    }

    override fun createPollWidgetView(
        pollWidgetModel: PollWidgetModel,
        isImage: Boolean
    ): View? {
        return CustomPollWidget(context).apply {
            this.pollWidgetModel = pollWidgetModel
        }
    }

    override fun createImageSliderWidgetView(imageSliderWidgetModel: ImageSliderWidgetModel): View? {
        return CustomEmojiSlider(context).apply {
            this.imageSliderWidgetModel = imageSliderWidgetModel
        }
    }

    private fun isWidgetActive(liveLikeWidgetMediator: LiveLikeWidgetMediator): Boolean {
        return widgetList?.find { it.liveLikeWidget.id == liveLikeWidgetMediator.widgetData.id }?.widgetState == WidgetStates.INTERACTING
    }
}