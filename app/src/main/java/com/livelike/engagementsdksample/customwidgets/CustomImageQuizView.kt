package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.livelike.engagementsdk.widget.widgetModel.QuizWidgetModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.parseDuration
import com.livelike.engagementsdksample.widget.model.LiveLikeQuizOption
import kotlinx.android.synthetic.main.custom_quiz_widget.view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CustomImageQuizView(context: Context, var quizWidgetModel: QuizWidgetModel? = null) :  FrameLayout(context) {

    private lateinit var adapter: QuizViewAdapter
    private var quizAnswerJob: Job? = null

   init {
        inflate(context, R.layout.custom_quiz_widget, this)
        quizWidgetModel?.widgetData?.let { liveLikeWidget ->
            // TODO  change sdk api for duration, it should passes duration in millis, parsing should be done at sdk side.
            val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: 5000
            time_bar.startTimer(timeMillis)
            quiz_title.text = liveLikeWidget.question

            (context as AppCompatActivity).lifecycleScope.async {
                delay(timeMillis)
                adapter.isResultState = true
                adapter.notifyDataSetChanged()
                adapter.selectedOptionItem?.let {
                delay(2000)
                }
             quizWidgetModel?.finish()
            }

            liveLikeWidget.choices?.let {
                 adapter =
                    QuizViewAdapter(context, ArrayList(it.map { item -> LiveLikeQuizOption(item?.id!!,item?.description?:"",false,item.imageUrl,item.answerCount) })
                    ) { option->
                        // TODO change sdk apis to have non-nullable option item ids
                        // 1000ms debounce added, TODO To discuss whether sdk should have inbuilt debounce to optimize sdk api calls
                        quizAnswerJob?.cancel()
                        quizAnswerJob =  context.lifecycleScope.launch {
                            delay(1000)
                            quizWidgetModel?.lockInAnswer(option.id ?: "")
                        }
                    }
                quiz_rv.layoutManager = GridLayoutManager(context,2)
                quiz_rv.adapter = adapter
                }
            }
        }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        subscribeToVoteResults()
    }

    private fun subscribeToVoteResults() {
        quizWidgetModel?.voteResults?.subscribe(this) { result ->
              val totalVotes  = result?.choices?.sumBy { it?.answer_count?:0 }?:0
            result?.choices?.zip(adapter.list)?.let { options ->
                adapter.isResultAvailable = true
                adapter.list = ArrayList(options.map { item -> LiveLikeQuizOption(item?.second.id!!,item?.second?.description?:"",item?.first.is_correct,item?.second.imageUrl,
                    (((item.first.answer_count?:0)*100)/ totalVotes))})
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        quizWidgetModel?.voteResults?.unsubscribe(this)
    }



}