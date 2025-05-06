package com.project.drinkly.ui.onboarding

import android.os.Bundle
import android.os.Handler
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

                // 홈화면 이동
                mainActivity.setBottomNavigationHome()
            } else {
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, LoginFragment())
                    .commit()
            }
        }, 3000)

        return binding.root
    }
}