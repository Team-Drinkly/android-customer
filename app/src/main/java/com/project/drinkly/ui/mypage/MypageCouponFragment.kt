package com.project.drinkly.ui.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentMypageCouponBinding
import com.project.drinkly.ui.MainActivity
import kotlin.concurrent.fixedRateTimer

class MypageCouponFragment : Fragment() {

    lateinit var binding: FragmentMypageCouponBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMypageCouponBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        val category: List<String> = listOf("사용 전", "사용 완료")

        val adapter = TemplateCategoryVPAdapter(this)
        binding.viewpager.adapter = adapter

        TabLayoutMediator(binding.tab, binding.viewpager) { tab, position ->
            tab.text = category[position]  // 포지션에 따른 텍스트
        }.attach()  // 탭 레이아웃과 뷰페이저를 붙여주는 기능

        binding.run {
            tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.let {
                        binding.viewpager.currentItem = it.position
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })


        }

        tabItemMargin(binding.tab)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    private fun tabItemMargin(mTabLayout: TabLayout) {
        for (i in 0 until mTabLayout.tabCount) {
            val tab = (mTabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(20, 8, 20, 8)
            tab.requestLayout()
        }
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
        }

        binding.run {
            toolbar.run {
                textViewTitle.text = "내 쿠폰함"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}

class TemplateCategoryVPAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2  // 일반, 기간, 반복 총 3개의 탭

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                // 사용 전 쿠폰에 해당하는 Fragment
                MypageCouponUnuseFragment()
            }
            1 -> {
                // 사용 완료 쿠폰에 해당하는 Fragment
                MypageCouponUsedFragment()
            }
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}
