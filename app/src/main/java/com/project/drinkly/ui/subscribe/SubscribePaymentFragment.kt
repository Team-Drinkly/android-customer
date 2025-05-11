package com.project.drinkly.ui.subscribe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.api.InfoManager
import com.project.drinkly.api.TokenManager
import com.project.drinkly.databinding.FragmentSubscribePaymentBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicDialogInterface
import com.project.drinkly.ui.dialog.DialogBasic
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.ui.subscribe.viewModel.SubscribeViewModel
import com.project.drinkly.ui.subscribe.viewModel.SubscriptionChecker.removeSubscriptionLastCheckedDate
import com.project.drinkly.util.MyApplication
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.concurrent.fixedRateTimer

class SubscribePaymentFragment : Fragment() {

    lateinit var binding: FragmentSubscribePaymentBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: SubscribeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSubscribePaymentBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(requireActivity())[SubscribeViewModel::class.java]

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

                binding.run {
                    var infoManager = InfoManager(mainActivity)
                    if (infoManager.getIsSubscribe() == true) {
                        binding.textViewSubscribeDay.text =
                            "${infoManager.getStartDate()} ~ ${infoManager.getExpiredDate()}"
                    }

                    buttonMembershipPayment.run {
                        if (InfoManager(mainActivity).getIsSubscribe() == true) {
                            visibility = View.INVISIBLE

                            text = "멤버십 구독 해지하기"
                            backgroundTintList = resources.getColorStateList(R.color.gray9)


                            setOnClickListener {

                            }
                        } else {
                            text = "멤버십 구독권 결제하기"
                            backgroundTintList = resources.getColorStateList(R.color.primary_50)

                            val calendar = Calendar.getInstance() // 현재 날짜 가져오기
                            val today = checkFormat(calendar)
                            calendar.add(Calendar.DAY_OF_YEAR, 30) // 30일 추가

                            textViewSubscribeDay.text = "$today ~ ${checkFormat(calendar)}"

                            setOnClickListener {

                                val dialog = DialogBasic("결제 기능 준비 중입니다.\n빠른 시일 내에 업데이트 될 예정입니다!")

                                dialog.setBasicDialogInterface(object : BasicDialogInterface {
                                    override fun onClickYesButton() {

                                    }
                                })

                                dialog.show(mainActivity.supportFragmentManager, "DialogPayment")


                                /*
                                // 구독권 결제 완료 화면으로 이동
                                mainActivity.supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView_main, SubscribeCompleteFragment())
                                    .addToBackStack(null)
                                    .commit()
                                 */
                            }
                        }
                    }
                }

            } else {
                Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                mainActivity.goToLogin()
            }
        }

        binding.run {
            toolbar.run {
                textViewTitle.text = "구독 관리"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    fun checkFormat(calendar: Calendar): String {

        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN) // 원하는 날짜 형식 지정
        return "${dateFormat.format(calendar.time)}" // 변환된 날짜 반환

    }
}