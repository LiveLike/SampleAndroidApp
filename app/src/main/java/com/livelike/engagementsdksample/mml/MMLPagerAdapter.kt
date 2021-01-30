package com.livelike.engagementsdksample.mml

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.livelike.engagementsdksample.R
import com.mml.mmlengagementsdk.LiveLikeSDKIntegrationManager

class MMLPagerAdapter(
    val context: Context,
    liveLikeSDKIntegrationManager: LiveLikeSDKIntegrationManager
) : PagerAdapter() {
    private val views =
        arrayListOf<View>(
            LayoutInflater.from(context).inflate(R.layout.mml_empty_chat_data_view, null),
            LayoutInflater.from(context).inflate(R.layout.mml_empty_chat_data_view, null),
            liveLikeSDKIntegrationManager.getChatView(context),
            liveLikeSDKIntegrationManager.getWidgetsView(context)
        )

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = views[position]
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int = views.size

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Chat 1"
            1 -> "Widget1"
            2 -> "Chat"
            3 -> "Widget"
            else -> ""
        }
    }
}