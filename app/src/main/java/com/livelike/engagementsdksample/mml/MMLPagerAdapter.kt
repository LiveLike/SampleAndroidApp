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
    private val liveLikeSDKIntegrationManager: LiveLikeSDKIntegrationManager
) : PagerAdapter() {

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = when (position) {
            2 -> liveLikeSDKIntegrationManager.getChatView(context)
            3 -> liveLikeSDKIntegrationManager.getWidgetsView(context)
            else -> LayoutInflater.from(context).inflate(R.layout.mml_empty_chat_data_view, null)
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int = 4

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