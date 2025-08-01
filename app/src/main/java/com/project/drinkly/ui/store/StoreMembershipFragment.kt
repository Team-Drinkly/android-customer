package com.project.drinkly.ui.store

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentStoreMembershipBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

class StoreMembershipFragment : Fragment() {

    lateinit var binding: FragmentStoreMembershipBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StoreViewModel

    var isChecked = false

    var isUsedMembership = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreMembershipBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[StoreViewModel::class.java]

        binding.run {
            layoutCheckBox.setOnClickListener {
                mixpanel.track("click_membership_notice", null)

                isChecked = !isChecked
                if(isChecked) {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_checked)
                    buttonUseMembership.isEnabled = true
                } else {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_unchecked)
                    buttonUseMembership.isEnabled = false
                }
            }

            buttonUseMembership.setOnClickListener {
                mixpanel.track("click_membership_use", null)

                // 멤버십 사용
                viewModel.useMembership(mainActivity, arguments?.getLong("storeId") ?: 0, arguments?.getString("drinkName", "").toString())
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.run {
            usedMembershipTime.observe(viewLifecycleOwner) {
                if(it != null) {
                    isUsedMembership = true

                    binding.run {
                        textViewTooltip.text = "${it}에 사용되었습니다"
                        layoutCheckBox.visibility = View.GONE
                        buttonUseMembership.run {
                            isEnabled = false
                            text = "멤버십 사용 완료"
                        }
                        imageViewClick.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun initView() {
        viewModel.usedMembershipTime.value = null

        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
            hideOrderHistoryButton(true)

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
            layoutCheckBox.visibility = View.VISIBLE
            textViewStoreName.text = arguments?.getString("storeName")
            textViewAvaiableDrinkName.text = arguments?.getString("drinkName")

            if(arguments?.getBoolean("isUsed") == true) {
                textViewTooltip.text = "${arguments?.getString("usedDate")}에 사용되었습니다"
                layoutCheckBox.visibility = View.GONE
                buttonUseMembership.run {
                    isEnabled = false
                    text = "멤버십 사용 완료"
                }
                imageViewClick.visibility = View.GONE
            }

            toolbar.run {
                textViewHead.text = "멤버십 사용"
                buttonBack.setOnClickListener {
                    if(!isUsedMembership) {
                        parentFragmentManager.popBackStack()
                    } else {
                        parentFragmentManager.popBackStack("membership", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(!isUsedMembership) {
                    parentFragmentManager.popBackStack()
                } else {
                    parentFragmentManager.popBackStack("membership", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        })
    }
}