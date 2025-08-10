package com.project.drinkly.ui.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentMypageQnaBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

class MypageQnaFragment : Fragment() {

    lateinit var binding: FragmentMypageQnaBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageQnaBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonKakao.setOnClickListener {
                mixpanel.track("click_mypage_customer_kakao_center", null)

                // 드링클리 카카오톡 채널
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://pf.kakao.com/_kWSGn"))
                startActivity(intent)
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
            toolbar.run {
                textViewHead.text = "고객센터"
                buttonBack.setOnClickListener {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

}