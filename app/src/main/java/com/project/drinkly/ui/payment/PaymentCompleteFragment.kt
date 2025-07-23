package com.project.drinkly.ui.payment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.drinkly.databinding.FragmentPaymentCompleteBinding
import com.project.drinkly.ui.MainActivity

class PaymentCompleteFragment : Fragment() {

    lateinit var binding: FragmentPaymentCompleteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPaymentCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initView()

        binding.run {
            buttonNext.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }

        return binding.root
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
            hideOrderHistoryButton(true)
        }
    }
}