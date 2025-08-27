package com.project.drinkly.ui.event

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.drinkly.R
import com.project.drinkly.api.response.coupon.StoreCouponResponse
import com.project.drinkly.databinding.FragmentCouponListBottomSheetBinding
import com.project.drinkly.databinding.FragmentEventFriendInviteBottomSheetBinding
import com.project.drinkly.ui.BasicToast
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.StoreCouponFragment
import com.project.drinkly.ui.store.adapter.StoreDetailCouponAdapter
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

class EventFriendInviteBottomSheetFragment(var activity: MainActivity, var code: String?) : BottomSheetDialogFragment() {

    lateinit var binding: FragmentEventFriendInviteBottomSheetBinding
    lateinit var mainActivity: MainActivity

    interface OnCouponClickListener {
        fun onBottomSheetClicked(position: Int, type: String)
    }

    var clickListener: OnCouponClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEventFriendInviteBottomSheetBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            textViewCode.text = code

            layoutCopy.setOnClickListener {
                // 초대 코드 복사
            }

            buttonShareLink.setOnClickListener {
                // 초대 링크 복사
            }

            buttonShareKakao.setOnClickListener {
                // 카카오톡 공유
            }

            buttonClose.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        return dialog
    }
}