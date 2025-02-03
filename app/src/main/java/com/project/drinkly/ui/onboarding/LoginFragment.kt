package com.project.drinkly.ui.onboarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.project.drinkly.databinding.FragmentLoginBinding
import com.project.drinkly.ui.MainActivity

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding
    lateinit var mainActivity: MainActivity

    private val handler = Handler(Looper.getMainLooper())  // UI 스레드 핸들러
    private val delay: Long = 3000 // 자동 슬라이드 시간 (3초)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity

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

        return binding.root
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