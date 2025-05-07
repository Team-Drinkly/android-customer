package com.project.drinkly.ui.subscribe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.api.response.subscribe.OrderHistory
import com.project.drinkly.databinding.RowOrderHistoryBinding
import com.project.drinkly.ui.MainActivity

class OrderHistoryAdapter(
    private var activity: MainActivity,
    private var histories: List<OrderHistory>
) :
    RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newHistories: List<OrderHistory>) {
        histories = newHistories
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewDrinksName.text = histories[position].providedDrink
            textViewStoreName.text = histories[position].storeName
            textViewDate.text = histories[position].usageDate
        }
    }

    override fun getItemCount() = histories.size


    inner class ViewHolder(val binding: RowOrderHistoryBinding) :
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