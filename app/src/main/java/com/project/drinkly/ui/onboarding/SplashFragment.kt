package com.project.drinkly.ui.onboarding

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.api.TokenManager
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentSplashBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.util.MyApplication

class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSplashBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        mainActivity.run {
            hideBottomNavigation(true)
            hideMyLocationButton(true)
            hideMapButton(true)
        }

        Handler().postDelayed({
            val tokenManager = TokenManager(mainActivity)
            if(tokenManager.getAccessToken() != null) {
                MyApplication.isLogin = true

                mainActivity.updateSubscriptionStatusIfNeeded(activity = mainActivity) { success ->
                    if (success) {
                        // 구독 상태가 오늘 날짜 기준으로 정상 체크됨 → 이후 로직 실행
                        Log.d("SubscriptionCheck", "✅ 상태 확인 완료 후 이어서 작업 실행")

                        // 홈화면 이동
                        mainActivity.setBottomNavigationHome()
                    } else {
                        Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                        mainActivity.goToLogin()
                    }
                }
            } else {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, LoginFragment())
                    .commit()
            }
        }, 3000)

        return binding.root
    }
}