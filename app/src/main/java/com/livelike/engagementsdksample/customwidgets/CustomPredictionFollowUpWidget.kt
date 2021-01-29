package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.livelike.engagementsdk.widget.widgetModel.FollowUpWidgetViewModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.parseDuration
import com.livelike.engagementsdksample.widget.model.LiveLikeWidgetOption
import kotlinx.android.synthetic.main.custom_prediction_widget.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class CustomPredictionFollowUpWidget : ConstraintLayout {

    private lateinit var imageOptionsWidgetAdapter: ImageOptionsWidgetAdapter
    lateinit var followUpWidgetViewModel: FollowUpWidgetViewModel
    var isTimeLine = false

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }


    private fun init(attrs: AttributeSet?, defStyle: Int) {
        inflate(context, R.layout.custom_prediction_widget, this)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        widget_type_label.text = context.getString(R.string.prediction_follow_up)
        followUpWidgetViewModel.widgetData.let { liveLikeWidget ->
            widget_title.text = liveLikeWidget.question
            if (isTimeLine) {
                time_bar.visibility = View.INVISIBLE
            } else {
                val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: DEFAULT_INTERACTION_TIME
                time_bar.startTimer(timeMillis)
                (context as AppCompatActivity).lifecycleScope.async {
                    delay(timeMillis)
                    followUpWidgetViewModel.finish()
                }
            }


            followUpWidgetViewModel.claimRewards()
            liveLikeWidget.options?.let {
                val totalVotes = it.sumBy { it?.voteCount ?: 0 }
                imageOptionsWidgetAdapter =
                    ImageOptionsWidgetAdapter(
                        context, true, ArrayList(it.map { item ->
                            LiveLikeWidgetOption(
                                item?.id!!,
                                item.description ?: "",
                                item.isCorrect ?: false,
                                item.imageUrl,
                                when (totalVotes > 0) {
                                    true -> (((item.voteCount ?: 0) * 100) / totalVotes)
                                    else -> 0
                                }
                            )
                        })
                    ) {}
                widget_rv.layoutManager = GridLayoutManager(context, 2)
                imageOptionsWidgetAdapter.isResultState = true
                imageOptionsWidgetAdapter.isResultAvailable = true
                imageOptionsWidgetAdapter.selectedOptionItem =
                    imageOptionsWidgetAdapter.list.find { it.id == followUpWidgetViewModel.getPredictionVoteId() }
                widget_rv.adapter = imageOptionsWidgetAdapter
            }
            lottie_animation_view?.apply {
                if (imageOptionsWidgetAdapter.selectedOptionItem?.isCorrect == false) {
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
        subscribeToVoteResults()
    }

    private fun subscribeToVoteResults() {
        followUpWidgetViewModel.voteResults.subscribe(this) { result ->
            val totalVotes = result?.choices?.sumBy { it.vote_count ?: 0 } ?: 0
            result?.choices?.zip(imageOptionsWidgetAdapter.list)?.let { options ->
                imageOptionsWidgetAdapter.list = ArrayList(options.map { item ->
                    LiveLikeWidgetOption(
                        item.second.id,
                        item.second.description,
                        item.first.is_correct,
                        item.second.imageUrl,
                        when (totalVotes > 0) {
                            true -> (((item.first.vote_count ?: 0) * 100) / totalVotes)
                            else -> 0
                        }
                    )
                })
                imageOptionsWidgetAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        followUpWidgetViewModel.voteResults?.unsubscribe(this)
    }


}