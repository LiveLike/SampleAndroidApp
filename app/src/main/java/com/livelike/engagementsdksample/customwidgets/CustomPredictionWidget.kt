package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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

class CustomPredictionWidget : ConstraintLayout {
    lateinit var predictionWidgetViewModel: PredictionWidgetViewModel

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

    private lateinit var imageOptionsWidgetAdapter: ImageOptionsWidgetAdapter
    var isTimeLine = false

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        inflate(context, R.layout.custom_prediction_widget, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        predictionWidgetViewModel.widgetData.let { liveLikeWidget ->
            widget_title.text = liveLikeWidget.question
            liveLikeWidget.options?.let {
                imageOptionsWidgetAdapter =
                    ImageOptionsWidgetAdapter(
                        context,
                        ArrayList(it.map { item ->
                            LiveLikeWidgetOption(
                                item?.id!!,
                                item.description ?: "",
                                false,
                                item.imageUrl,
                                item.answerCount
                            )
                        })
                    ) { option ->
                        predictionWidgetViewModel.lockInVote(option.id ?: "")
                    }
                imageOptionsWidgetAdapter.indicateRightAnswer = false
                widget_rv.layoutManager = GridLayoutManager(context, 2)
                widget_rv.adapter = imageOptionsWidgetAdapter
            }

            if (isTimeLine) {
                time_bar.visibility = View.INVISIBLE
                imageOptionsWidgetAdapter.isResultState = true
                imageOptionsWidgetAdapter.isResultAvailable = true
                val totalVotes = liveLikeWidget.options?.sumBy { it?.voteCount ?: 0 } ?: 0

                liveLikeWidget.options?.zip(imageOptionsWidgetAdapter.list)?.let { options ->
                    imageOptionsWidgetAdapter.list = ArrayList(options.map { item ->
                        LiveLikeWidgetOption(
                            item.second.id,
                            item.second.description,
                            item.first?.isCorrect ?: false,
                            item.second.imageUrl,
                            when (totalVotes > 0) {
                                true -> (((item.first?.voteCount ?: 0) * 100) / totalVotes)
                                else -> 0
                            }
                        )
                    })
                    imageOptionsWidgetAdapter.notifyDataSetChanged()
                }

            } else {
                val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: DEFAULT_INTERACTION_TIME
                time_bar.startTimer(timeMillis)
                (context as AppCompatActivity).lifecycleScope.async {
                    delay(timeMillis)
                    imageOptionsWidgetAdapter.isResultState = true
                    imageOptionsWidgetAdapter.notifyDataSetChanged()
                    lottie_animation_view?.apply {
                        setAnimation(
                            "stay_tuned.json"
                        )
                        playAnimation()
                        visibility = View.VISIBLE
                    }
                    delay(2000)
                    predictionWidgetViewModel.finish()
                }
            }
        }
        subscribeToVoteResults()
    }

    private fun subscribeToVoteResults() {
        predictionWidgetViewModel.voteResults.subscribe(this) { result ->
            imageOptionsWidgetAdapter.isResultAvailable = true
            val totalVotes = result?.choices?.sumBy { it.vote_count ?: 0 } ?: 0
            result?.choices?.zip(imageOptionsWidgetAdapter.list)?.let { options ->
                imageOptionsWidgetAdapter.list = ArrayList(options.map { item ->
                    LiveLikeWidgetOption(
                        item.second.id,
                        item.second.description,
                        item.first.is_correct,
                        item.second.imageUrl,
                        (((item.first.vote_count ?: 0) * 100) / totalVotes)
                    )
                })
                imageOptionsWidgetAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        predictionWidgetViewModel.voteResults.unsubscribe(this)
    }


}