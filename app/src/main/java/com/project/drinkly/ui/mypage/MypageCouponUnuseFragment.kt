package com.project.drinkly.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.R
import com.project.drinkly.api.response.coupon.CouponListResponse
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.databinding.FragmentMypageCouponUnuseBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.mypage.adapter.CouponAdapter
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.ui.store.StoreDetailFragment
import com.project.drinkly.ui.store.adapter.StoreListAdapter
import com.project.drinkly.util.MyApplication

class MypageCouponUnuseFragment : Fragment() {

    lateinit var binding: FragmentMypageCouponUnuseBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: MypageViewModel

    lateinit var couponListAdapter: CouponAdapter
    var getCouponList = mutableListOf<CouponListResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageCouponUnuseBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[MypageViewModel::class.java]

        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewCoupon.apply {
                adapter = couponListAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        initView()
        viewModel.getAvailableCouponList(mainActivity)
    }

    fun initAdapter() {
        couponListAdapter = CouponAdapter(
            mainActivity,
            getCouponList
        ).apply {
            itemClickListener = object : CouponAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 쿠폰 사용
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            availableCouponInfo.observe(viewLifecycleOwner) {
                getCouponList = it

                binding.textViewCouponNumber.text = "${getCouponList.size}개"

                couponListAdapter.updateList(getCouponList)
            }
        }
    }

    fun initView() {
        binding.run {
            textViewNickname.text = "${MyApplication.userNickName}님"
        }
    }

}