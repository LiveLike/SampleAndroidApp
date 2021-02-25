package com.mml.mmlengagementsdk.widgets

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.mmlengagementsdk.R
import com.livelike.engagementsdk.widget.widgetModel.AlertWidgetModel
import com.mml.mmlengagementsdk.widgets.timeline.TimelineWidgetResource
import com.mml.mmlengagementsdk.widgets.utils.getFormattedTime
import com.mml.mmlengagementsdk.widgets.utils.parseDuration
import com.mml.mmlengagementsdk.widgets.utils.setCustomFontWithTextStyle
import kotlinx.android.synthetic.main.mml_alert_widget.view.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.max

class MMLAlertWidget(context: Context) : ConstraintLayout(context) {

    private val job = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    lateinit var alertModel: AlertWidgetModel
    var timelineWidgetResource: TimelineWidgetResource? = null

    init {
        inflate(context, R.layout.mml_alert_widget, this)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        alertModel.widgetData.let { liveLikeWidget ->
            txt_title.text = liveLikeWidget.title
            setCustomFontWithTextStyle(txt_title, "fonts/RingsideExtraWide-Black.otf")
            liveLikeWidget.text?.let {
                txt_description.visibility = View.VISIBLE
                txt_description.text = liveLikeWidget.text
            }
            setCustomFontWithTextStyle(txt_description, "fonts/RingsideRegular-Book.otf")
            liveLikeWidget.imageUrl?.let {
                img_alert.visibility = View.VISIBLE
                Glide.with(context)
                    .load(it)
                    .into(img_alert)
            }
            liveLikeWidget.createdAt?.let {
                setCustomFontWithTextStyle(txt_time, "fonts/RingsideRegular-Book.otf")
                txt_time.text = getFormattedTime(it)
            }
            liveLikeWidget.linkLabel?.let {
                btn_link.visibility = View.VISIBLE
                btn_link.text = it
                setCustomFontWithTextStyle(btn_link, "fonts/RingsideRegular-Book.otf")
                liveLikeWidget.linkUrl?.let { url ->
                    btn_link.setOnClickListener {
                        alertModel.alertLinkClicked(url)
                        val universalLinkIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(liveLikeWidget.linkUrl)).setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                            )
                        if (universalLinkIntent.resolveActivity(context.packageManager) != null) {
                            ContextCompat.startActivity(context, universalLinkIntent, Bundle.EMPTY)
                        }
                    }
                }
            }
            if (timelineWidgetResource?.isActive == false) {
                time_bar.visibility = View.GONE
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
                uiScope.async {
                    delay(remainingTimeMillis)
                    timelineWidgetResource?.isActive = false
                    if (timelineWidgetResource == null) {
                        alertModel.finish()
                    } else {
                        time_bar.visibility = View.GONE
                    }
                }
            }
        }
    }
}