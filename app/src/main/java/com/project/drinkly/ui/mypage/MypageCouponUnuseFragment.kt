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
import com.project.drinkly.api.InfoManager
import com.project.drinkly.api.response.coupon.MembershipCouponListResponse
import com.project.drinkly.api.response.coupon.StoreCouponListResponse
import com.project.drinkly.databinding.FragmentMypageCouponUnuseBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicButtonDialogInterface
import com.project.drinkly.ui.dialog.DialogBasicButton
import com.project.drinkly.ui.mypage.adapter.MembershipCouponAdapter
import com.project.drinkly.ui.mypage.adapter.StoreCouponAdapter
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.ui.store.StoreCouponFragment
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel
import com.project.drinkly.util.MyApplication

class MypageCouponUnuseFragment : Fragment() {

    lateinit var binding: FragmentMypageCouponUnuseBinding
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

        binding = FragmentMypageCouponUnuseBinding.inflate(layoutInflater)
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
        ).apply {
            itemClickListener = object : MembershipCouponAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    mixpanel.track("click_coupon_subscribe", null)

                    val dialog = DialogBasicButton("구독권 쿠폰을 사용하시겠습니까?", "취소", "사용하기", R.color.primary_50)

                    dialog.setBasicDialogInterface(object : BasicButtonDialogInterface {
                        override fun onClickYesButton() {
                            mixpanel.track("click_coupon_subscribe_confirm", null)

                            // 쿠폰 사용
                            viewModel.useMembershipCoupon(mainActivity, getMembershipCouponList[position].id)
                        }
                    })

                    dialog.show(mainActivity.supportFragmentManager, "DialogCoupon")
                }
            }
        }

        storeCouponListAdapter = StoreCouponAdapter(
            mainActivity,
            getStoreCouponList,
            false
        ).apply {
            itemClickListener = object : StoreCouponAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    mixpanel.track("click_coupon_select", null)

                    // 쿠폰 사용 화면
                    val bundle = Bundle().apply {
                        putString("storeName", getStoreCouponList[position].storeName.toString())
                        putLong("couponId", getStoreCouponList[position].id ?: 0)
                        putString("couponTitle", getStoreCouponList[position].title)
                        putString("couponDescription", getStoreCouponList[position].description)
                        putString("couponDate", getStoreCouponList[position].expirationDate)
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
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            availableMembershipCouponInfo.observe(viewLifecycleOwner) {
                getMembershipCouponList = it

                var couponSize = getMembershipCouponList.size + getStoreCouponList.size
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

            availableStoreCouponInfo.observe(viewLifecycleOwner) {
                getStoreCouponList = it

                var couponSize = getMembershipCouponList.size + getStoreCouponList.size
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
        viewModel.getAvailableMembershipCouponList(mainActivity)
        viewModel.getAvailableStoreCouponList(mainActivity)

        binding.run {
            textViewNickname.text = "${InfoManager(mainActivity).getUserNickname()}님"
        }
    }

}