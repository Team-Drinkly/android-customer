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
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.ui.subscribe.viewModel.SubscribeViewModel

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

        viewModel.getUserId(mainActivity)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()

        mainActivity.run {
            hideMapButton(true)
            hideMyLocationButton(true)
        }
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

                        buttonSubscribeMembership.setOnClickListener {
                            // 멤버십 구독 화면으로 전환

                        }
                    }
                }
            }
        }
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "구독"
            }
        }
    }
}