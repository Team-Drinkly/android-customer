package com.project.drinkly.ui.subscribe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentSubscribeCompleteBinding
import com.project.drinkly.ui.MainActivity

class SubscribeCompleteFragment : Fragment() {

    lateinit var binding: FragmentSubscribeCompleteBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSubscribeCompleteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initView()

        return binding.root
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
        }
    }
}