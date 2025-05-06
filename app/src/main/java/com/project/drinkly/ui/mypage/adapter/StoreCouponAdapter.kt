package com.project.drinkly.ui.mypage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.api.response.coupon.MembershipCouponListResponse
import com.project.drinkly.api.response.coupon.StoreCouponListResponse
import com.project.drinkly.databinding.LayoutStoreCouponBinding
import com.project.drinkly.ui.MainActivity

class StoreCouponAdapter(
    private var activity: MainActivity,
    private var coupons: List<StoreCouponListResponse>,
    private var isUsed: Boolean
) :
    RecyclerView.Adapter<StoreCouponAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newCoupons: List<StoreCouponListResponse>) {
        coupons = newCoupons
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int) {}
    }

    var itemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding =
            LayoutStoreCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            textViewStoreName.text = coupons[position].storeName
            textViewCouponTitle.text = coupons[position].title
            textViewCouponDescription.text = coupons[position].description
            textViewCouponDate.text = "유효기간: ${coupons[position].expirationDate}까지"

            if(isUsed) {
                textViewCouponDownload.text = "사용\n완료"
            } else {
                textViewCouponDownload.text = "쿠폰\n사용"
            }
        }
    }

    override fun getItemCount() = coupons.size


    inner class ViewHolder(val binding: LayoutStoreCouponBinding) :
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