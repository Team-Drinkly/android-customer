package com.project.drinkly.ui.store

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentStoreCouponBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.viewModel.StoreViewModel

class StoreCouponFragment : Fragment() {

    lateinit var binding : FragmentStoreCouponBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: StoreViewModel by lazy {
        ViewModelProvider(this)[StoreViewModel::class.java]
    }

    var isChecked = false

    var isUsedCoupon = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreCouponBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            layoutCheckBox.setOnClickListener {
                isChecked = !isChecked
                if(isChecked) {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_checked)
                    buttonUseCoupon.isEnabled = true
                } else {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_unchecked)
                    buttonUseCoupon.isEnabled = false
                }
            }

            buttonUseCoupon.setOnClickListener {
                // 쿠폰 사용
                viewModel.useStoreCoupon(mainActivity, arguments?.getLong("couponId") ?: 0L)
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
            usedCouponTime.observe(viewLifecycleOwner) {
                if(it != null) {
                    isUsedCoupon = true

                    binding.run {
                        textViewTooltip.text = "$it 에 사용되었습니다"
                        layoutCheckBox.visibility = View.GONE
                        buttonUseCoupon.run {
                            isEnabled = false
                            text = "쿠폰 사용 완료"
                        }
                        imageViewClick.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun initView() {
        mainActivity.updateSubscriptionStatusIfNeeded(activity = mainActivity) { success ->
            if (success) {
                // 구독 상태가 오늘 날짜 기준으로 정상 체크됨 → 이후 로직 실행
                Log.d("SubscriptionCheck", "✅ 상태 확인 완료 후 이어서 작업 실행")

            } else {
                Log.e("SubscriptionCheck", "❌ 상태 체크 실패")
            }
        }

        binding.run {
            layoutCheckBox.visibility = View.VISIBLE
            textViewStoreName.text = arguments?.getString("storeName")
            layoutCoupon.run {
                textViewCouponTitle.text = arguments?.getString("couponTitle")
                textViewCouponDescription.text = arguments?.getString("couponDescription")
                textViewCouponDate.text = "유효기간: ${arguments?.getString("couponDate")}까지"
            }

            if(arguments?.getString("couponDescription").isNullOrEmpty()) {
                layoutCouponDescription.visibility = View.GONE
            } else {
                layoutCouponDescription.visibility = View.VISIBLE
                textViewCouponDescriptionValue.text = arguments?.getString("couponDescription")
            }

            toolbar.run {
                textViewTitle.text = "쿠폰 사용"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}