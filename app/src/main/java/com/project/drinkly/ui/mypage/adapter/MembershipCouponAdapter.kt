package com.project.drinkly.ui.mypage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.api.response.coupon.MembershipCouponListResponse
import com.project.drinkly.databinding.LayoutMembershipCouponBinding
import com.project.drinkly.ui.MainActivity

class MembershipCouponAdapter(
    private var activity: MainActivity,
    private var coupons: List<MembershipCouponListResponse>
) :
    RecyclerView.Adapter<MembershipCouponAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newCoupons: List<MembershipCouponListResponse>) {
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
            LayoutMembershipCouponBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.couponTitle.text = coupons[position].title
        holder.couponDescription.text = coupons[position].description
        holder.couponExpiredDay.text = "유효기간: ${coupons[position].expirationDate}까지"
        if(coupons[position].used) {
            holder.isUsed.text = "사용\n완료"
        } else {
            holder.isUsed.text = "쿠폰\n사용"
        }
    }

    override fun getItemCount() = coupons.size


    inner class ViewHolder(val binding: LayoutMembershipCouponBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val couponTitle = binding.textViewCouponTitle
        val couponDescription = binding.textViewCouponDescription
        val couponExpiredDay = binding.textViewCouponDate
        val isUsed = binding.textViewCouponDownload

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