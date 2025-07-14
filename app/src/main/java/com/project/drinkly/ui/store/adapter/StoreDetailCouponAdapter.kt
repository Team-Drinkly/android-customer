package com.project.drinkly.ui.store.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.api.response.coupon.StoreCouponResponse
import com.project.drinkly.databinding.LayoutStoreCouponBinding
import com.project.drinkly.ui.MainActivity

class StoreDetailCouponAdapter(
    private var activity: MainActivity,
    private var coupons: List<StoreCouponResponse>?
) :
    RecyclerView.Adapter<StoreDetailCouponAdapter.ViewHolder>() {

    private var onItemClickListener: ((Int) -> Unit)? = null
    private var context: Context? = null
    private var selectedPosition: Int = 0

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateList(newCoupons: MutableList<StoreCouponResponse>?) {
        coupons = newCoupons
        notifyDataSetChanged()
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int, type: String) {}
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
            textViewStoreName.text = coupons?.get(position)?.storeName ?: ""
            textViewCouponTitle.text = coupons?.get(position)?.title ?: ""
            textViewCouponDescription.text = coupons?.get(position)?.description ?: ""
            textViewCouponDate.text = "유효기간: ${coupons?.get(position)?.expirationDate}까지"

            when(coupons?.get(position)?.status) {
                "NONE" -> {
                    textViewCouponDownload.visibility = View.INVISIBLE
                    imageViewCouponDownload.visibility = View.VISIBLE
                }
                // 쿠폰 다운로드 후 사용 X
                "AVAILABLE" -> {
                    textViewCouponDownload.text = "쿠폰\n사용"
                    textViewCouponDownload.visibility = View.VISIBLE
                    imageViewCouponDownload.visibility = View.INVISIBLE
                }
                // 쿠폰 사용 완료
                "USED" -> {
                    textViewCouponDownload.text = "사용\n완료"
                    textViewCouponDownload.visibility = View.VISIBLE
                    imageViewCouponDownload.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun getItemCount() = coupons?.size ?: 0


    inner class ViewHolder(val binding: LayoutStoreCouponBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(adapterPosition,
                    coupons?.get(adapterPosition)?.status.toString()
                )

                // 클릭 리스너 호출
                onItemClickListener?.invoke(position)

                true
            }
        }
    }
}