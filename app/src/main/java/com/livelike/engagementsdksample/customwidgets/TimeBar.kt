package com.livelike.engagementsdksample.customwidgets

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.custom_quiz_widget.view.*

class TimeBar : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )


    fun startTimer(time : Long){
//            time_bar.measure( View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY)
        time_bar.pivotX = 0f
        ObjectAnimator.ofFloat(time_bar,"scaleX",0f,1f).apply {
            this.duration = time
            start()
        }
    }


}