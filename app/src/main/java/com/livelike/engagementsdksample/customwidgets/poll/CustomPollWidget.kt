package com.livelike.engagementsdksample.customwidgets.poll

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livelike.engagementsdk.OptionsItem
import com.livelike.engagementsdk.widget.widgetModel.PollWidgetModel
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.parseDuration
import kotlinx.android.synthetic.main.custom_poll_widget.view.*
import kotlinx.android.synthetic.main.poll_image_list_item.view.*
import kotlinx.android.synthetic.main.poll_text_list_item.view.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay


class CustomPollWidget : ConstraintLayout {
    var pollWidgetModel: PollWidgetModel? = null
    var isImage = false

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
        inflate(context, R.layout.custom_poll_widget, this@CustomPollWidget)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        pollWidgetModel?.widgetData?.let { liveLikeWidget ->
            txt_title.text = liveLikeWidget.question
            liveLikeWidget.options?.let {
                if (it.size > 2) {
                    rcyl_poll_list.layoutManager = GridLayoutManager(context, 2)
                }
                val adapter =
                    PollListAdapter(context, isImage, ArrayList(it.map { item -> item!! }))
                rcyl_poll_list.adapter = adapter
                adapter.pollListener = object : PollListAdapter.PollListener {
                    override fun onSelectOption(id: String) {
                        pollWidgetModel?.submitVote(id)
                    }
                }
                pollWidgetModel?.voteResults?.subscribe(this) { result ->
                    result?.choices?.let { options ->
                        options.forEach { op ->
                            adapter.optionIdCount[op.id] = op.vote_count ?: 0
                        }
                    }
                }
                val timeMillis = liveLikeWidget.timeout?.parseDuration() ?: 5000
                time_bar.startTimer(timeMillis)

                (context as AppCompatActivity).lifecycleScope.async {
                    delay(timeMillis)
                    adapter.notifyDataSetChanged()
                    delay(2000)
                    pollWidgetModel?.finish()
                }
            }
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pollWidgetModel?.voteResults?.unsubscribe(this)
    }
}

class PollListAdapter(
    private val context: Context,
    private val isImage: Boolean,
    private val list: ArrayList<OptionsItem>
) :
    RecyclerView.Adapter<PollListAdapter.PollListItemViewHolder>() {
    var selectedIndex = -1
    val optionIdCount: HashMap<String, Int> = hashMapOf()

    var isFollowUp = false

    var pollListener: PollListener? = null

    interface PollListener {
        fun onSelectOption(id: String)
    }

    class PollListItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): PollListItemViewHolder {
        return PollListItemViewHolder(
            LayoutInflater.from(p0.context!!).inflate(
                when (isImage) {
                    true -> R.layout.poll_image_list_item
                    else -> R.layout.poll_text_list_item
                }, p0, false
            )
        )
    }

    override fun onBindViewHolder(holder: PollListItemViewHolder, index: Int) {
        val item = list[index]
        if (isImage) {
            Glide.with(context)
                .load(item.imageUrl)
                .into(holder.itemView.imageView)
            if (optionIdCount.containsKey(item.id)) {
                holder.itemView.progressBar.visibility = View.VISIBLE
                holder.itemView.textView2.visibility = View.VISIBLE
                val total = optionIdCount.values.reduce { acc, i -> acc + i }
                val percent = (optionIdCount[item.id!!]!!.toFloat() / total.toFloat()) * 100
                holder.itemView.progressBar.progress = percent.toInt()
                holder.itemView.textView2.text = "$percent %"
            } else {
                holder.itemView.progressBar.visibility = View.INVISIBLE
                holder.itemView.textView2.visibility = View.GONE
            }
            holder.itemView.textView.text = "${item.description}"
            if (selectedIndex == index) {
                holder.itemView.lay_poll_img_option.setBackgroundResource(R.drawable.option_item_background_selected)
                holder.itemView.progressBar.progressDrawable = ContextCompat.getDrawable(
                    context,
                    R.drawable.custom_progress_color_options_selected
                )
                holder.itemView.textView2.setTextColor(Color.WHITE)
                holder.itemView.textView.setTextColor(Color.WHITE)
            } else {
                holder.itemView.lay_poll_img_option.setBackgroundResource(R.drawable.option_item_background)
                holder.itemView.progressBar.progressDrawable = ContextCompat.getDrawable(
                    context,
                    R.drawable.custom_progress_color_options_selected
                )
                holder.itemView.textView2.setTextColor(Color.BLACK)
                holder.itemView.textView.setTextColor(Color.BLACK)
            }
            holder.itemView.lay_poll_img_option.setOnClickListener {
                selectedIndex = holder.adapterPosition
                pollListener?.onSelectOption(item.id!!)
                notifyDataSetChanged()
            }
        } else {
            if (optionIdCount.containsKey(item.id)) {
                holder.itemView.txt_percent.visibility = View.VISIBLE
                holder.itemView.progressBar_text.visibility = View.VISIBLE
                val total = optionIdCount.values.reduce { acc, i -> acc + i }
                val percent = (optionIdCount[item.id!!]!!.toFloat() / total.toFloat()) * 100
                holder.itemView.txt_percent.text = "$percent %"
                holder.itemView.progressBar_text.progress = percent.toInt()
            } else {
                holder.itemView.txt_percent.visibility = View.INVISIBLE
                holder.itemView.progressBar_text.visibility = View.INVISIBLE
            }
            holder.itemView.text_poll_item.text = "${item.description}"
            if (selectedIndex == index) {
                holder.itemView.lay_poll_text_option.setBackgroundResource(R.drawable.option_item_background_selected)
                holder.itemView.text_poll_item.setTextColor(Color.WHITE)
                holder.itemView.txt_percent.setTextColor(Color.WHITE)
                holder.itemView.progressBar_text.progressDrawable = ContextCompat.getDrawable(
                    context,
                    R.drawable.custom_progress_color_options_selected
                )
            } else {
                holder.itemView.lay_poll_text_option.setBackgroundResource(R.drawable.option_item_background)
                holder.itemView.text_poll_item.setTextColor(Color.BLACK)
                holder.itemView.txt_percent.setTextColor(Color.BLACK)
                holder.itemView.progressBar_text.progressDrawable = ContextCompat.getDrawable(
                    context,
                    R.drawable.custom_progress_color_options
                )
            }
            holder.itemView.lay_poll_text_option.setOnClickListener {
                if (!isFollowUp) {
                    selectedIndex = holder.adapterPosition
                    pollListener?.onSelectOption(item.id!!)
                    notifyDataSetChanged()
                }
            }
        }

    }

    override fun getItemCount(): Int = list.size
}
