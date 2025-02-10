package com.project.drinkly.ui.onboarding

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentLoginBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.ui.onboarding.signUp.SignUpAgreementFragment

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    private val handler = Handler(Looper.getMainLooper())  // UI 스레드 핸들러
    private val delay: Long = 3000 // 자동 슬라이드 시간 (3초)

    // 카카오 로그인
    // 카카오계정으로 로그인 공통 callback 구성
    // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            // 로그인 기능 구현
            mainActivity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView_main, SignUpAgreementFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

        binding.root.post {
            setupViewPager()  // post()를 사용하여 뷰가 완전히 생성된 후 실행
        }

        binding.run {
            buttonSeek.setOnClickListener {
                // 제휴업체 둘러보기
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreMapFragment())
                    .addToBackStack(null)
                    .commit()
            }

            buttonKakao.setOnClickListener {
                // 카카오 로그인
                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(mainActivity)) {
                    UserApiClient.instance.loginWithKakaoTalk(mainActivity) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡으로 로그인 실패", error)

                            // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                            // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            }

                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = callback)
                        } else if (token != null) {
                            Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                            // 로그인 기능 구현
                            mainActivity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView_main, SignUpAgreementFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(mainActivity, callback = callback)
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mainActivity.run {
            hideBottomNavigation(true)
            hideMyLocationButton(true)
            hideMapButton(true)
        }
    }

    private fun setupViewPager() {
        binding.run {
            viewPager.run {
                adapter = MainFragmentStateAdapter(mainActivity)

                dotsIndicator.setViewPager2(viewPager)

                // 자동 슬라이드 시작
                startAutoSlide()

                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        // 사용자가 직접 스와이프하면 슬라이드 타이머 리셋
                        resetAutoSlide()
                    }
                })
            }
        }
    }

    private val autoSlideRunnable = object : Runnable {
        override fun run() {
            binding.viewPager?.let { viewPager ->
                val nextItem = (viewPager.currentItem + 1) % 4 // 페이지 4개 기준
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, delay) // 다음 실행 예약
            }
        }
    }

    private fun startAutoSlide() {
        handler.postDelayed(autoSlideRunnable, delay)
    }

    private fun resetAutoSlide() {
        handler.removeCallbacks(autoSlideRunnable)
        handler.postDelayed(autoSlideRunnable, delay)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null) // 메모리 누수 방지
    }

    // ViewPager2 어댑터
    inner class MainFragmentStateAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OnboardingPage1Fragment()
                1 -> OnboardingPage2Fragment()
                2 -> OnboardingPage3Fragment()
                3 -> OnboardingPage4Fragment()
                else -> OnboardingPage1Fragment()
            }
        }
    }
}