package com.project.drinkly.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.api.InfoManager
import com.project.drinkly.api.response.coupon.MembershipCouponListResponse
import com.project.drinkly.api.response.coupon.StoreCouponListResponse
import com.project.drinkly.databinding.FragmentMypageCouponUsedBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.mypage.adapter.MembershipCouponAdapter
import com.project.drinkly.ui.mypage.adapter.StoreCouponAdapter
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.util.MyApplication


class MypageCouponUsedFragment : Fragment() {

    lateinit var binding: FragmentMypageCouponUsedBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: MypageViewModel

    lateinit var membershipCouponListAdapter: MembershipCouponAdapter
    lateinit var storeCouponListAdapter: StoreCouponAdapter

    var getMembershipCouponList = mutableListOf<MembershipCouponListResponse>()
    var getStoreCouponList = mutableListOf<StoreCouponListResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageCouponUsedBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[MypageViewModel::class.java]

        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewMembershipCoupon.apply {
                adapter = membershipCouponListAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            recyclerViewStoreCoupon.apply {
                adapter = storeCouponListAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()

        initView()
    }

    fun initAdapter() {
        membershipCouponListAdapter = MembershipCouponAdapter(
            mainActivity,
            getMembershipCouponList
        )


        storeCouponListAdapter = StoreCouponAdapter(
            mainActivity,
            getStoreCouponList,
            true
        )
    }

    fun observeViewModel() {
        viewModel.run {
            usedMembershipCouponInfo.observe(viewLifecycleOwner) {
                getMembershipCouponList = it

                var couponSize = getMembershipCouponList.size + (getStoreCouponList.size ?: 0)
                binding.run {
                    textViewCouponNumber.text = "${couponSize}개"

                    if(couponSize == 0) {
                        layoutEmpty.visibility = View.VISIBLE
                        recyclerViewMembershipCoupon.visibility = View.GONE
                        recyclerViewStoreCoupon.visibility = View.GONE
                    } else {
                        layoutEmpty.visibility = View.GONE
                        recyclerViewMembershipCoupon.visibility = View.VISIBLE
                        recyclerViewStoreCoupon.visibility = View.VISIBLE
                    }
                }

                membershipCouponListAdapter.updateList(getMembershipCouponList)
            }

            usedStoreCouponInfo.observe(viewLifecycleOwner) {
                getStoreCouponList = it

                var couponSize = getMembershipCouponList.size + (getStoreCouponList.size ?: 0)
                binding.run {
                    textViewCouponNumber.text = "${couponSize}개"

                    if(couponSize == 0) {
                        layoutEmpty.visibility = View.VISIBLE
                        recyclerViewMembershipCoupon.visibility = View.GONE
                        recyclerViewStoreCoupon.visibility = View.GONE
                    } else {
                        layoutEmpty.visibility = View.GONE
                        recyclerViewMembershipCoupon.visibility = View.VISIBLE
                        recyclerViewStoreCoupon.visibility = View.VISIBLE
                    }
                }

                storeCouponListAdapter.updateList(getStoreCouponList)
            }
        }
    }

    fun initView() {

        viewModel.getUsedMembershipCouponList(mainActivity)
        viewModel.getUsedStoreCouponList(mainActivity)

        binding.run {
            textViewNickname.text = "${InfoManager(mainActivity).getUserNickname()}님"
        }
    }
}