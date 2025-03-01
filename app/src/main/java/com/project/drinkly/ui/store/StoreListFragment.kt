package com.project.drinkly.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentStoreListBinding
import com.project.drinkly.ui.MainActivity

class StoreListFragment : Fragment() {

    lateinit var binding: FragmentStoreListBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {

        }

        mainActivity.binding.buttonList.run {
            setImageResource(R.drawable.ic_map)
            setOnClickListener {
                // 제휴업체 - 리스트 화면으로 전환
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreMapFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        mainActivity.run {
            hideBottomNavigation(false)
            hideMyLocationButton(true)
            hideMapButton(false)
        }
    }
}