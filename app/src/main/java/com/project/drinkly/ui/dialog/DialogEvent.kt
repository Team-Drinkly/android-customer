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
        val window = dialog?.window
        if (window != null) {
            val params = window.attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT
            window.attributes = params
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}