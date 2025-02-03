package com.project.drinkly.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.databinding.FragmentOnboardingPage2Binding

class OnboardingPage2Fragment : Fragment() {

    lateinit var binding: FragmentOnboardingPage2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingPage2Binding.inflate(layoutInflater)

        return binding.root
    }
}