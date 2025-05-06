package com.project.drinkly.ui.onboarding.signUp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentSignUpAgreementBinding
import com.project.drinkly.ui.MainActivity

class SignUpAgreementFragment : Fragment() {

    lateinit var binding: FragmentSignUpAgreementBinding
    lateinit var mainActivity: MainActivity

    val isAgree = MutableList(4) { false }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpAgreementBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            layoutAgreementAll.setOnClickListener {
                isAgree[0] = !isAgree[0]
                checkAgreementAll()
            }
            checkbox1.setOnClickListener {
                isAgree[1] = !isAgree[1]
                checkAgree(1, checkbox1)
            }
            checkbox2.setOnClickListener {
                isAgree[2] = !isAgree[2]
                checkAgree(2, checkbox2)
            }
            layoutAgreement3.setOnClickListener {
                isAgree[3] = !isAgree[3]
                checkAgree(3, checkbox3)
            }
            /*
            checkbox4.setOnClickListener {
                isAgree[4] = !isAgree[4]
                checkAgree(4, checkbox4)
            }
             */

            // 서비스 이용 약관 확인
            textViewAgreement1.setOnClickListener {
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://drinkly.notion.site/1ae09cf02bf980eb87bdf92be7d48e54"))
                startActivity(intent)
            }

            // 개인정보 수집 및 이용동의서 확인
            textViewAgreement2.setOnClickListener {
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://drinkly.notion.site/1ae09cf02bf980a79391e106aac091d3"))
                startActivity(intent)
            }

            buttonNext.setOnClickListener {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, SignUpPassFragment())
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

    fun checkAgreementAll() {
        binding.run {
            if(isAgree[0]) {
                checkboxAll.setImageResource(R.drawable.ic_checkbox_checked)
                isAgree.fill(true)
                checkAgreementAllWithOthers()
            } else {
                checkboxAll.setImageResource(R.drawable.ic_checkbox_unchecked)
                isAgree.fill(false)
                checkAgreementAllWithOthers()
            }
        }
        checkEnable()
    }

    fun checkAgreementAllWithOthers() {
        binding.run {
            listOf(
                checkbox1,
                checkbox2,
                checkbox3
            ).forEachIndexed { index, checkbox ->
                checkAgree(index + 1, checkbox)
            }
        }
    }

    fun checkAgree(position: Int, view: ImageView) {
        if(isAgree[position]) {
            view.setImageResource(R.drawable.ic_check_checked)
        } else {
            view.setImageResource(R.drawable.ic_check_unchecked)
        }
        checkEnable()
        if(isAgree[1] && isAgree[2] && isAgree[3]) {
            binding.checkboxAll.setImageResource(R.drawable.ic_checkbox_checked)
        } else {
            binding.checkboxAll.setImageResource(R.drawable.ic_checkbox_unchecked)
        }
    }

    fun checkEnable() {
        binding.run {
            if(isAgree[1] && isAgree[2] && isAgree[3]) {
                buttonNext.isEnabled = true
            } else {
                buttonNext.isEnabled = false
            }
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
                textViewTitle.text = "약관 동의"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }
}