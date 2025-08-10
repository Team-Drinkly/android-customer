package com.project.drinkly.ui.store

import android.app.Activity
import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.drinkly.R
import com.project.drinkly.api.response.coupon.StoreCouponResponse
import com.project.drinkly.databinding.FragmentCouponListBottomSheetBinding
import com.project.drinkly.ui.BasicToast
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.adapter.StoreDetailCouponAdapter
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

class CouponListBottomSheetFragment(var activity: MainActivity, var coupons: MutableList<StoreCouponResponse>?, var storeId: Long, var storeName: String) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentCouponListBottomSheetBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(requireActivity())[StoreViewModel::class.java]
    }

    lateinit var storeCouponAdapter: StoreDetailCouponAdapter

    private var couponInfo: MutableList<StoreCouponResponse>? = null

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
        observeViewModel()

        binding.run {
            recyclerViewCoupon.apply {
                adapter = storeCouponAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            buttonDownloadAll.setOnClickListener {
                mixpanel.track("click_detail_coupon_list_download_all", null)

                // 쿠폰 전체 다운로드
                viewModel.downloadAllCoupon(activity, storeId) {
                    viewModel.getStoreCoupon(activity, storeId)
                    BasicToast.showBasicToast(requireContext(), "쿠폰 다운로드가 완료되었어요", R.drawable.ic_check, binding.buttonDownloadAll)
                }
            }

            buttonClose.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)) as View
            val behavior = BottomSheetBehavior.from(bottomSheet)

            bottomSheet.layoutParams.height = getHalfScreenHeight()
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            behavior.peekHeight = getHalfScreenHeight()
        }

        return dialog
    }

    private fun getHalfScreenHeight(): Int {
        val displayMetrics = Resources.getSystem().displayMetrics
        return (displayMetrics.heightPixels * 0.7).toInt()
    }


    fun initAdapter() {
        storeCouponAdapter = StoreDetailCouponAdapter(
            mainActivity,
            coupons
        ).apply {
            itemClickListener = object : StoreDetailCouponAdapter.OnItemClickListener {
                override fun onItemClick(position: Int, type: String) {
                    useCoupon(position, type)
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            // 쿠폰 데이터
            storeCouponInfo.observe(viewLifecycleOwner) {
                couponInfo = it

                println("쿠폰 데이터 업데이트 ${couponInfo}")
                storeCouponAdapter.updateList(couponInfo)
            }
        }
    }

    fun useCoupon(position: Int, status: String) {
        val coupon = couponInfo?.getOrNull(position) ?: return

        when (status) {
            "NONE" -> {
                mixpanel.track("click_detail_coupon_download", null)

                // (쿠폰 다운로드 전) 쿠폰 다운로드
                viewModel.downloadCoupon(
                    mainActivity,
                    coupon.id,
                    storeId
                ) {
                    BasicToast.showBasicToast(requireContext(), "쿠폰 다운로드가 완료되었어요", R.drawable.ic_check, binding.buttonDownloadAll)
                }
            }

            "AVAILABLE" -> {
                mixpanel.track("move_detail_to_coupon_use", null)

                // (쿠폰 다운로드 후 사용 X) 쿠폰 사용 화면으로 이동
                val bundle = Bundle().apply {
                    putString("storeName", storeName.toString())
                    putLong("couponId",  coupon.id)
                    putString("couponTitle",  coupon.title)
                    putString("couponDescription", coupon.description)
                    putString("couponDate", coupon.expirationDate)
                }

                // 전달할 Fragment 생성
                var nextFragment = StoreCouponFragment().apply {
                    arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, nextFragment)
                    .addToBackStack(null)
                    .commit()
            }

            "USED" -> {
                // (쿠폰 사용 완료)
            }
        }
    }
}
