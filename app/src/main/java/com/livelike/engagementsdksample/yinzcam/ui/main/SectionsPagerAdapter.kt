package com.livelike.engagementsdksample.yinzcam.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.livelike.engagementsdksample.yinzcam.ui.chat.ChatFragment
import com.livelike.engagementsdksample.yinzcam.ui.widget.WidgetFragment


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return when (position) {
            0 -> ChatFragment.newInstance()
            1 -> WidgetFragment.newInstance()
            else -> ChatFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Chat"
            1 -> "Widgets"
            else -> ""
        }
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}