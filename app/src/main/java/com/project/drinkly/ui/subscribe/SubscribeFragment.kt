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
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.ui.subscribe.viewModel.SubscribeViewModel
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
            buttonSubscribeMembership.setOnClickListener {
                // 구독권 결제 화면으로 이동
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, SubscribePaymentFragment())
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

    fun showToolTip() {
        val balloon = Balloon.Builder(mainActivity)
                .setWidth(BalloonSizeSpec.WRAP)
//            .setWidthRatio(0.6f) // sets width as 60% of the horizontal screen's size.
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("멤버십 사용 내역을 확인할 수 있어요!")
            .setTextColorResource(R.color.gray9)
            .setTextSize(14f)
            .setTextTypeface(ResourcesCompat.getFont(mainActivity,R.font.pretendard_medium)!!)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowOrientation(ArrowOrientation.END)
            .setArrowSize(5)
            .setArrowPosition(0.5f)
            .setArrowColorResource(R.color.gray3)
            .setTextGravity(Gravity.CENTER)
            .setElevation(0)
            .setPaddingHorizontal(12)
            .setPaddingVertical(8)
            .setMarginHorizontal(5)
            .setCornerRadius(8f)
            .setBackgroundDrawableResource(R.drawable.background_tooltip_gray3)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .build()

        mainActivity.binding.buttonOrderHistory.showAlignStart(balloon)

        Handler().postDelayed({
            balloon.dismiss()
        }, 3000)
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(false)
            hideMapButton(true)
            hideMyLocationButton(true)
            if(InfoManager(mainActivity).getIsSubscribe() == true) {
                hideOrderHistoryButton(false)
                showToolTip()

                viewModel.getOrderHistory(mainActivity)
            } else {
                hideOrderHistoryButton(true)
            }

            binding.buttonOrderHistory.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, OrderHistoryFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        binding.run {

            var infoManager = InfoManager(mainActivity)
            textViewNickname.text = "${infoManager.getUserNickname()}"
            if(infoManager.getIsSubscribe() == true) {
                // 멤버십 사용 내역 조회

                layoutMembershipButton.visibility = View.GONE

                textViewExpirationDday.text = "D-${infoManager.getSubscribeLeftDays()}"
                textViewExpirationDay.text = "${infoManager.getExpiredDate()} 만료"
            } else {
                layoutSubscribe.visibility = View.GONE
                layoutMembershipInfo.visibility = View.GONE
            }

            toolbar.run {
                textViewTitle.text = "구독"
            }
        }
    }
}