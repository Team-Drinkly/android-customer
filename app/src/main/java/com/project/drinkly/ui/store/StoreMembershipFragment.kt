package com.project.drinkly.ui.store

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentStoreMembershipBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

class StoreMembershipFragment : Fragment() {

    lateinit var binding: FragmentStoreMembershipBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreMembershipBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            buttonNext.setOnClickListener {
                fragmentManager?.popBackStack()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
            hideOrderHistoryButton(true)
        }

        binding.run {
            toolbar.run {
                textViewHead.text = "멤버십 사용"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }

            if(arguments?.getBoolean("history") == true) {
                layoutTitle.visibility = View.GONE
            } else {
                layoutTitle.visibility = View.VISIBLE
            }

            textViewStoreName.text = arguments?.getString("storeName")
            textViewAvailableDrink.text = arguments?.getString("availableDrinkName")
            textViewMembershipUsedTime.text = "${arguments?.getString("usedTime")} 사용완료"
            Glide.with(mainActivity).load(arguments?.getString("availableDrinkImage")).into(imageViewAvailableDrink)
        }
    }
}