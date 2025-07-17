package com.project.drinkly.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentPaymentManageBinding
import com.project.drinkly.ui.MainActivity

class PaymentManageFragment : Fragment() {

    lateinit var binding: FragmentPaymentManageBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPaymentManageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        return binding.root
    }

}