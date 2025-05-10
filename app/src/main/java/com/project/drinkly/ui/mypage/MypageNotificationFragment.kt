package com.project.drinkly.ui.mypage

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentMypageNotificationBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.ui.onboarding.viewModel.LoginViewModel

class MypageNotificationFragment : Fragment() {

    lateinit var binding: FragmentMypageNotificationBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: MypageViewModel by lazy {
        ViewModelProvider(requireActivity())[MypageViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageNotificationBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonNotificationOs.setOnClickListener {
                // 앱 알림 설정 화면으로 이동
            }

            switchNotification.setOnCheckedChangeListener { _, isChecked ->
                // 알림 상태 변경

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
                textViewTitle.text = "알림 설정"
                buttonBack.setOnClickListener {
                }
            }
        }
    }

}