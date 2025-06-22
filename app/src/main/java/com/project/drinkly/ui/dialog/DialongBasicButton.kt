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
import com.project.drinkly.databinding.DialogBasicButtonBinding

interface BasicButtonDialogInterface {
    fun onClickYesButton()
}

class DialogBasicButton(var message: String, var cancelText: String, var checkText: String, var color: Int) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogBasicButtonBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: BasicButtonDialogInterface? = null

    // 인터페이스 인스턴스
    private var listener: BasicButtonDialogInterface? = null

    // 리스너 설정 메서드
    fun setBasicDialogInterface(listener: BasicButtonDialogInterface) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBasicButtonBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding.run {
            textViewDialogDescription.text = message

            buttonCancel.run {
                text = cancelText
                setOnClickListener {
                    dismiss()
                }
            }

            buttonCheck.run {
                text = checkText
                backgroundTintList = resources.getColorStateList(color)
                setOnClickListener {
                    listener?.onClickYesButton() // 인터페이스를 통해 이벤트 전달
                    dismiss()
                }
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