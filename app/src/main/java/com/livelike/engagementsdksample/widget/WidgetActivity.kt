package com.livelike.engagementsdksample.widget

import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.livelike.engagementsdk.EngagementSDK
import com.livelike.engagementsdk.LiveLikeWidget
import com.livelike.engagementsdk.chat.data.remote.LiveLikePagination
import com.livelike.engagementsdk.core.services.messaging.proxies.LiveLikeWidgetEntity
import com.livelike.engagementsdk.core.services.messaging.proxies.WidgetInterceptor
import com.livelike.engagementsdk.publicapis.LiveLikeCallback
import com.livelike.engagementsdk.widget.LiveLikeWidgetViewFactory
import com.livelike.engagementsdk.widget.widgetModel.*
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.customwidgets.*
import com.livelike.engagementsdksample.customwidgets.poll.CustomPollWidget
import com.livelike.engagementsdksample.widget.viewmodels.EngagementViewModelFactory
import com.livelike.engagementsdksample.widget.viewmodels.widgetViewModel
import kotlinx.android.synthetic.main.activity_widget.*
import kotlinx.android.synthetic.main.time_line_item.view.*


class WidgetActivity : AppCompatActivity() {

    var mainViewModel: widgetViewModel? = null
    private lateinit var adapter: TimeLineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget)

        // to get intent received from MainActivity

        // This will create an instance of Engagement viewmodel which can be used to creating session and initialization
        mainViewModel = ViewModelProvider(
            this,
            EngagementViewModelFactory(this.applicationContext as Application)
        ).get(widgetViewModel::class.java)
        // Check whether chat or widget is selected

        widget_view_container.widgetViewFactory = object : LiveLikeWidgetViewFactory {
            override fun createAlertWidgetView(alertWidgetModel: AlertWidgetModel): View? {
                return CustomAlert(this@WidgetActivity).apply {
                    this.alertModel = alertWidgetModel
                    this.isSponsor = true
                }
            }

            override fun createCheerMeterView(cheerMeterWidgetModel: CheerMeterWidgetmodel): View? {
                return CustomCheerMeter(this@WidgetActivity).apply {
                    this.cheerMeterWidgetmodel = cheerMeterWidgetModel
                }
            }

            override fun createImageSliderWidgetView(imageSliderWidgetModel: ImageSliderWidgetModel): View? {
                return CustomEmojiSlider(this@WidgetActivity).apply {
                    this.imageSliderWidgetModel = imageSliderWidgetModel
                }
            }

            override fun createPollWidgetView(
                pollWidgetModel: PollWidgetModel,
                isImage: Boolean
            ): View? {
                return CustomPollWidget(this@WidgetActivity).apply {
                    this.pollWidgetModel = pollWidgetModel
                    this.isImage = isImage
                }
            }

            override fun createPredictionFollowupWidgetView(
                followUpWidgetViewModel: FollowUpWidgetViewModel,
                isImage: Boolean
            ): View? {
                if (isImage) {
                    return CustomPredictionFollowUpWidget(
                        this@WidgetActivity
                    ).apply {
                        this.isTimeLine = false
                        this.followUpWidgetViewModel = followUpWidgetViewModel
                    }
                } else {
                    return null
                }
            }

            override fun createPredictionWidgetView(
                predictionViewModel: PredictionWidgetViewModel,
                isImage: Boolean
            ): View? {
                if (isImage) {
                    return CustomPredictionWidget(this@WidgetActivity).apply {
                        this.predictionWidgetViewModel = predictionViewModel
                        this.isTimeLine = false
                    }
                } else {
                    return null
                }
            }

            override fun createQuizWidgetView(
                quizWidgetModel: QuizWidgetModel,
                isImage: Boolean
            ): View? {
                return CustomQuizView(this@WidgetActivity).apply {
                    this.quizWidgetModel = quizWidgetModel
                    this.isImage = isImage
                    this.isTimeLine = false
                }
            }

        }
        mainViewModel!!.getSession()?.let { widget_view_container.setSession(it) }
        // Example of Widget Interceptor showing a dialog
        val interceptor = object : WidgetInterceptor() {
            override fun widgetWantsToShow(widgetData: LiveLikeWidgetEntity) {
                AlertDialog.Builder(this@WidgetActivity).apply {
                    setMessage("You received a Widget, what do you want to do?")
                    setPositiveButton("Show") { _, _ ->
                        showWidget() // Releases the widget
                    }
                    setNegativeButton("Dismiss") { _, _ ->
                        dismissWidget() // Discards the widget
                    }
                    create()
                }.show()
            }
        }

        widget_view_container.setOnHierarchyChangeListener(object :
            ViewGroup.OnHierarchyChangeListener {
            override fun onChildViewRemoved(parent: View?, child: View?) {
                swipe_container.isRefreshing = true
                loadData(LiveLikePagination.FIRST)
            }

            override fun onChildViewAdded(parent: View?, child: View?) {

            }
        })

        // You just need to add it on your session instance
        mainViewModel?.getSession()?.widgetInterceptor = interceptor
        adapter = TimeLineAdapter(this, mainViewModel?.engagementSDK!!)
//        mainViewModel?.engagementSDK?.analyticService?.setEventObserver { eventKey, eventJson ->
//           Log.d("Sample_Events",eventKey)
//        }
        rcyl_widgets.adapter = adapter
        swipe_container.setOnRefreshListener {
            loadData(LiveLikePagination.FIRST)
        }


        rcyl_widgets.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var loading: Boolean = true
            var pastVisiblesItems = 0
            var visibleItemCount: Int = 0
            var totalItemCount: Int = 0
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { //check for scroll down
                    visibleItemCount = rcyl_widgets.layoutManager!!.childCount
                    totalItemCount = rcyl_widgets.layoutManager!!.itemCount
                    pastVisiblesItems =
                        (rcyl_widgets.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false
                            loadData(LiveLikePagination.NEXT)
                            loading = true
                        }
                    }
                }
            }
        })

        loadData(LiveLikePagination.FIRST)
    }

    private fun loadData(liveLikePagination: LiveLikePagination) {
        progress_bar.visibility = View.VISIBLE
        mainViewModel?.getSession()?.getPublishedWidgets(
            liveLikePagination,
            object : LiveLikeCallback<List<LiveLikeWidget>>() {
                override fun onResponse(result: List<LiveLikeWidget>?, error: String?) {
                    result?.let { list ->
                        if (liveLikePagination == LiveLikePagination.FIRST) {
                            adapter.list.clear()
                        }
                        adapter.list.addAll(list.map { it!! })
                        adapter.notifyDataSetChanged()
                        progress_bar.visibility = View.GONE
                        swipe_container.isRefreshing = false
                    }
                }
            })

    }

    override fun onPause() {
        super.onPause()
        mainViewModel?.pauseSession()
    }

    override fun onResume() {
        super.onResume()
        mainViewModel?.resumeSession()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel?.closeSession()
    }
}

class TimeLineAdapter(private val context: Context, private val engagementSDK: EngagementSDK) :
    RecyclerView.Adapter<TimeLineViewHolder>() {

    init {
        setHasStableIds(true)
    }

    val list: ArrayList<LiveLikeWidget> = arrayListOf()
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TimeLineViewHolder {
        return TimeLineViewHolder(
            LayoutInflater.from(p0.context).inflate(R.layout.time_line_item, p0, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].id?.hashCode() ?: position
    }

    override fun onBindViewHolder(viewHolder: TimeLineViewHolder, p1: Int) {
        val liveLikeWidget = list[p1]
        println("TimeLineAdapter.onBindViewHolder->${liveLikeWidget.kind}")
        viewHolder.itemView.widget_view.enableDefaultWidgetTransition = false
        viewHolder.itemView.widget_view.widgetViewFactory = object : LiveLikeWidgetViewFactory {
            override fun createAlertWidgetView(alertWidgetModel: AlertWidgetModel): View? {
                return CustomAlert(context).apply {
                    this.alertModel = alertWidgetModel
                    this.isSponsor = true
                    this.isTimeLine = true
                }
            }

            override fun createCheerMeterView(cheerMeterWidgetModel: CheerMeterWidgetmodel): View? {
                return CustomCheerMeter(context).apply {
                    this.cheerMeterWidgetmodel = cheerMeterWidgetModel
                    this.isTimeLine = true
                }
            }

            override fun createImageSliderWidgetView(imageSliderWidgetModel: ImageSliderWidgetModel): View? {
                return CustomEmojiSlider(context).apply {
                    this.imageSliderWidgetModel = imageSliderWidgetModel
                    this.isTimeLine = true
                }
            }

            override fun createPollWidgetView(
                pollWidgetModel: PollWidgetModel,
                isImage: Boolean
            ): View? {
                return CustomPollWidget(context).apply {
                    this.pollWidgetModel = pollWidgetModel
                    this.isImage = isImage
                    this.isTimeLine = true
                }
            }

            override fun createPredictionFollowupWidgetView(
                followUpWidgetViewModel: FollowUpWidgetViewModel,
                isImage: Boolean
            ): View? {
                if (isImage) {
                    return CustomPredictionFollowUpWidget(context).apply {
                        this.followUpWidgetViewModel = followUpWidgetViewModel
                        this.isTimeLine = true
                    }
                } else {
                    return null
                }
            }

            override fun createPredictionWidgetView(
                predictionViewModel: PredictionWidgetViewModel,
                isImage: Boolean
            ): View? {
                if (isImage) {
                    return CustomPredictionWidget(context).apply {
                        this.predictionWidgetViewModel = predictionViewModel
                        this.isTimeLine = true
                    }
                } else {
                    return null
                }
            }

            override fun createQuizWidgetView(
                quizWidgetModel: QuizWidgetModel,
                isImage: Boolean
            ): View? {
                return CustomQuizView(context).apply {
                    this.quizWidgetModel = quizWidgetModel
                    this.isTimeLine = true
                    this.isImage = isImage
                }
            }
        }

        viewHolder.itemView.widget_view.displayWidget(
            engagementSDK,
            liveLikeWidget
        )
    }

    override fun getItemCount(): Int = list.size

}

class TimeLineViewHolder(view: View) : RecyclerView.ViewHolder(view)
