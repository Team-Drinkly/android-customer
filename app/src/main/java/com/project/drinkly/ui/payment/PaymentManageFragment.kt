package com.project.drinkly.ui.payment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.api.TokenManager
import com.project.drinkly.databinding.FragmentPaymentManageBinding
import com.project.drinkly.ui.BasicToast
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicButtonDialogInterface
import com.project.drinkly.ui.dialog.BasicDescriptionDialogInterface
import com.project.drinkly.ui.dialog.BasicDialogInterface
import com.project.drinkly.ui.dialog.DialogBasic
import com.project.drinkly.ui.dialog.DialogBasicButton
import com.project.drinkly.ui.dialog.DialogBasicDescription
import com.project.drinkly.ui.onboarding.LoginFragment
import com.project.drinkly.ui.payment.viewModel.PaymentViewModel
import com.project.drinkly.ui.store.StoreMembershipFragment
import com.project.drinkly.util.MyApplication

class PaymentManageFragment : Fragment() {

    lateinit var binding: FragmentPaymentManageBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: PaymentViewModel by lazy {
        ViewModelProvider(requireActivity())[PaymentViewModel::class.java]
    }

    private var isChecked = false
    private var status: String? = null

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

            layoutCheckBox.setOnClickListener {
                isChecked = !isChecked
                if(isChecked) {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_checked)
                    buttonNext.isEnabled = true
                } else {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_unchecked)
                    buttonNext.isEnabled = false
                }
            }

            buttonNext.setOnClickListener {
                // 구독권 결제
                buttonNext.isEnabled = false

                viewModel.paymentForSubscribe(
                    mainActivity,
                    onSuccess = {
                        mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, PaymentCompleteFragment())
                        .commit()
                    },
                    onFailure = {
                        buttonNext.isEnabled = true

                        val dialog = DialogBasicDescription("카드 결제를 실패했어요!", "은행 점검 시간이거나 카드 잔액이 부족하여\n결제가 진행되지 않았어요", "다시 시도해볼게요")

                        dialog.show(mainActivity.supportFragmentManager, "DialogPayment")
                    }
                )
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

                    viewModel.getCardInfo(mainActivity)

                    status = arguments?.getString("status")
                    if(status.isNullOrEmpty()) {
                        viewModel.getSubscribeStatusInfo(mainActivity)
                    }
                } else {
                    Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                    mainActivity.goToLogin()
                }
            }
        }

        binding.run {
            toolbar.run {
                textViewHead.text = "결제 수단 관리"
                buttonBack.setOnClickListener {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            registeredCardInfo.observe(viewLifecycleOwner) {
                binding.run {
                    if(it != null) {
                        textViewTitle.text = "지금 등록된 카드로\n멤버십이 자동으로 결제돼요"
                        if(arguments?.getBoolean("payment") == true) {
                            layoutCheckBox.visibility = View.VISIBLE
                            buttonNext.visibility = View.VISIBLE
                        } else  {
                            buttonNext.visibility = View.GONE
                            layoutCheckBox.visibility = View.GONE
                        }

                        showToast()

                        layoutCardEmpty.visibility = View.GONE
                        layoutCardInfo.run {
                            visibility = View.VISIBLE
                            textViewCardName.text = "${it.cardName}카드"
                            textViewCardNumber.text = "${it.cardFirst}-****-****-${it.cardLast}"

                            var cardOrderId = it.orderId
                            buttonCardDelete.setOnClickListener {
                                // 카드 삭제
                                when(status) {
                                    "ACTIVE" -> showCardDeleteDialog(
                                        "멤버십을 해지한 후 카드를 삭제할 수 있어요. 삭제하시겠어요?",
                                        cardOrderId,
                                        false
                                    )
                                    else -> showCardDeleteDialog(
                                        "등록된 카드를 삭제하시겠어요?",
                                        cardOrderId,
                                        true
                                    )
                                }
                            }
                        }
                    } else {
                        textViewTitle.text = "멤버십 구독을 위한\n간편결제 카드를 등록해주세요"

                        layoutCardEmpty.visibility = View.VISIBLE
                        layoutCardInfo.visibility = View.GONE
                        buttonNext.visibility = View.GONE
                        layoutCheckBox.visibility = View.GONE
                    }
                }
            }

            subscribeStatus.observe(viewLifecycleOwner) {
                status = it
            }
        }
    }

    private fun showCardDeleteDialog(message: String, cardOrderId: String, isAvailable: Boolean) {
        val dialog = DialogBasicButton(message, "아니요", "삭제하기", R.color.primary_50)

        dialog.setBasicDialogInterface(object : BasicButtonDialogInterface {
            override fun onClickYesButton() {
                if(isAvailable) {
                    viewModel.deleteCard(mainActivity, cardOrderId) {
                        binding.run {
                            layoutCardEmpty.visibility = View.VISIBLE
                            layoutCardInfo.visibility = View.GONE
                            buttonNext.visibility = View.GONE
                            layoutCheckBox.visibility = View.GONE
                        }
                        BasicToast.showBasicToastBottom(
                            requireContext(),
                            "등록된 카드가 정상적으로 삭제됐어요",
                            R.drawable.ic_check,
                            binding.root
                        )
                    }
                } else {
                    val bundle = Bundle().apply {
                        putBoolean("cardDelete", true)
                    }

                    val nextFragment = PaymentCancelFragment().apply {
                        arguments = bundle
                    }

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        })

        dialog.show(mainActivity.supportFragmentManager, "DialogCardDelete")
    }

    fun showToast() {
        binding.root.post {
            if (MyApplication.isCardRegistered) {
                MyApplication.isCardRegistered = false

                if(arguments?.getBoolean("payment") == true) {
                    BasicToast.showBasicToast(requireContext(), "카드 등록이 정상적으로 완료됐어요!", R.drawable.ic_check, binding.buttonNext)
                } else {
                    BasicToast.showBasicToastBottom(requireContext(), "카드 등록이 정상적으로 완료됐어요!", R.drawable.ic_check, binding.root)
                }
            }

            if (MyApplication.isCardDelete) {
                MyApplication.isCardDelete = false

                BasicToast.showBasicToastBottom(requireContext(), "등록된 카드가 정상적으로 삭제됐어요", R.drawable.ic_check, binding.root)
            }
        }
    }

}