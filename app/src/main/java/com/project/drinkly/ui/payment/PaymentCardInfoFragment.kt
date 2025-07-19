package com.project.drinkly.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentPaymentCardInfoBinding
import com.project.drinkly.ui.MainActivity

class PaymentCardInfoFragment : Fragment() {

    lateinit var binding: FragmentPaymentCardInfoBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPaymentCardInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {

        }

        return binding.root
    }

}