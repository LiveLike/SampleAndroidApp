package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.livelike.engagementsdk.OptionsItem
import com.livelike.engagementsdk.widget.widgetModel.CheerMeterWidgetmodel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.parseDuration
import kotlinx.android.synthetic.main.custom_cheer_meter.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class CustomCheerMeter : ConstraintLayout {

    lateinit var cheerMeterWidgetmodel: CheerMeterWidgetmodel
    var winnerOptionItem: OptionsItem? = null
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
        inflate(context, R.layout.custom_cheer_meter, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        cheerMeterWidgetmodel.widgetData.let { likeWidget ->

            if (isTimeLine) {
                time_bar.visibility = View.INVISIBLE
                val op1 = likeWidget.options?.get(0)
                val op2 = likeWidget.options?.get(1)
                val vt1 = op1?.voteCount ?: 0
                val vt2 = op2?.voteCount ?: 0
                val total = vt1 + vt2
                if (total > 0) {
                    val perVt1 = (vt1.toFloat() / total) * 100
                    val perVt2 = (vt2.toFloat() / total) * 100
                    prg_cheer_team_1.progress = perVt1.toInt()
                    prg_cheer_team_2.progress = perVt2.toInt()
                    winnerOptionItem = if (perVt1 > perVt2) {
                        likeWidget.options?.get(0)
                    } else {
                        likeWidget.options?.get(1)
                    }
                    showWinnerAnimation()
                }
            } else {
                val timeMillis = likeWidget.timeout?.parseDuration() ?: 5000
                time_bar.startTimer(timeMillis)
                (context as AppCompatActivity).lifecycleScope.async {
                    delay(timeMillis)
                    showWinnerAnimation()
                    delay(5000)
                    cheerMeterWidgetmodel.finish()
                }
            }


            txt_title.text = likeWidget.question
            vs_anim.setAnimation("vs-1-light.json")
            vs_anim.playAnimation()
            likeWidget.options?.let { options ->
                if (options.size == 2) {
                    options[0]?.let { op ->
                        Glide.with(context)
                            .load(op.imageUrl)
                            .into(img_cheer_team_1)
                        if (!isTimeLine)
                            frame_cheer_team_1.setOnClickListener {
                                cheerMeterWidgetmodel.submitVote(op.id!!)
                            }
                    }
                    options[1]?.let { op ->
                        Glide.with(context)
                            .load(op.imageUrl)
                            .into(img_cheer_team_2)
                        if (!isTimeLine)
                            frame_cheer_team_2.setOnClickListener {
                                cheerMeterWidgetmodel.submitVote(op.id!!)
                            }
                    }
                    cheerMeterWidgetmodel.voteResults.subscribe(this) {
                        it?.let {
                            val op1 = it.choices?.get(0)
                            val op2 = it.choices?.get(1)
                            val vt1 = op1?.vote_count ?: 0
                            val vt2 = op2?.vote_count ?: 0
                            val total = vt1 + vt2
                            if (total > 0) {
                                val perVt1 = (vt1.toFloat() / total) * 100
                                val perVt2 = (vt2.toFloat() / total) * 100
                                prg_cheer_team_1.progress = perVt1.toInt()
                                prg_cheer_team_2.progress = perVt2.toInt()
                                winnerOptionItem = if (perVt1 > perVt2) {
                                    options[0]
                                } else {
                                    options[1]
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private fun showWinnerAnimation() {
        winnerOptionItem?.let { op ->
            cheer_result_team.visibility = View.VISIBLE
            Glide.with(this@CustomCheerMeter.context)
                .load(op.imageUrl)
                .into(img_winner_team)
            img_winner_anim.setAnimation("winner_animation.json")
            img_winner_anim.playAnimation()
        }
    }
}