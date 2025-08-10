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
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentMypageNotificationBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.ui.onboarding.viewModel.LoginViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

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

        observeViewModel()

        binding.run {
            buttonNotificationOs.setOnClickListener {
                // 앱 알림 설정 화면으로 이동
                presentNotificationSetting(mainActivity)
            }

            switchNotification.setOnCheckedChangeListener { _, isChecked ->
                mixpanel.track("click_mypage_alarm_toggle", null)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun observeViewModel() {
        viewModel.run {
            notificationStatus.observe(viewLifecycleOwner) {
                binding.switchNotification.isChecked = it
            }
        }
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
        }

        viewModel.getNotificationStatus(mainActivity)

        binding.run {
            if(NotificationManagerCompat.from(mainActivity).areNotificationsEnabled()) {
                layoutNotificationOs.visibility = View.GONE
                layoutButtonNotification.alpha = 1f
            } else {
                layoutNotificationOs.visibility = View.VISIBLE
                layoutButtonNotification.alpha = 0.6f
                switchNotification.isChecked = false
            }

            toolbar.run {
                textViewHead.text = "알림 설정"
                buttonBack.setOnClickListener {
                    viewModel.saveNotificationStatus(mainActivity, switchNotification.isChecked)
                    mainActivity.supportFragmentManager.popBackStack()
                }
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.saveNotificationStatus(mainActivity, switchNotification.isChecked)
                    mainActivity.supportFragmentManager.popBackStack()
                }
            })
        }
    }

    fun presentNotificationSetting(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationSettingOreo(context)
        } else {
            notificationSettingOreoLess(context)
        }
        try {
            context.startActivity(intent)
        }catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationSettingOreo(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun notificationSettingOreoLess(context: Context): Intent {
        return Intent().also { intent ->
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", context.packageName)
            intent.putExtra("app_uid", context.applicationInfo?.uid)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }
}