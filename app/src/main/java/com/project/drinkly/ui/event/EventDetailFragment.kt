package com.project.drinkly.ui.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentEventDetailBinding
import com.project.drinkly.ui.MainActivity

class EventDetailFragment : Fragment() {

    lateinit var binding: FragmentEventDetailBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEventDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonEvent.setOnClickListener {
                // 이벤트 참여 링크 연동
            }
        }

        return binding.root
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
        }

        binding.toolbar.run {
            textViewHead.text = "이벤트"

            buttonBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

}