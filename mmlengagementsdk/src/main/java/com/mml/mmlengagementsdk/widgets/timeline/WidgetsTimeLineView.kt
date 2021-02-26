package com.mml.mmlengagementsdk.widgets.timeline

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mmlengagementsdk.R
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeContentSession
import com.livelike.engagementsdk.LiveLikeWidget
import com.livelike.engagementsdk.chat.data.remote.LiveLikePagination
import com.livelike.engagementsdk.publicapis.LiveLikeCallback
import kotlinx.android.synthetic.main.mml_timeline_view.view.*

class WidgetsTimeLineView(
    context: Context,
    private val session: LiveLikeContentSession,
    sdk: EngagementSDK
) : FrameLayout(context) {

    private var adapter: TimeLineViewAdapter

    init {
        inflate(context, R.layout.mml_timeline_view, this)
        adapter =
            TimeLineViewAdapter(
                context,
                sdk
            )
        timeline_rv.adapter = adapter
        loadPastPublishedWidgets()
    }

    private fun loadPastPublishedWidgets() {
        session.getPublishedWidgets(
            LiveLikePagination.FIRST,
            object : LiveLikeCallback<List<LiveLikeWidget>>() {
                override fun onResponse(result: List<LiveLikeWidget>?, error: String?) {
                    result?.let { list ->
                        adapter.list.addAll(
                            list.map { TimelineWidgetResource(false, it) })
                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }

    private fun observeForLiveWidgets() {
        session.widgetStream.subscribe(this) {
            it?.let {
                Handler(Looper.getMainLooper()).post {
                    val layoutManager = (timeline_rv.layoutManager as? LinearLayoutManager)
                    val allowScroll =
                        (layoutManager?.findFirstCompletelyVisibleItemPosition() == 0 || layoutManager?.findFirstVisibleItemPosition() == 0)
                    if (adapter.list.isEmpty() || !adapter.list.any { timeline -> timeline.liveLikeWidget.id == it.id }) {
                        adapter.list.add(0, TimelineWidgetResource(true, it))
                        adapter.notifyItemInserted(0)
                        if (allowScroll) {
                            timeline_rv.smoothScrollToPosition(0)
                        }
                    }
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        observeForLiveWidgets()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        session.widgetStream.unsubscribe(this)
    }


}