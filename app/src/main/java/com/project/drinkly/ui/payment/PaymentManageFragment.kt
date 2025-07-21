package com.project.drinkly.ui.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentPaymentManageBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.mypage.MypageWithdrawalFragment
import com.project.drinkly.ui.payment.viewModel.PaymentViewModel

class PaymentManageFragment : Fragment() {

    lateinit var binding: FragmentPaymentManageBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: PaymentViewModel by lazy {
        ViewModelProvider(requireActivity())[PaymentViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPaymentManageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        binding.run {
            layoutCardEmpty.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, PaymentCardInfoFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonNext.setOnClickListener {
                // 구독권 결제
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
            hideOrderHistoryButton(true)

            updateSubscriptionStatusIfNeeded(activity = mainActivity) { success ->
                if (success) {
                    // 구독 상태가 오늘 날짜 기준으로 정상 체크됨 → 이후 로직 실행
                    Log.d("SubscriptionCheck", "✅ 상태 확인 완료 후 이어서 작업 실행")

                    viewModel.getCardInfo(mainActivity)

                } else {
                    Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                    mainActivity.goToLogin()
                }
            }
        }

        binding.run {
            toolbar.run {
                textViewTitle.text = "결제 수단 관리"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }

            if(arguments?.getBoolean("payment") == true) {
                buttonNext.visibility = View.VISIBLE
            } else  {
                buttonNext.visibility = View.GONE
                layoutCheckBox.visibility = View.GONE
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            registeredCardInfo.observe(viewLifecycleOwner) {
                binding.run {
                    if(it != null) {
                        layoutCardEmpty.visibility = View.GONE
                        layoutCardInfo.run {
                            visibility = View.VISIBLE
                            textViewCardName.text = it.cardName
                            textViewCardNumber.text = "${it.cardFirst}-****-****-${it.cardLast}"

                            buttonCardDelete.setOnClickListener {
                                // 카드 삭제
                            }
                        }
                    } else {
                        layoutCardEmpty.visibility = View.VISIBLE
                        layoutCardInfo.visibility = View.GONE
                    }
                }
            }
        }
    }

}