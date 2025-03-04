package com.project.drinkly.ui.subscribe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentSubscribeBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.mypage.MypageWithdrawalFragment
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.ui.subscribe.viewModel.SubscribeViewModel
import com.project.drinkly.util.MyApplication

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
        viewModel = ViewModelProvider(this)[SubscribeViewModel::class.java]

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
            userInfo.observe(viewLifecycleOwner) {
                binding.run {
                    textViewNickname.text = "${it?.nickname}님"
                    if(it?.isSubscribe == true) {
                        layoutMembershipButton.visibility = View.GONE
                        textViewExpirationDday.text = "D-${it.subscribeInfo?.leftDays}"
                        textViewExpirationDay.text = "${it.subscribeInfo?.expiredDate} 만료"
                        textViewMembershipUsedNumber.text = "${it.subscribeInfo?.usedCount}회"
                    } else {
                        layoutSubscribe.visibility = View.GONE
                        layoutMembershipInfo.visibility = View.GONE
                    }
                }
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

            textViewNickname.text = "${MyApplication.userNickName}님"
            if(MyApplication.isSubscribe) {
                layoutMembershipButton.visibility = View.GONE
                viewModel.getUserId(mainActivity)
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