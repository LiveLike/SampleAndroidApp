package com.livelike.engagementsdksample.yinzcam.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.livelike.engagementsdk.widget.timeline.WidgetTimeLineViewModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.yinzcam.YinzCamActivity
import kotlinx.android.synthetic.main.widget_fragment.*

class WidgetFragment : Fragment() {

    companion object {
        fun newInstance() = WidgetFragment()
    }

    private lateinit var viewModel: WidgetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.widget_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? YinzCamActivity)?.let {
            val timeLineViewModel = WidgetTimeLineViewModel(it.session)
            val timeLineView = WidgetsTimeLineView(
                this,
                timeLineViewModel,
                it.sdk
            )
            container_view.addView(timeLineView)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WidgetViewModel::class.java)
        // TODO: Use the ViewModel
    }

}