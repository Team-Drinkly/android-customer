package com.project.drinkly.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.databinding.FragmentOnboardingPage3Binding

class OnboardingPage3Fragment : Fragment() {

    lateinit var binding: FragmentOnboardingPage3Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingPage3Binding.inflate(layoutInflater)

        return binding.root
    }
}