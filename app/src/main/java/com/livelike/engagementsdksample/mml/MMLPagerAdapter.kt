package com.livelike.engagementsdksample.mml

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.livelike.engagementsdksample.R
import com.mml.mmlengagementsdk.LiveLikeSDKIntegrationManager

class MMLPagerAdapter(
    val context: Context,
    private val liveLikeSDKIntegrationManager: LiveLikeSDKIntegrationManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    private val widgetView = liveLikeSDKIntegrationManager.getWidgetsView(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            2 -> MMLChatViewHolder(context, liveLikeSDKIntegrationManager)
            3 -> MMLTimeLineViewHolder(context, liveLikeSDKIntegrationManager)
            else -> DefaultViewHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.mml_empty_chat_data_view, parent, false)
            )
        }
    }

    override fun getItemCount(): Int = 4

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }
}

class DefaultViewHolder(view: View) : RecyclerView.ViewHolder(view)
class MMLChatViewHolder(
    context: Context,
    liveLikeSDKIntegrationManager: LiveLikeSDKIntegrationManager
) : RecyclerView.ViewHolder(liveLikeSDKIntegrationManager.getChatView(context).apply {
    this.layoutParams = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.MATCH_PARENT,
        ConstraintLayout.LayoutParams.MATCH_PARENT
    )
})

class MMLTimeLineViewHolder(
    context: Context,
    liveLikeSDKIntegrationManager: LiveLikeSDKIntegrationManager
) : RecyclerView.ViewHolder(liveLikeSDKIntegrationManager.getWidgetsView(context).apply {
    this.layoutParams = ConstraintLayout.LayoutParams(
        ConstraintLayout.LayoutParams.MATCH_PARENT,
        ConstraintLayout.LayoutParams.MATCH_PARENT
    )
})