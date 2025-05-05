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
import com.project.drinkly.api.response.coupon.MembershipCouponListResponse
import com.project.drinkly.databinding.FragmentMypageCouponUnuseBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicButtonDialogInterface
import com.project.drinkly.ui.dialog.DialogBasicButton
import com.project.drinkly.ui.mypage.adapter.MembershipCouponAdapter
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.util.MyApplication

class MypageCouponUnuseFragment : Fragment() {

    lateinit var binding: FragmentMypageCouponUnuseBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: MypageViewModel

    lateinit var membershipCouponListAdapter: MembershipCouponAdapter

    var getMembershipCouponList = mutableListOf<MembershipCouponListResponse>()

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
                    val dialog = DialogBasicButton("구독권 쿠폰을 사용하시겠습니까?", "취소", "사용하기", R.color.primary_50)

                    dialog.setBasicDialogInterface(object : BasicButtonDialogInterface {
                        override fun onClickYesButton() {
                            // 쿠폰 사용
                            viewModel.useCoupon(mainActivity, getMembershipCouponList[position].id)
                        }
                    })

                    dialog.show(mainActivity.supportFragmentManager, "DialogLogout")
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            availableMembershipCouponInfo.observe(viewLifecycleOwner) {
                getMembershipCouponList = it

                binding.textViewCouponNumber.text = "${getMembershipCouponList.size + getStoreCouponList.size}개"

                membershipCouponListAdapter.updateList(getMembershipCouponList)


            }
        }
    }

    fun initView() {
        viewModel.getAvailableMembershipCouponList(mainActivity)
        binding.run {
            textViewNickname.text = "${MyApplication.userNickName}님"
        }
    }

}