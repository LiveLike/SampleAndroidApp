package com.livelike.engagementsdksample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeWidget
import com.livelike.engagementsdk.chat.data.remote.LiveLikePagination
import com.livelike.engagementsdk.publicapis.LiveLikeCallback
import kotlinx.android.synthetic.main.activity_list_widget.*
import kotlinx.android.synthetic.main.list_widget_item.view.*

class ListWidgetActivity : AppCompatActivity() {
    private lateinit var engagementSDK: EngagementSDK
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_widget)
        engagementSDK =
            EngagementSDK("3WtkbrjmyPFUHTSckcVVUlikAAdHEy1P0zqqczF0", applicationContext)
        val session = engagementSDK.createContentSession("8f34411b-159c-4840-b8ca-794785dfc1ca")
        val adapter = WidgetAdapter(engagementSDK)
        rcyl_widgets.adapter = adapter
        session.getPublishedWidgets(LiveLikePagination.FIRST, object :
            LiveLikeCallback<List<LiveLikeWidget>>() {
            override fun onResponse(result: List<LiveLikeWidget>?, error: String?) {
                result?.let {
                    adapter.list.addAll(it)
                }
                adapter.notifyDataSetChanged()
            }
        })
    }
}

class WidgetAdapter(private val engagementSDK: EngagementSDK) : RecyclerView.Adapter<WidgetViewHolder>() {
    val list = arrayListOf<LiveLikeWidget>()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): WidgetViewHolder {
        return WidgetViewHolder(
            LayoutInflater.from(p0.context).inflate(R.layout.list_widget_item, p0, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(p0: WidgetViewHolder, p1: Int) {
        val liveLikeWidget = list[p1]
        p0.itemView.widget_view.displayWidget(engagementSDK, liveLikeWidget)
    }
}


class WidgetViewHolder(view: View) : RecyclerView.ViewHolder(view)