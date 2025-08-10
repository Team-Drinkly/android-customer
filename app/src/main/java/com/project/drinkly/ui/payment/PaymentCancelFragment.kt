package com.project.drinkly.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.api.InfoManager
import com.project.drinkly.databinding.FragmentPaymentCancelBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicButtonDialogInterface
import com.project.drinkly.ui.dialog.DialogBasicButton
import com.project.drinkly.ui.payment.viewModel.PaymentViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel
import com.project.drinkly.util.MainUtil.checkFormat
import com.project.drinkly.util.MainUtil.parseKoreanDateToCalendar
import com.project.drinkly.util.MyApplication
import java.util.Calendar

class PaymentCancelFragment : Fragment() {
    lateinit var binding: FragmentPaymentCancelBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: PaymentViewModel by lazy {
        ViewModelProvider(requireActivity())[PaymentViewModel::class.java]
    }

    var isChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPaymentCancelBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity


        binding.run {
            layoutCheckBox.setOnClickListener {
                isChecked = !isChecked
                if(isChecked) {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_checked_gray1)
                    buttonUnsubscribe.isEnabled = true
                } else {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_unchecked)
                    buttonUnsubscribe.isEnabled = false
                }
            }

            buttonUnsubscribe.setOnClickListener {
                if(arguments?.getBoolean("cardDelete") == true) {
                    mixpanel.track("click_card_delete_and_cancel", null)

                    val dialog = DialogBasicButton("멤버십 구독을 해지하고\n카드를 삭제하시겠습니까?", "아니요", "네", R.color.red)

                    dialog.setBasicDialogInterface(object : BasicButtonDialogInterface {
                        override fun onClickYesButton() {
                            mixpanel.track("click_card_delete_and_cancel_confirm", null)

                            // 구독 해지 + 카드 삭제
                            viewModel.deleteCardMembership(mainActivity) {
                                MyApplication.isCardDelete = true

                                parentFragmentManager.popBackStack()
                            }
                        }
                    })

                    dialog.show(mainActivity.supportFragmentManager, "DialogUnsubscribe")
                } else {
                    mixpanel.track("click_membership_cancel", null)

                    val dialog = DialogBasicButton("멤버십 구독을 해지하시겠습니까?", "아니요", "해지하기", R.color.red)

                    dialog.setBasicDialogInterface(object : BasicButtonDialogInterface {
                        override fun onClickYesButton() {
                            mixpanel.track("click_membership_cancel_confirm", null)

                            // 구독 해지
                            viewModel.cancelSubscribe(mainActivity) {
                                parentFragmentManager.popBackStack()
                            }
                        }
                    })

                    dialog.show(mainActivity.supportFragmentManager, "DialogUnsubscribe")
                }
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
        }

        binding.run {

            val startCalendar = parseKoreanDateToCalendar(InfoManager(mainActivity).getStartDate().toString()) ?: Calendar.getInstance()

            // 환불 가능 마감일: 시작일 + 6일
            val refundDeadlineCalendar = startCalendar.clone() as Calendar
            refundDeadlineCalendar.add(Calendar.DAY_OF_YEAR, 6)
            val refundDeadline = checkFormat(refundDeadlineCalendar)

            // 환불 불가 시작일: 시작일 + 7일
            val noRefundAfterCalendar = startCalendar.clone() as Calendar
            noRefundAfterCalendar.add(Calendar.DAY_OF_YEAR, 7)
            val noRefundAfter = checkFormat(noRefundAfterCalendar)

            // 멤버십 구독 해지 안내사항
            val message = requireActivity().getString(
                R.string.membership_unsubscribe_guide,
                refundDeadline,
                noRefundAfter,
                InfoManager(mainActivity).getExpiredDate()
            )

            textViewGuideDescription.text = message

            if(arguments?.getBoolean("cardDelete") == true) {
                textViewDescription.text = "드링클리 멤버십 구독을 해지하고\n카드를 삭제하시겠습니까?"
                buttonUnsubscribe.text = "구독 해지하고 카드 삭제하기"
                toolbar.textViewHead.text = "구독 해지 및 카드 삭제"
            } else {
                textViewDescription.text = "드링클리 멤버십 구독을\n해지하시겠습니까?"
                buttonUnsubscribe.text = "멤버십 구독 해지하기"
                toolbar.textViewHead.text = "멤버십 구독 해지"
            }

            toolbar.run {
                buttonBack.setOnClickListener {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }
}