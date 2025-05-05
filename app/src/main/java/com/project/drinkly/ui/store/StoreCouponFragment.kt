package com.project.drinkly.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentStoreCouponBinding
import com.project.drinkly.ui.MainActivity

class StoreCouponFragment : Fragment() {

    lateinit var binding : FragmentStoreCouponBinding
    lateinit var mainActivity: MainActivity

    var isChecked = false

    var isUsedCoupon = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreCouponBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            layoutCheckBox.setOnClickListener {
                isChecked = !isChecked
                if(isChecked) {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_checked)
                    buttonUseCoupon.isEnabled = true
                } else {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_unchecked)
                    buttonUseCoupon.isEnabled = false
                }
            }

            buttonUseCoupon.setOnClickListener {
                // 쿠폰 사용
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        binding.run {
            layoutCheckBox.visibility = View.VISIBLE
            textViewStoreName.text = arguments?.getString("storeName")
            layoutCoupon.run {
                textViewCouponTitle.text = arguments?.getString("couponTitle")
                textViewCouponDescription.text = arguments?.getString("couponDescription")
                textViewCouponDate.text = "유효기간: ${arguments?.getString("couponDate")}까지"
            }

            if(arguments?.getString("couponDescription").isNullOrEmpty()) {
                layoutCouponDescription.visibility = View.GONE
            } else {
                layoutCouponDescription.visibility = View.VISIBLE
                textViewCouponDescriptionValue.text = arguments?.getString("couponDescription")
            }

            toolbar.run {
                textViewTitle.text = "쿠폰 사용"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}