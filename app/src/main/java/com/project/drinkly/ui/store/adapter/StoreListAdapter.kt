package com.project.drinkly.ui.store.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly.R
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.databinding.RowStoreListBinding
import com.project.drinkly.ui.MainActivity

class StoreListAdapter(
    private var activity: MainActivity,
    private var stores: List<StoreListResponse>
) :
    RecyclerView.Adapter<StoreListAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newStores: List<StoreListResponse>) {
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
        holder.storeName.text = stores[position].storeName
        holder.isOpen.text = stores[position].isOpen
        holder.closeOrOpenTime.text = stores[position].openingInfo
        holder.storeCall.text = stores[position].storeTel
        holder.avaiableDrink.text = stores[position].availableDrinks?.joinToString(",")

        if(stores[position].isAvailable == true) {
            holder.layoutStoreUnavailable.visibility = View.INVISIBLE
        } else {
            holder.layoutStoreUnavailable.visibility = View.VISIBLE
        }

        if(stores[position].storeMainImageUrl.isNullOrEmpty()) {
            holder.storeImage.setImageResource(R.drawable.img_store_main_basic)
        } else {
            Glide.with(activity).load(stores[position].storeMainImageUrl).into(holder.storeImage)
        }
    }

    override fun getItemCount() = stores.size


    inner class ViewHolder(val binding: RowStoreListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val storeName = binding.textViewStoreName
        val isOpen = binding.textViewStoreIsOpen
        val closeOrOpenTime = binding.textViewStoreCloseOrOpenTime
        val storeCall = binding.textViewStoreCall
        val avaiableDrink = binding.textViewStoreAvailableDrink
        val storeImage = binding.imageViewStore
        val layoutStoreUnavailable = binding.layoutStoreUnavailable

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