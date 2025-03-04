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
import com.project.drinkly.databinding.DialogBasicBinding

interface BasicDialogInterface {
    fun onClickYesButton()
}

class DialogBasic(var message: String) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogBasicBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: BasicDialogInterface? = null

    // 인터페이스 인스턴스
    private var listener: BasicDialogInterface? = null

    // 리스너 설정 메서드
    fun setBasicDialogInterface(listener: BasicDialogInterface) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBasicBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding.run {
            textViewDialogDescription.text = message

            buttonCheck.setOnClickListener {
                listener?.onClickYesButton() // 인터페이스를 통해 이벤트 전달
                dismiss()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}