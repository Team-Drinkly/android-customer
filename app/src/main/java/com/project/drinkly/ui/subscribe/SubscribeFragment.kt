package com.project.drinkly.ui.subscribe

import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.api.InfoManager
import com.project.drinkly.api.response.subscribe.OrderHistory
import com.project.drinkly.databinding.FragmentSubscribeBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.mypage.MypageWithdrawalFragment
import com.project.drinkly.ui.payment.SubscribePaymentFragment
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.ui.subscribe.viewModel.SubscribeViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel
import com.project.drinkly.util.MyApplication
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignStart

class SubscribeFragment : Fragment() {

    lateinit var binding: FragmentSubscribeBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: SubscribeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSubscribeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(requireActivity())[SubscribeViewModel::class.java]

        observeViewModel()

        binding.run {
            layoutMembershipButton.setOnClickListener {
                mixpanel.track("click_subscribe_main", null)

                // 구독권 결제 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, SubscribePaymentFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonOrderHistory.setOnClickListener {
                mixpanel.track("click_subscribe_history", null)

                // 멤버십 사용 내역 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, OrderHistoryFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun observeViewModel() {
        viewModel.run {
            orderHistory.observe(viewLifecycleOwner) {
                binding.textViewMembershipUsedNumber.text = "${it?.usedCount ?: 0}회"
            }
        }
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(false)
            hideMapButton(true)
            hideMyLocationButton(true)
        }

        binding.run {

            var infoManager = InfoManager(mainActivity)
            textViewNickname.text = "${infoManager.getUserNickname()}"
            if(infoManager.getIsSubscribe() == true) {
                // 멤버십 사용 내역 조회
                viewModel.getOrderHistory(mainActivity)

                layoutMembershipButton.visibility = View.GONE

                textViewExpirationDday.text = "D-${infoManager.getSubscribeLeftDays()}"
                textViewExpirationDay.text = "${infoManager.getExpiredDate()} 만료"
            } else {
                layoutSubscribe.visibility = View.GONE
                layoutMembershipInfo.visibility = View.GONE
                buttonOrderHistory.visibility = View.GONE
            }

            toolbar.run {
                textViewHead.text = "구독"
            }
        }
    }
}