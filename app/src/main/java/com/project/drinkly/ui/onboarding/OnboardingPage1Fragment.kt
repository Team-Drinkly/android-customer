package com.project.drinkly.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentOnboardingPage1Binding

class OnboardingPage1Fragment : Fragment() {

    lateinit var binding: FragmentOnboardingPage1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingPage1Binding.inflate(layoutInflater)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }
}