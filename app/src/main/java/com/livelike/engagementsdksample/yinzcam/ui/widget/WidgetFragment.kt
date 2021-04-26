package com.livelike.engagementsdksample.yinzcam.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonParser
import com.livelike.engagementsdk.LiveLikeEngagementTheme
import com.livelike.engagementsdk.core.services.network.Result
import com.livelike.engagementsdk.widget.timeline.WidgetTimeLineViewModel
import com.livelike.engagementsdk.widget.timeline.WidgetsTimeLineView
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.yinzcam.YinzCamActivity
import kotlinx.android.synthetic.main.widget_fragment.*
import java.io.IOException
import java.io.InputStream

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
                it,
                timeLineViewModel,
                it.sdk
            )
            try {
                val inputStream: InputStream = it.assets.open("livelikeStyles.json")
                val size: Int = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                val theme = String(buffer)
                val result =
                    LiveLikeEngagementTheme.instanceFrom(JsonParser.parseString(theme).asJsonObject)
                if (result is Result.Success) {
                    timeLineView.applyTheme(result.data)
                } else {
                    Toast.makeText(
                        it,
                        "Unable to get the theme json",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            container_view.addView(timeLineView)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(WidgetViewModel::class.java)
        // TODO: Use the ViewModel
    }

}