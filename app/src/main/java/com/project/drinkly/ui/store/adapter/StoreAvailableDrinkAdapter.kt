package com.project.drinkly.ui.store.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly.api.response.store.StoreImageInfo
import com.project.drinkly.databinding.RowStoreAvaiableDrinkBinding
import com.project.drinkly.databinding.RowStoreImageBinding

class StoreAvailableDrinkAdapter(
    private var activity: Activity,
    private var avaiableDrinks: List<StoreImageInfo>?
) :
    RecyclerView.Adapter<StoreAvailableDrinkAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null

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
            RowStoreAvaiableDrinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(activity).load(avaiableDrinks?.get(position)?.imageUrl)
            .into(holder.availableDrinkImage)
        holder.avaiableDrinkName.text = avaiableDrinks?.get(position)?.description
    }

    override fun getItemCount() = avaiableDrinks?.size!!

    inner class ViewHolder(val binding: RowStoreAvaiableDrinkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val availableDrinkImage = binding.imageViewAvaiableDrink
        val avaiableDrinkName = binding.textViewAvaiableDrink
    }
}