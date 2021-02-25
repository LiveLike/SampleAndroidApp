package com.mml.mmlengagementsdk.widgets

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mmlengagementsdk.R
import com.livelike.engagementsdk.widget.widgetModel.PollWidgetModel
import com.mml.mmlengagementsdk.widgets.adapter.PollListAdapter
import com.mml.mmlengagementsdk.widgets.timeline.TimelineWidgetResource
import com.mml.mmlengagementsdk.widgets.utils.getFormattedTime
import com.mml.mmlengagementsdk.widgets.utils.parseDuration
import com.mml.mmlengagementsdk.widgets.utils.setCustomFontWithTextStyle
import kotlinx.android.synthetic.main.mml_poll_widget.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set
import kotlin.math.max

class MMLPollWidget(context: Context) : ConstraintLayout(context) {
    private var selectedOptionId: String? = null
    lateinit var pollWidgetModel: PollWidgetModel
    var isImage = false
    private val job = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    var timelineWidgetResource: TimelineWidgetResource? = null

    init {
        inflate(context, R.layout.mml_poll_widget, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        pollWidgetModel.widgetData.let { liveLikeWidget ->
            txt_title.text = liveLikeWidget.question
            setCustomFontWithTextStyle(txt_title, "fonts/RingsideExtraWide-Black.otf")
            liveLikeWidget.createdAt?.let {
                setCustomFontWithTextStyle(txt_time, "fonts/RingsideRegular-Book.otf")
                txt_time.text = getFormattedTime(it)
            }
            liveLikeWidget.options?.let { list ->
                if (isImage) {
                    rcyl_poll_list.layoutManager = GridLayoutManager(context, 2)
                } else {
                    rcyl_poll_list.layoutManager =
                        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                }
                val adapter =
                    PollListAdapter(
                        context,
                        isImage,
                        ArrayList(list.map { item -> item!! })
                    ).apply {
                        selectedOptionId?.let {
                            selectedIndex = list.indexOfFirst { it?.id == selectedOptionId }
                        }
                    }

                rcyl_poll_list.adapter = adapter

                if (timelineWidgetResource?.isActive == false) {
                    adapter.isTimeLine = true
                    list.forEach { op ->
                        op?.let {
                            adapter.optionIdCount[op.id!!] = op.voteCount ?: 0
                        }
                    }
                    timelineWidgetResource?.liveLikeWidgetResult?.choices?.let { options ->
                        options.forEach { op ->
                            adapter.optionIdCount[op.id] = op.vote_count ?: 0
                        }
                    }
                    adapter.notifyDataSetChanged()
                    time_bar.visibility = View.INVISIBLE
                } else {
                    adapter.pollListener = object : PollListAdapter.PollListener {
                        override fun onSelectOption(id: String) {
                            if (selectedOptionId != id) {
                                selectedOptionId = id
                                pollWidgetModel.submitVote(id)
                            }
                        }
                    }
                    pollWidgetModel.voteResults.subscribe(this@MMLPollWidget) { result ->
                        result?.choices?.let { options ->
                            var change = false
                            options.forEach { op ->
                                if (!adapter.optionIdCount.containsKey(op.id) || adapter.optionIdCount[op.id] != op.vote_count) {
                                    change = true
                                }
                                adapter.optionIdCount[op.id] = op.vote_count ?: 0
                            }
                            if (change)
                                adapter.notifyDataSetChanged()
                        }
                        timelineWidgetResource?.liveLikeWidgetResult = result
                    }

                    val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: 5000
                    val remainingTimeMillis = when (timelineWidgetResource == null) {
                        true -> timeMillis
                        else -> {
                            if (timelineWidgetResource?.startTime == null) {
                                timelineWidgetResource?.startTime =
                                    Calendar.getInstance().timeInMillis
                            }
                            val timeDiff =
                                Calendar.getInstance().timeInMillis - (timelineWidgetResource?.startTime
                                    ?: 0L)
                            max(0, timeMillis - timeDiff)
                        }
                    }
                    time_bar.visibility = View.VISIBLE
                    time_bar.startTimer(timeMillis, remainingTimeMillis)

                    uiScope.async {
                        delay(remainingTimeMillis)
                        timelineWidgetResource?.isActive = false
                        adapter.isTimeLine = true
                        adapter.notifyDataSetChanged()
                        pollWidgetModel.voteResults.unsubscribe(this@MMLPollWidget)
                        if (timelineWidgetResource == null) {
                            pollWidgetModel.finish()
                        } else {
                            time_bar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

}