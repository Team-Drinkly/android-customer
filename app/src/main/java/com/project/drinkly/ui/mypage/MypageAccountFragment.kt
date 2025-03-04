package com.project.drinkly.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.project.drinkly.R
import com.project.drinkly.api.TokenManager
import com.project.drinkly.databinding.FragmentMypageAccountBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicButtonDialogInterface
import com.project.drinkly.ui.dialog.BasicDialogInterface
import com.project.drinkly.ui.dialog.DialogBasic
import com.project.drinkly.ui.dialog.DialogBasicButton

class MypageAccountFragment : Fragment() {

    lateinit var binding: FragmentMypageAccountBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageAccountBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            // 로그아웃
            layoutButtonLogout.setOnClickListener {
                val dialog = DialogBasicButton("로그아웃 하시겠습니까?", "취소", "로그아웃")

                dialog.setBasicDialogInterface(object : BasicButtonDialogInterface {
                    override fun onClickYesButton() {
                        // 로그아웃
                        TokenManager(mainActivity).deleteAccessToken()
                        TokenManager(mainActivity).deleteRefreshToken()
                        fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                })

                dialog.show(mainActivity.supportFragmentManager, "DialogLogout")
            }

            // 계정탈퇴
            layoutButtonWithdrawal.setOnClickListener {

            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        binding.run {
            toolbar.run {
                textViewTitle.text = "계정 관리"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}