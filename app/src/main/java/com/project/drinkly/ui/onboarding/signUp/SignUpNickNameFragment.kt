package com.project.drinkly.ui.onboarding.signUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentSignUpNicknameBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.home.HomeMapFragment

class SignUpNickNameFragment : Fragment() {

    lateinit var binding: FragmentSignUpNicknameBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpNicknameBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            editTextNickname.addTextChangedListener {
               if(editTextNickname.text.isNotEmpty()) {
                   buttonNext.isEnabled = true
               } else {
                   buttonNext.isEnabled = false
               }
            }

            editTextNickname.setOnEditorActionListener { textView, i, keyEvent ->
                // 닉네임 중복 확인

                true
            }

            buttonNext.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, HomeMapFragment())
                    .addToBackStack(null)
                    .commit()
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
            hideMyLocationButton(true)
            hideMapButton(true)
        }

        binding.run {
            toolbar.run {
                textViewTitle.text = "정보 입력"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}