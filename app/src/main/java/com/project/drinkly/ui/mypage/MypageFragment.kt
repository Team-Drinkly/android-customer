package com.project.drinkly.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentMypageBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.ui.subscribe.SubscribePaymentFragment
import com.project.drinkly.util.MyApplication

class MypageFragment : Fragment() {

    lateinit var binding: FragmentMypageBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonMembershipPayment.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, SubscribePaymentFragment())
                    .addToBackStack(null)
                    .commit()
            }

            // 구독 관리
            layoutButtonSubscribe.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, SubscribePaymentFragment())
                    .addToBackStack(null)
                    .commit()
            }
            // 내 쿠폰함
            layoutButtonCoupon.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, MypageCouponFragment())
                    .addToBackStack(null)
                    .commit()
            }
            // 계정 관리
            layoutButtonAccount.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, MypageAccountFragment())
                    .addToBackStack(null)
                    .commit()
            }
            // 고객센터
            layoutButtonQna.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, MypageQnaFragment())
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
            hideBottomNavigation(false)
            hideMapButton(true)
            hideMyLocationButton(true)
        }

        binding.run {
            if(MyApplication.isSubscribe) {
                buttonMembershipPayment.visibility = View.GONE
                layoutSubscribe.visibility = View.VISIBLE
            } else {
                buttonMembershipPayment.visibility = View.VISIBLE
                layoutSubscribe.visibility = View.GONE
            }

            textViewNickname.text = "${MyApplication.userNickName}님"

            toolbar.run {
                textViewTitle.text = "마이페이지"
            }
        }
    }
}