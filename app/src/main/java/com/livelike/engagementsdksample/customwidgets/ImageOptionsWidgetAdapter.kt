package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.widget.model.LiveLikeWidgetOption
import kotlinx.android.synthetic.main.image_option_list_item.view.*
import kotlinx.android.synthetic.main.image_option_list_item.view.option_tv
import kotlinx.android.synthetic.main.image_option_list_item.view.result_bar
import kotlinx.android.synthetic.main.image_option_list_item.view.result_tv
import kotlinx.android.synthetic.main.text_option_list_item.view.*
import kotlin.math.max

class ImageOptionsWidgetAdapter(
    private val context: Context,
    var isImage: Boolean,
    var list: ArrayList<LiveLikeWidgetOption>,
    val optionSelectListener: (LiveLikeWidgetOption) -> Unit
) :
    RecyclerView.Adapter<ImageOptionsWidgetAdapter.ImageOptionsListItemViewHolder>() {


    var isResultState: Boolean = false
    var isResultAvailable: Boolean = false

    /**
     * flag to tell whether to use red-green bar color to indicate right and wrong answers
     *  or otherwise just blue - grey to indicate selected and unselected answer
     */
    var indicateRightAnswer: Boolean = true

    var selectedOptionItem: LiveLikeWidgetOption? = null
    var currentlySelectedViewHolder: ImageOptionsListItemViewHolder? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageOptionsWidgetAdapter.ImageOptionsListItemViewHolder {
        return ImageOptionsListItemViewHolder(
            LayoutInflater.from(context).inflate(
                when (isImage) {
                    true -> R.layout.image_option_list_item
                    else -> R.layout.text_option_list_item
                },
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(
        holder: ImageOptionsWidgetAdapter.ImageOptionsListItemViewHolder,
        position: Int
    ) {
        val liveLikeWidgetOption = list[position]
        holder.view.option_tv.text = liveLikeWidgetOption.description
        if (isImage)
            Glide.with(context).load(liveLikeWidgetOption.imageUrl).into(holder.view.option_iv)

        if (isResultState && isResultAvailable) {
            holder.view.result_bar.visibility = View.VISIBLE
            holder.view.result_tv.visibility = View.VISIBLE
            holder.view.setOnClickListener(null)
            holder.view.result_tv.text = "${liveLikeWidgetOption.percentage ?: 0}%"
            holder.view.result_bar.pivotX = 0f
            holder.view.result_bar.scaleX =
                max(((liveLikeWidgetOption.percentage ?: 0) / 100f), 0.1f)

            if (!indicateRightAnswer) {
                if (selectedOptionItem?.id == liveLikeWidgetOption.id) {
                    holder.view.result_bar.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.selected_result_bar_color
                        )
                    )
                } else {
                    holder.view.result_bar.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.default_result_bar_color
                        )
                    )
                }
            } else {
                if (selectedOptionItem?.id == liveLikeWidgetOption.id && !liveLikeWidgetOption.isCorrect) {
                    holder.view.result_bar.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.incorrect_result_bar_color
                        )
                    )
                } else if (liveLikeWidgetOption.isCorrect) {
                    holder.view.result_bar.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.correct_result_bar_color
                        )
                    )
                } else {
                    holder.view.result_bar.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.default_result_bar_color
                        )
                    )
                }
            }
            if (selectedOptionItem?.id == liveLikeWidgetOption.id) {
                holder.selectOption()
            } else {
                holder.unSelectOption()
            }
            holder.view.setOnClickListener(null)
        } else {
            holder.view.result_bar.visibility = View.GONE
            holder.view.result_tv.visibility = View.GONE
            holder.view.setOnClickListener {
                currentlySelectedViewHolder?.unSelectOption()
                currentlySelectedViewHolder = holder
                holder.selectOption()
                selectedOptionItem = liveLikeWidgetOption
                optionSelectListener.invoke(liveLikeWidgetOption)
            }
        }

    }


    inner class ImageOptionsListItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun selectOption() {
            view.setBackgroundResource(R.drawable.image_option_background_selected_drawable)
            if (isImage) {
                view.option_tv.setTextColor(Color.WHITE)
                view.result_tv.setTextColor(Color.WHITE)
            } else {
                view.option_tv.setTextColor(Color.WHITE)
                view.result_tv.setTextColor(Color.WHITE)
            }
        }

        fun unSelectOption() {
            view.setBackgroundResource(R.drawable.image_option_background_stroke_drawable)
            if (isImage) {
                view.option_tv.setTextColor(Color.BLACK)
                view.result_tv.setTextColor(Color.BLACK)
            } else {
                view.option_tv.setTextColor(Color.BLACK)
                view.result_tv.setTextColor(Color.BLACK)
            }
        }
    }

}