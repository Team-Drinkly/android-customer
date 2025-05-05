package com.project.drinkly.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentStoreMembershipBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.viewModel.StoreViewModel

class StoreMembershipFragment : Fragment() {

    lateinit var binding: FragmentStoreMembershipBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StoreViewModel

    var isChecked = false

    var isUsedMembership = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreMembershipBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[StoreViewModel::class.java]

        binding.run {
            layoutCheckBox.setOnClickListener {
                isChecked = !isChecked
                if(isChecked) {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_checked)
                    buttonUseMembership.isEnabled = true
                } else {
                    imageViewCheckBox.setImageResource(R.drawable.ic_checkcircle_unchecked)
                    buttonUseMembership.isEnabled = false
                }
            }

            buttonUseMembership.setOnClickListener {
                // 멤버십 사용
                viewModel.useMembership(mainActivity, arguments?.getLong("storeId") ?: 0, arguments?.getString("drinkName", "").toString())
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
        observeViewModel()
    }

    fun observeViewModel() {
        viewModel.run {
            usedMembershipTime.observe(viewLifecycleOwner) {
                if(it != null) {
                    isUsedMembership = true

                    binding.run {
                        textViewTooltip.text = "$it 에 사용되었습니다"
                        layoutCheckBox.visibility = View.GONE
                        buttonUseMembership.isEnabled = false
                    }
                }
            }
        }
    }

    fun initView() {
        binding.run {
            layoutCheckBox.visibility = View.VISIBLE
            textViewStoreName.text = arguments?.getString("storeName")
            textViewAvaiableDrinkName.text = arguments?.getString("drinkName")
            toolbar.run {
                textViewTitle.text = "멤버십 사용"
                buttonBack.setOnClickListener {
                    if(!isUsedMembership) {
                        fragmentManager?.popBackStack()
                    } else {
                        fragmentManager?.popBackStack("membership", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    }
                }
            }
        }
    }

}