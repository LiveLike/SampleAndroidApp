package com.mml.mmlengagementsdk.widgets

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mmlengagementsdk.R
import com.livelike.engagementsdk.widget.widgetModel.QuizWidgetModel
import com.mml.mmlengagementsdk.widgets.adapter.QuizListAdapter
import com.mml.mmlengagementsdk.widgets.model.LiveLikeWidgetOption
import com.mml.mmlengagementsdk.widgets.timeline.TimelineWidgetResource
import com.mml.mmlengagementsdk.widgets.utils.DEFAULT_DELAY_TIME_FOR_RESULT
import com.mml.mmlengagementsdk.widgets.utils.getFormattedTime
import com.mml.mmlengagementsdk.widgets.utils.parseDuration
import com.mml.mmlengagementsdk.widgets.utils.setCustomFontWithTextStyle
import kotlinx.android.synthetic.main.mml_quiz_widget.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

class MMLQuizWidget(context: Context) : ConstraintLayout(context) {
    private var selectedOption: LiveLikeWidgetOption? = null
    lateinit var quizWidgetModel: QuizWidgetModel
    private lateinit var adapter: QuizListAdapter
    private var quizAnswerJob: Job? = null
    var isImage = false
    private val job = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    var timelineWidgetResource: TimelineWidgetResource? = null

    init {
        inflate(context, R.layout.mml_quiz_widget, this)
    }

    private fun showResultAnimation() {
        lottie_animation_view?.apply {
            if (adapter.selectedOptionItem?.isCorrect == false) {
                setAnimation(
                    "mml/quiz_incorrect.json"
                )
            } else {
                setAnimation(
                    "mml/quiz_correct.json"
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

                if (isImage) {
                    quiz_rv.layoutManager = GridLayoutManager(context, 2)
                } else {
                    quiz_rv.layoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }

                adapter =
                    QuizListAdapter(
                        context,
                        isImage,
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
                        selectedOption = option
                        quizAnswerJob?.cancel()
                        quizAnswerJob = uiScope.launch {
                            delay(1000)
                            quizWidgetModel.lockInAnswer(option.id ?: "")
                        }
                    }.apply {
                        selectedOptionItem = selectedOption
                    }
                quiz_rv.adapter = adapter
            }
            liveLikeWidget.createdAt?.let {
                setCustomFontWithTextStyle(txt_time, "fonts/RingsideRegular-Book.otf")
                txt_time.text = getFormattedTime(it)
            }
            quiz_title.text = liveLikeWidget.question
            setCustomFontWithTextStyle(quiz_title, "fonts/RingsideExtraWide-Black.otf")
            // TODO  change sdk api for duration, it should passes duration in millis, parsing should be done at sdk side.
            if (timelineWidgetResource?.isActive == false) {
                time_bar.visibility = View.INVISIBLE
                val totalVotes = timelineWidgetResource?.liveLikeWidgetResult?.choices?.sumBy {
                    it.answer_count ?: 0
                } ?: liveLikeWidget.choices?.sumBy { it?.answerCount ?: 0 } ?: 0
                adapter.isResultState = true
                adapter.isResultAvailable = true
                liveLikeWidget.choices?.zip(adapter.list)?.let { options ->
                    adapter.list = ArrayList(options.map { item ->
                        LiveLikeWidgetOption(
                            item.second.id,
                            item.second.description,
                            item.first?.isCorrect ?: false,
                            item.second.imageUrl,
                            when (totalVotes > 0) {
                                true -> (((item.first?.answerCount ?: 0) * 100) / totalVotes)
                                else -> 0
                            }
                        )
                    })
                }
                timelineWidgetResource?.liveLikeWidgetResult?.choices?.zip(adapter.list)
                    ?.let { options ->
                        adapter.isResultAvailable = true
                        adapter.list = ArrayList(options.map { item ->
                            LiveLikeWidgetOption(
                                item.second.id,
                                item.second.description,
                                item.first.is_correct,
                                item.second.imageUrl,
                                when (totalVotes > 0) {
                                    true -> (((item.first.answer_count ?: 0) * 100) / totalVotes)
                                    else -> 0
                                }
                            )
                        })
                    }
                adapter.notifyDataSetChanged()
            } else {
                val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: 5000
                val remainingTimeMillis = when (timelineWidgetResource == null) {
                    true -> timeMillis
                    else -> {
                        if (timelineWidgetResource?.startTime == null) {
                            timelineWidgetResource?.startTime = Calendar.getInstance().timeInMillis
                        }
                        val timeDiff =
                            Calendar.getInstance().timeInMillis - (timelineWidgetResource?.startTime
                                ?: 0L)
                        max(0, timeMillis - timeDiff)
                    }
                }
                time_bar.visibility = View.VISIBLE
                time_bar.startTimer(timeMillis, remainingTimeMillis)
                subscribeToVoteResults()
                uiScope.async {
                    delay(remainingTimeMillis)
                    adapter.isResultState = true
                    adapter.notifyDataSetChanged()
                    adapter.selectedOptionItem?.let {
                        showResultAnimation()
                        delay(DEFAULT_DELAY_TIME_FOR_RESULT)
                        timelineWidgetResource?.isActive = false
                        quizWidgetModel.voteResults.unsubscribe(this@MMLQuizWidget)
                    }
                    if (timelineWidgetResource == null) {
                        quizWidgetModel.finish()
                    } else {
                        time_bar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun subscribeToVoteResults() {
        quizWidgetModel.voteResults.subscribe(this@MMLQuizWidget) { result ->
            val totalVotes = result?.choices?.sumBy { it.answer_count ?: 0 } ?: 0
            result?.choices?.zip(adapter.list)?.let { options ->
                adapter.isResultAvailable = true
                adapter.list = ArrayList(options.map { item ->
                    LiveLikeWidgetOption(
                        item.second.id,
                        item.second.description,
                        item.first.is_correct,
                        item.second.imageUrl,
                        (((item.first.answer_count ?: 0) * 100) / totalVotes)
                    )
                })
                adapter.notifyDataSetChanged()
            }
            timelineWidgetResource?.liveLikeWidgetResult = result
        }
    }
}