package com.project.drinkly.ui.store.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly.databinding.RowStoreImageBinding
import com.project.drinkly.databinding.RowStoreMenuImageBinding

class StoreMenuImageAdapter(
    private var activity: Activity,
    private var images: List<String>?
) :
    RecyclerView.Adapter<StoreMenuImageAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newImages: List<String>?) {
        images = newImages
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowStoreMenuImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(activity).load(images?.get(position))
            .into(holder.storeMenuImage)
    }

    override fun getItemCount() = images?.size ?: 0

    inner class ViewHolder(val binding: RowStoreMenuImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val storeMenuImage = binding.imageViewStoreMenu

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