package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.livelike.engagementsdk.widget.widgetModel.QuizWidgetModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.parseDuration
import com.livelike.engagementsdksample.widget.model.LiveLikeQuizOption
import kotlinx.android.synthetic.main.custom_quiz_widget.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class CustomImageQuizView :  FrameLayout {

    private lateinit var adapter: QuizViewAdapter
    var quizWidgetModel: QuizWidgetModel? = null


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
        quizWidgetModel?.widgetData?.let { liveLikeWidget ->
            // TODO  change sdk api for duration, it should passes duration in millis, parsing should be done at sdk side.
            val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: 5000

            time_bar.startTimer(timeMillis)
            (context as AppCompatActivity).lifecycleScope.async {
                delay(timeMillis)
                adapter.selectedOptionItem?.let {
                // TODO change sdk apis to have non-nullable option item ids
                quizWidgetModel?.lockInAnswer(it.id ?: "")
                delay(3000)
                }
             quizWidgetModel?.finish()
            }

            liveLikeWidget.choices?.let {
                 adapter =
                    QuizViewAdapter(context, ArrayList(it.map { item -> LiveLikeQuizOption(item?.id!!,item?.description?:"",false,item.imageUrl,item.answerCount) }))
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

            result?.choices?.let { options ->
                adapter.isResultState = true
                adapter.list = ArrayList(options.map { item -> LiveLikeQuizOption(item?.id!!,item?.description?:"",item.is_correct,item.image_url,item.percentage)})
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        quizWidgetModel?.voteResults?.unsubscribe(this)
    }



}