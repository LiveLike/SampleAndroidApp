package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.livelike.engagementsdk.widget.widgetModel.QuizWidgetModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.parseDuration
import com.livelike.engagementsdksample.widget.model.LiveLikeWidgetOption
import kotlinx.android.synthetic.main.custom_quiz_widget.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomImageQuizView :
    ConstraintLayout {
    lateinit var quizWidgetModel: QuizWidgetModel
    private lateinit var adapter: ImageOptionsWidgetAdapter
    private var quizAnswerJob: Job? = null
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
        inflate(context, R.layout.custom_quiz_widget, this)

    }

    private fun showResultAnimation() {
        lottie_animation_view?.apply {
            if (adapter.selectedOptionItem?.isCorrect == false) {
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


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        quizWidgetModel.widgetData.let { liveLikeWidget ->
            liveLikeWidget.choices?.let {
                adapter =
                    ImageOptionsWidgetAdapter(
                        context,
                        ArrayList(it.map { item ->
                            LiveLikeWidgetOption(
                                item?.id!!,
                                item.description ?: "",
                                item.isCorrect ?: false,
                                item.imageUrl,
                                item.answerCount
                            )
                        })
                    ) { option ->
                        // TODO change sdk apis to have non-nullable option item ids
                        // 1000ms debounce added, TODO To discuss whether sdk should have inbuilt debounce to optimize sdk api calls
                        quizAnswerJob?.cancel()
                        quizAnswerJob = (context as AppCompatActivity).lifecycleScope.launch {
                            delay(1000)
                            quizWidgetModel.lockInAnswer(option.id ?: "")
                        }
                    }
                quiz_rv.layoutManager = GridLayoutManager(context, 2)
                quiz_rv.adapter = adapter
            }
            // TODO  change sdk api for duration, it should passes duration in millis, parsing should be done at sdk side.
            if (isTimeLine) {
                time_bar.visibility = View.INVISIBLE
                val totalVotes = liveLikeWidget.choices?.sumBy { it?.answerCount ?: 0 } ?: 0
                liveLikeWidget.choices?.zip(adapter.list)?.let { options ->
                    adapter.isResultState = true
                    adapter.isResultAvailable = true
                    adapter.list = ArrayList(options.map { item ->
                        LiveLikeWidgetOption(
                            item.second.id,
                            item.second.description,
                            item.first?.isCorrect ?: false,
                            item.second.imageUrl,
                            (((item.first?.answerCount ?: 0) * 100) / totalVotes)
                        )
                    })
                    adapter.notifyDataSetChanged()
                }
            } else {
                val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: 5000
                time_bar.startTimer(timeMillis)
                quiz_title.text = liveLikeWidget.question

                (context as AppCompatActivity).lifecycleScope.async {
                    delay(timeMillis)
                    adapter.isResultState = true
                    adapter.notifyDataSetChanged()
                    adapter.selectedOptionItem?.let {
                        showResultAnimation()
                        delay(2000)
                    }
                    quizWidgetModel.finish()
                }
            }
        }
        subscribeToVoteResults()
    }

    private fun subscribeToVoteResults() {
        quizWidgetModel.voteResults.subscribe(this) { result ->
            val totalVotes = result?.choices?.sumBy { it.answer_count ?: 0 } ?: 0
            result?.choices?.zip(adapter.list)?.let { options ->
                adapter.isResultAvailable = true
                adapter.list = ArrayList(options.map { item ->
                    LiveLikeWidgetOption(
                        item.second.id,
                        item.second.description ?: "",
                        item.first.is_correct,
                        item.second.imageUrl,
                        (((item.first.answer_count ?: 0) * 100) / totalVotes)
                    )
                })
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        quizWidgetModel.voteResults.unsubscribe(this)
    }


}