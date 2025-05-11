package com.project.drinkly.ui.store.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly.R
import com.project.drinkly.api.response.store.StoreImageInfo
import com.project.drinkly.databinding.RowStoreAvaiableDrinkBinding
import com.project.drinkly.databinding.RowStoreAvaiableDrinkSelectBinding
import com.project.drinkly.databinding.RowStoreImageBinding

class StoreAvailableDrinkSelectAdapter(
    private var activity: Activity,
    private var avaiableDrinks: List<StoreImageInfo>?
) :
    RecyclerView.Adapter<StoreAvailableDrinkSelectAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newAvaiableDrinks: List<StoreImageInfo>?) {
        avaiableDrinks = newAvaiableDrinks
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowStoreAvaiableDrinkSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            Glide.with(activity).load(avaiableDrinks?.get(position)?.imageUrl)
                .into(imageViewAvaiableDrink)
            textViewAvaiableDrink.text = avaiableDrinks?.get(position)?.description

            // 선택된 아이템의 배경 변경
            if (position == selectedPosition) {
                backgroundAvaiableDrink.setBackgroundResource(R.drawable.background_gray5_top_radius10)
                textViewAvaiableDrink.setBackgroundResource(R.drawable.background_primary50_bottom_radius10)
            } else {
                backgroundAvaiableDrink.setBackgroundResource(R.drawable.background_gray6_top_radius10)
                textViewAvaiableDrink.setBackgroundResource(R.drawable.background_card_bottom_radius10)
            }
        }

        // 클릭 이벤트
        holder.itemView.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = position

            // 이전 선택된 아이템과 현재 아이템을 다시 그림
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)

            itemClickListener?.onItemClick(selectedPosition)

            onItemClickListener?.invoke(position) // 클릭 리스너 호출
        }
    }

    override fun getItemCount() = avaiableDrinks?.size ?: 0

    inner class ViewHolder(val binding: RowStoreAvaiableDrinkSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}