package com.project.drinkly.ui.store

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.drinkly.api.response.coupon.StoreCouponResponse
import com.project.drinkly.databinding.FragmentCouponListBottomSheetBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.adapter.StoreDetailCouponAdapter

class CouponListBottomSheetFragment(var activity: Activity, var coupons: MutableList<StoreCouponResponse>?) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentCouponListBottomSheetBinding
    lateinit var mainActivity: MainActivity

    lateinit var storeCouponAdapter: StoreDetailCouponAdapter

    interface OnCouponClickListener {
        fun onBottomSheetClicked(position: Int, type: String)
    }

    var clickListener: OnCouponClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCouponListBottomSheetBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        binding.run {
            recyclerViewCoupon.apply {
                adapter = storeCouponAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            buttonClose.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    fun initAdapter() {
        storeCouponAdapter = StoreDetailCouponAdapter(
            mainActivity,
            coupons
        ).apply {
            itemClickListener = object : StoreDetailCouponAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, type: String) {
                    clickListener?.onBottomSheetClicked(position, type)
                }
            }
        }
    }

    fun updateCouponList(updatedList: List<StoreCouponResponse>?) {
        coupons?.clear()
        coupons?.addAll(updatedList ?: emptyList())
        storeCouponAdapter.updateList(coupons)
        storeCouponAdapter.notifyDataSetChanged()
    }
}
