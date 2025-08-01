package com.project.drinkly.ui.payment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.api.InfoManager
import com.project.drinkly.databinding.FragmentSubscribePaymentBinding
import com.project.drinkly.ui.BasicToast
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicButtonDialogInterface
import com.project.drinkly.ui.dialog.DialogBasicButton
import com.project.drinkly.ui.payment.viewModel.PaymentViewModel
import com.project.drinkly.util.MainUtil.checkFormat
import java.util.Calendar

class SubscribePaymentFragment : Fragment() {

    lateinit var binding: FragmentSubscribePaymentBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: PaymentViewModel by lazy {
        ViewModelProvider(requireActivity())[PaymentViewModel::class.java]
    }

    private var status: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSubscribePaymentBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

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
        }

        mainActivity.updateSubscriptionStatusIfNeeded(activity = mainActivity) { success ->
            if (success) {
                // 구독 상태가 오늘 날짜 기준으로 정상 체크됨 → 이후 로직 실행
                Log.d("SubscriptionCheck", "✅ 상태 확인 완료 후 이어서 작업 실행")

                viewModel.getSubscribeStatusInfo(mainActivity)

            } else {
                Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                mainActivity.goToLogin()
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

    fun observeViewModel() {
        viewModel.run {
            subscribeStatus.observe(viewLifecycleOwner) {
                status = it

                checkSubscribeInfo(it)
            }
        }
    }

    fun checkSubscribeInfo(status: String) {
        binding.run {
            var infoManager = InfoManager(mainActivity)
            if (infoManager.getIsSubscribe() == true) {
                binding.textViewSubscribeDay.text =
                    "${infoManager.getStartDate()} ~ ${infoManager.getExpiredDate()}"
            }

            if (InfoManager(mainActivity).getIsSubscribe() == true) {
                when(status) {
                    "ACTIVE_SCHEDULED" -> {
                        buttonMembershipPayment.run {
                            visibility = View.VISIBLE
                            text = "멤버십 해지 취소하기"
                            backgroundTintList = resources.getColorStateList(R.color.primary_50)

                            setOnClickListener {
                                val dialog = DialogBasicButton("멤버십 해지를 취소하고 계속해서 드링클리 혜택을 누리시겠어요?", "아니요", "해지 취소하기", R.color.primary_50)

                                dialog.setBasicDialogInterface(object : BasicButtonDialogInterface {
                                    override fun onClickYesButton() {
                                        // 구독 해지 요청 취소
                                        viewModel.revertCancelSubscribe(mainActivity) {
                                            viewModel.getSubscribeStatusInfo(mainActivity)
                                        }
                                    }
                                })

                                dialog.show(mainActivity.supportFragmentManager, "DialogRevertSubscribe")
                            }
                        }
                        buttonUnsubscribe.visibility = View.INVISIBLE
                    }
                    "ACTIVE" -> {
                        buttonMembershipPayment.run {
                            visibility = View.VISIBLE
                            text = "멤버십 구독 중"
                            backgroundTintList = resources.getColorStateList(R.color.gray9)
                        }
                        buttonUnsubscribe.run {
                            visibility = View.VISIBLE

                            setOnClickListener {
                                // 구독 해지
                                mainActivity.supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView_main, PaymentCancelFragment())
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }
                    }
                    else -> {
                        buttonMembershipPayment.run {
                            visibility = View.VISIBLE
                            text = "멤버십 구독 중"
                            backgroundTintList = resources.getColorStateList(R.color.gray9)
                        }
                        buttonUnsubscribe.visibility = View.INVISIBLE
                    }
                }
            } else {
                buttonMembershipPayment.run {
                    text = "멤버십 구독권 결제하기"
                    backgroundTintList = resources.getColorStateList(R.color.primary_50)

                    val calendar = Calendar.getInstance() // 현재 날짜 가져오기
                    val today = checkFormat(calendar)
                    calendar.add(Calendar.DAY_OF_YEAR, 30) // 30일 추가

                    textViewSubscribeDay.text = "$today ~ ${checkFormat(calendar)}"

                    setOnClickListener {
                        // 구독권 결제 수단 관리 화면으로 이동
                        val bundle = Bundle().apply {
                            putBoolean("payment", true)
                            putString("status", status)
                        }

                        var nextFragment = PaymentManageFragment().apply {
                            arguments = bundle
                        }

                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, nextFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
                buttonUnsubscribe.visibility = View.INVISIBLE
            }
        }
    }
}