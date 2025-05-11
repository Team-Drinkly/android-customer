package com.project.drinkly.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.project.drinkly.databinding.DialogEventBinding

interface PopUpDialogInterface {
    fun onClickYesButton(id: Int)
}
class DialogEvent() : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogEventBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: PopUpDialogInterface? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEventBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding.run {
            buttonClose.setOnClickListener {
                dismiss()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

            // 포커스 잃었을 때 깜빡이거나 깨지는 현상 방지
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}