package com.project.drinkly.ui.event.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly.R
import com.project.drinkly.api.response.event.DrinklyEventResponse
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.databinding.LayoutDrinklyEventBinding
import com.project.drinkly.ui.MainActivity

class DrinklyEventListAdapter(
    private var activity: MainActivity,
    private var events: MutableList<DrinklyEventResponse>?
) :
    RecyclerView.Adapter<DrinklyEventListAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newEvents: MutableList<DrinklyEventResponse>?) {
        events = newEvents
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            LayoutDrinklyEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            Glide.with(activity).load(events?.get(position)?.thumbnailPath).into(imageViewEvent)
            textViewEventName.text = events?.get(position)?.title
            textViewTagType.text = events?.get(position)?.status
            textViewTagDay.text = events?.get(position)?.dDay
            when(events?.get(position)?.status) {
                "진행중" -> {
                    textViewTagType.run {
                        setTextColor(resources.getColor(R.color.white))
                        setBackgroundResource(R.drawable.background_primary50_radius6)
                    }
                    textViewTagDay.run {
                        setTextColor(resources.getColor(R.color.primary_50))
                    }
                    layoutExpired.visibility = View.GONE
                }
                "마감" -> {
                    textViewTagType.run {
                        setTextColor(resources.getColor(R.color.gray6))
                        setBackgroundResource(R.drawable.background_gray8_radius6)
                    }
                    textViewTagDay.run {
                        setTextColor(resources.getColor(R.color.gray7))
                    }
                    layoutExpired.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemCount() = events?.size ?: 0


    inner class ViewHolder(val binding: LayoutDrinklyEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition)

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)

                true
            }
        }
    }
}