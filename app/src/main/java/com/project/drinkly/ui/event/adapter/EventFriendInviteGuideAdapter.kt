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
import com.project.drinkly.databinding.RowEventDetailImageBinding
import com.project.drinkly.databinding.RowEventFriendInviteBinding
import com.project.drinkly.databinding.RowEventGuideBinding
import com.project.drinkly.ui.MainActivity

class EventFriendInviteGuideAdapter(
    private var activity: MainActivity,
    private var guides: List<String>?
) :
    RecyclerView.Adapter<EventFriendInviteGuideAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowEventGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewGuide.text = guides?.get(position)
        }
    }

    override fun getItemCount() = guides?.size ?: 0


    inner class ViewHolder(val binding: RowEventGuideBinding) :
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