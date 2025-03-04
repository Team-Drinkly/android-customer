package com.project.drinkly.ui.subscribe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentSubscribePaymentBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicDialogInterface
import com.project.drinkly.ui.dialog.DialogBasic
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.util.MyApplication
import kotlin.concurrent.fixedRateTimer

class SubscribePaymentFragment : Fragment() {

    lateinit var binding: FragmentSubscribePaymentBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSubscribePaymentBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity


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
            buttonMembershipPayment.run {
                if (MyApplication.isSubscribe) {
                    visibility = View.INVISIBLE

                    text = "멤버십 구독 해지하기"
                    backgroundTintList = resources.getColorStateList(R.color.gray9)

                    setOnClickListener {

                    }
                } else {
                    text = "멤버십 구독권 결제하기"
                    backgroundTintList = resources.getColorStateList(R.color.primary_50)

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

                toolbar.run {
                    textViewTitle.text = "구독 관리"
                    buttonBack.setOnClickListener {
                        fragmentManager?.popBackStack()
                    }
                }
            }
        }
    }

}