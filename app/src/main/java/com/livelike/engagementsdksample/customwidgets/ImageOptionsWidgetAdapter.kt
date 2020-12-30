package com.livelike.engagementsdksample.customwidgets

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.livelike.engagementsdksample.R
import com.livelike.engagementsdksample.widget.model.LiveLikeWidgetOption
import kotlinx.android.synthetic.main.image_option_list_item.view.*
import kotlin.math.max

class ImageOptionsWidgetAdapter (private val context: Context,
                                 var list: ArrayList<LiveLikeWidgetOption>,
                                 val optionSelectListener : (LiveLikeWidgetOption)->Unit
) :
RecyclerView.Adapter<ImageOptionsWidgetAdapter.ImageOptionsListItemViewHolder>() {


    var isResultState: Boolean = false
    var isResultAvailable: Boolean = false

    var selectedOptionItem : LiveLikeWidgetOption?=null
    var currentlySelectedViewHolder : ImageOptionsListItemViewHolder ? =null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImageOptionsWidgetAdapter.ImageOptionsListItemViewHolder {
        return ImageOptionsListItemViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.image_option_list_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: ImageOptionsWidgetAdapter.ImageOptionsListItemViewHolder, position: Int) {
        val liveLikeWidgetOption = list[position]
        holder.view.option_tv.text = liveLikeWidgetOption.description
           Glide.with(context).load(liveLikeWidgetOption.imageUrl).into(holder.view.option_iv)

        if(isResultState && isResultAvailable){
            holder.view.result_bar.visibility = View.VISIBLE
            holder.view.result_tv.visibility = View.VISIBLE
            holder.view.setOnClickListener(null)
            holder.view.result_tv.text = "${liveLikeWidgetOption.percentage}%"
            holder.view.result_bar.pivotX = 0f
            holder.view.result_bar.scaleX = max(((liveLikeWidgetOption.percentage?:0) / 100f), 0.1f)
            if(selectedOptionItem?.id == liveLikeWidgetOption.id && !liveLikeWidgetOption.isCorrect ){
                holder.view.result_bar.setBackgroundColor(context.getColor(R.color.quiz_incorrect_result_bar_color))
            }else if(liveLikeWidgetOption.isCorrect){
                holder.view.result_bar.setBackgroundColor(context.getColor(R.color.quiz_correct_result_bar_color))
            }else{
                holder.view.result_bar.setBackgroundColor(context.getColor(R.color.quiz_default_result_bar_color))
            }
        }else{
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



   inner class ImageOptionsListItemViewHolder(val view: View) : RecyclerView.ViewHolder(view){

       fun selectOption(){
           view.option_tv.setTextColor(context.getColor(android.R.color.white))
           view.result_tv.setTextColor(context.getColor(android.R.color.white))
           view.setBackgroundResource(R.drawable.image_option_background_selected_drawable)
       }

       fun unSelectOption(){
           view.option_tv.setTextColor(context.getColor(android.R.color.black))
           view.result_tv.setTextColor(context.getColor(android.R.color.black))
           view.setBackgroundResource(R.drawable.image_option_background_stroke_drawable)

       }
   }

}