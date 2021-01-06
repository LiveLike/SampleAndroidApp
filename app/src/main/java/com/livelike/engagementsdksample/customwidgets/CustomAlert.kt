package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.livelike.engagementsdk.widget.widgetModel.AlertWidgetModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.parseDuration
import kotlinx.android.synthetic.main.custom_sponser_alert.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class CustomAlert : ConstraintLayout {
    lateinit var alertModel: AlertWidgetModel
    var isSponsor = false

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
        inflate(context, R.layout.custom_sponser_alert, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        alertModel.widgetData.let { likeWidget ->
            val timeMillis = likeWidget.timeout?.parseDuration() ?: 5000
            time_bar.startTimer(timeMillis)

            if (!isSponsor) {
                sponsor_container.visibility = View.GONE
            }

            txt_title.text = likeWidget.title
            txt_description.text = likeWidget.text
            likeWidget.imageUrl?.let {
                img_alert.visibility = View.VISIBLE
                Glide.with(context)
                    .load(it)
                    .into(img_alert)
            }
            likeWidget.linkLabel?.let {
                btn_link.visibility = View.VISIBLE
                btn_link.text = it
                likeWidget.linkUrl?.let {
                    btn_link.setOnClickListener {
                        val universalLinkIntent =
                            Intent(Intent.ACTION_VIEW, Uri.parse(likeWidget.linkUrl)).setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                            )
                        if (universalLinkIntent.resolveActivity(context.packageManager) != null) {
                            startActivity(context, universalLinkIntent, Bundle.EMPTY)
                        }
                    }
                }
            }
            (context as AppCompatActivity).lifecycleScope.async {
                delay(timeMillis)
                alertModel.finish()
            }
        }
    }

}