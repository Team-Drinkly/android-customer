package com.project.drinkly.ui.onboarding.signUp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentSignUpPassBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicDialogInterface
import com.project.drinkly.ui.dialog.DialogBasic
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel
import com.project.drinkly.util.MyApplication

class SignUpPassFragment : Fragment() {

    lateinit var binding: FragmentSignUpPassBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpPassBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonNext.setOnClickListener {
                mixpanel.track("click_signup2", null)

                // 패스 본인인증
                val passIntent = Intent(mainActivity, PassWebActivity::class.java)
                startActivity(passIntent)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()

        if(MyApplication.signUpPassAuthorization == true) {
            // 패스 인증 완료 후 회원가입 - 닉네임 화면 이동
            MyApplication.signUpPassAuthorization = null

            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, SignUpNickNameFragment())
                .addToBackStack(null)
                .commit()
        } else if(MyApplication.signUpPassAuthorization == false) {
            MyApplication.signUpPassAuthorization = null

            val dialog = DialogBasic("이미 가입된 계정이 있어요.\n기존 계정으로 로그인해주세요.")

            dialog.setBasicDialogInterface(object : BasicDialogInterface {
                override fun onClickYesButton() {
                    fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            })

            dialog.show(mainActivity.supportFragmentManager, "DialogPass")
        }
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMyLocationButton(true)
            hideMapButton(true)
            hideOrderHistoryButton(true)
        }

        binding.run {
            toolbar.run {
                textViewHead.text = "본인 인증"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}