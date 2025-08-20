package com.project.drinkly.ui.event.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly.api.response.event.GeneralEventResponse
import com.project.drinkly.databinding.RowGeneralEventBinding
import com.project.drinkly.databinding.RowMainGeneralEventBinding
import com.project.drinkly.ui.MainActivity

class MainGeneralEventListAdapter(
    private var activity: MainActivity,
    private var events: MutableList<GeneralEventResponse>?
) :
    RecyclerView.Adapter<MainGeneralEventListAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newEvents: MutableList<GeneralEventResponse>?) {
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
            RowMainGeneralEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            Glide.with(activity).load(events?.get(position)?.thumbnailPath).into(imageViewEvent)
            textViewEventCompany.text = events?.get(position)?.organizer
            textViewEventTitle.text = events?.get(position)?.title
            textViewEventDescription.text = events?.get(position)?.description
        }
    }

    override fun getItemCount() = if ((events?.size ?: 0) <= 3) events?.size ?: 0 else 3


    inner class ViewHolder(val binding: RowMainGeneralEventBinding) :
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