package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.livelike.engagementsdk.widget.widgetModel.ImageSliderWidgetModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.customwidgets.imageslider.ScaleDrawable
import com.livelike.engagementsdksample.customwidgets.imageslider.ThumbDrawable
import com.livelike.engagementsdksample.parseDuration
import kotlinx.android.synthetic.main.custom_image_slider.view.*
import kotlinx.coroutines.*

class CustomEmojiSlider : ConstraintLayout {

    lateinit var imageSliderWidgetModel: ImageSliderWidgetModel
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
        inflate(context, R.layout.custom_image_slider, this)
    }

    private val viewModelJob = SupervisorJob()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        imageSliderWidgetModel.widgetData.let { widget ->
            slider_title.text = widget.question
            val size = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                36f,
                resources.displayMetrics
            ).toInt()
            uiScope.launch {
                val list = mutableListOf<Deferred<Bitmap>>()
                withContext(Dispatchers.IO) {
                    widget.options?.forEach {
                        list.add(
                            async {
                                Glide.with(context)
                                    .asBitmap()
                                    .load(it?.imageUrl)
                                    .centerCrop().submit(size, size).get()
                            }
                        )
                    }
                    val drawableList = list.map { t ->
                        ScaleDrawable(
                            t.await()
                        )
                    }
                    withContext(Dispatchers.Main) {
                        val drawable =
                            ThumbDrawable(
                                drawableList,
                                .5f
                            )
                        image_slider.thumbDrawable = drawable
                    }
                }
            }
            if (isTimeLine) {
                image_slider.averageProgress = widget.averageMagnitude
                time_bar.visibility = View.INVISIBLE
                image_slider.isUserSeekable = false
            } else {
                image_slider.isUserSeekable = true
                val timeMillis = widget.timeout?.parseDuration() ?: 5000
                time_bar.startTimer(timeMillis)
                (context as AppCompatActivity).lifecycleScope.async {
                    delay(timeMillis)
                    imageSliderWidgetModel.lockInVote(image_slider.progress.toDouble())
                    delay(2500)
                    imageSliderWidgetModel.finish()
                }
                imageSliderWidgetModel.voteResults.subscribe(this) {
                    it?.let {
                        image_slider.averageProgress = it.averageMagnitude
                    }
                }
            }
        }


    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        imageSliderWidgetModel.voteResults.unsubscribe(this)
    }


}