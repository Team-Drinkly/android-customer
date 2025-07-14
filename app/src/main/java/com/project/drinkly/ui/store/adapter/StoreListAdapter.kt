package com.project.drinkly.ui.store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly.R
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.databinding.RowStoreListBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.util.MainUtil.formatDistance

class StoreListAdapter(
    private var activity: MainActivity,
    private var stores: MutableList<StoreListResponse>?
) :
    RecyclerView.Adapter<StoreListAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newStores: MutableList<StoreListResponse>?) {
        stores = newStores
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            RowStoreListBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewStoreName.text = stores?.get(position)?.storeName
            textViewStoreIsOpen.text = stores?.get(position)?.isOpen
            textViewStoreCloseOrOpenTime.text = stores?.get(position)?.openingInfo
            textViewStoreCall.text = stores?.get(position)?.storeTel
            textViewStoreAvailableDrink.text = stores?.get(position)?.availableDrinks?.joinToString(",")
            val isNotificationAllowed = NotificationManagerCompat.from(activity).areNotificationsEnabled()
            if(isNotificationAllowed) {
                textViewDistance.visibility = View.VISIBLE
                textViewDistance.text = formatDistance(stores?.get(position)?.distance ?: 0.0)
            } else {
                textViewDistance.visibility = View.GONE
            }

            if(stores?.get(position)?.isAvailable == true) {
                layoutStoreUnavailable.visibility = View.INVISIBLE
            } else {
                layoutStoreUnavailable.visibility = View.VISIBLE
            }

            if(stores?.get(position)?.storeMainImageUrl.isNullOrEmpty()) {
                imageViewStore.setImageResource(R.drawable.img_store_main_basic)
            } else {
                Glide.with(activity).load(stores?.get(position)?.storeMainImageUrl).into(imageViewStore)
            }
        }
    }

    override fun getItemCount() = stores?.size ?: 0


    inner class ViewHolder(val binding: RowStoreListBinding) :
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