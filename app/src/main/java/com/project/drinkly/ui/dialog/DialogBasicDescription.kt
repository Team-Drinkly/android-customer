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
import com.project.drinkly.databinding.DialogBasicDescriptionBinding

interface BasicDescriptionDialogInterface {
    fun onClickYesButton()
}

class DialogBasicDescription(var title: String, var description: String, var button: String) : DialogFragment() {

    // 뷰 바인딩 정의
    private var _binding: DialogBasicDescriptionBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: BasicDescriptionDialogInterface? = null

    // 인터페이스 인스턴스
    private var listener: BasicDescriptionDialogInterface? = null

    // 리스너 설정 메서드
    fun setBasicDialogInterface(listener: BasicDescriptionDialogInterface) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogBasicDescriptionBinding.inflate(inflater)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        binding.run {
            textViewDialogTitle.text = title
            textViewDialogDescription.text = description
            buttonCheck.text = button

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