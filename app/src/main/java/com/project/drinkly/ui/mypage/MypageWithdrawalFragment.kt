package com.project.drinkly.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.api.TokenManager
import com.project.drinkly.databinding.FragmentMypageWithdrawalBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicButtonDialogInterface
import com.project.drinkly.ui.dialog.DialogBasicButton
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

class MypageWithdrawalFragment : Fragment() {

    lateinit var binding: FragmentMypageWithdrawalBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: MypageViewModel

    var isChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageWithdrawalBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[MypageViewModel::class.java]

        binding.run {
            layoutCheckBox.setOnClickListener {
                isChecked = !isChecked
                if(isChecked) {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_checked_gray1)
                    buttonWithdrawal.isEnabled = true
                } else {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_unchecked)
                    buttonWithdrawal.isEnabled = false
                }
            }

            buttonWithdrawal.setOnClickListener {
                mixpanel.track("click_mypage_signout", null)

                // 계정 탈퇴
                val dialog = DialogBasicButton("드링클리 계정을 탈퇴하시겠습니까?", "취소", "계정 탈퇴", R.color.red)

                dialog.setBasicDialogInterface(object : BasicButtonDialogInterface {
                    override fun onClickYesButton() {
                        mixpanel.track("click_mypage_signout_confirm", null)
                        
                        // 회원탈퇴
                        viewModel.withdrawal(mainActivity)
                    }
                })

                dialog.show(mainActivity.supportFragmentManager, "DialogWithdrawal")
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
        }

        binding.run {
            toolbar.run {
                textViewHead.text = "계정 탈퇴"
                buttonBack.setOnClickListener {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

}