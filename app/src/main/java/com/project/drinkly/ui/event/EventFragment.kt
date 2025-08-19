package com.project.drinkly.ui.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentEventBinding
import com.project.drinkly.ui.MainActivity

class EventFragment : Fragment() {

    lateinit var binding: FragmentEventBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEventBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {

        }

        return binding.root
    }

}