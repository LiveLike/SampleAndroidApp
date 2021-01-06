package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.livelike.engagementsdk.widget.widgetModel.FollowUpWidgetViewModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.parseDuration
import com.livelike.engagementsdksample.widget.model.LiveLikeWidgetOption
import kotlinx.android.synthetic.main.custom_prediction_widget.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class CustomPredictionFollowUpWidget(context : Context, val followUpWidgetViewModel: FollowUpWidgetViewModel): FrameLayout(context)  {

    private lateinit var imageOptionsWidgetAdapter: ImageOptionsWidgetAdapter

    init {
        inflate(context, R.layout.custom_prediction_widget, this)

        widget_type_label.text = context.getString(R.string.prediction_follow_up)
        followUpWidgetViewModel?.widgetData?.let { liveLikeWidget ->
            val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: DEFAULT_INTERACTION_TIME
            time_bar.startTimer(timeMillis)
            widget_title.text = liveLikeWidget.question

            (context as AppCompatActivity).lifecycleScope.async {
                delay(timeMillis)
                followUpWidgetViewModel?.finish()
            }
            followUpWidgetViewModel.claimRewards()
            liveLikeWidget.options?.let {
                val totalVotes  = it?.sumBy { it?.voteCount?:0 }?:0
                imageOptionsWidgetAdapter =
                    ImageOptionsWidgetAdapter(context, ArrayList(it.map { item -> LiveLikeWidgetOption(item?.id!!,item?.description?:"",item.isCorrect?:false,item.imageUrl,
                        (((item.voteCount?:0)*100)/ totalVotes))})
                    ) {}
                widget_rv.layoutManager = GridLayoutManager(context,2)
                imageOptionsWidgetAdapter.isResultState = true
                imageOptionsWidgetAdapter.isResultAvailable = true
                imageOptionsWidgetAdapter.selectedOptionItem = imageOptionsWidgetAdapter.list.find { it.id == followUpWidgetViewModel.getPredictionVoteId() }
                widget_rv.adapter = imageOptionsWidgetAdapter
            }
            lottie_animation_view?.apply {
                if(imageOptionsWidgetAdapter.selectedOptionItem?.isCorrect == false){
                    setAnimation(
                        "GSW_incorrect.json"
                    )
                } else {
                    setAnimation(
                        "GSW_correct.json"
                    )
                }
                playAnimation()
                visibility = View.VISIBLE
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        subscribeToVoteResults()
    }

    private fun subscribeToVoteResults() {
        followUpWidgetViewModel?.voteResults?.subscribe(this) { result ->
            val totalVotes  = result?.choices?.sumBy { it?.vote_count?:0 }?:0
            result?.choices?.zip(imageOptionsWidgetAdapter.list)?.let { options ->
                imageOptionsWidgetAdapter.list = ArrayList(options.map { item -> LiveLikeWidgetOption(item?.second.id!!,item?.second?.description?:"",item?.first.is_correct,item?.second.imageUrl,
                    (((item.first.vote_count?:0)*100)/ totalVotes))})
                imageOptionsWidgetAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        followUpWidgetViewModel?.voteResults?.unsubscribe(this)
    }


}