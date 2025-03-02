package com.project.drinkly.ui.onboarding.signUp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentSignUpPassBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.StoreMapFragment
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

        if(MyApplication.signUpPassAuthorization) {
            // 패스 인증 완료 후 회원가입 - 닉네임 화면 이동
            MyApplication.signUpPassAuthorization = false

            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, SignUpNickNameFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMyLocationButton(true)
            hideMapButton(true)
        }

        binding.run {
            toolbar.run {
                textViewTitle.text = "본인 인증"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}