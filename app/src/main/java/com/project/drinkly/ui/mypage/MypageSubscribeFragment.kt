package com.project.drinkly.ui.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.internal.zaac
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentMypageSubscribeBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.payment.PaymentManageFragment
import com.project.drinkly.ui.payment.SubscribePaymentFragment
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

class MypageSubscribeFragment : Fragment() {

    lateinit var binding: FragmentMypageSubscribeBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageSubscribeBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            // 멤버십 구독 관리
            layoutButtonSubscribe.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, SubscribePaymentFragment())
                    .addToBackStack(null)
                    .commit()
            }

            // 결제 수단 관리
            layoutButtonPayment.setOnClickListener {
                // 구독권 결제 수단 관리 화면으로 이동
                val bundle = Bundle().apply { putBoolean("payment", false) }

                var nextFragment = PaymentManageFragment().apply {
                    arguments = bundle
                }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, nextFragment)
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

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)

            updateSubscriptionStatusIfNeeded(activity = mainActivity) { success ->
                if (success) {
                    // 구독 상태가 오늘 날짜 기준으로 정상 체크됨 → 이후 로직 실행
                    Log.d("SubscriptionCheck", "✅ 상태 확인 완료 후 이어서 작업 실행")

                } else {
                    Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                    mainActivity.goToLogin()
                }
            }
        }

        binding.run {
            toolbar.run {
                textViewHead.text = "구독 관리"
                buttonBack.setOnClickListener {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

}