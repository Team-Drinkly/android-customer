package com.project.drinkly.ui

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.project.drinkly.R
import com.project.drinkly.databinding.ToastBasicBinding
import com.project.drinkly.util.MainUtil.toPx
import kotlin.run

object BasicToast {
    fun showBasicToast(context: Context, message: String, icon: Int, anchorView: View) {
        val inflater = LayoutInflater.from(context)
        val binding: ToastBasicBinding =
            DataBindingUtil.inflate(inflater, R.layout.toast_basic, null, false)

        binding.run {
            textViewTooltip.text = message
            imageViewTooltip.setImageResource(icon)
        }

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val marginPx = 20.toPx()
        val popupWidth = screenWidth - marginPx * 2

        val popupWindow = PopupWindow(binding.root,
            popupWidth,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )

        anchorView.post {
            binding.root.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            val popupHeight = binding.root.measuredHeight

            val yOffset = -(anchorView.height + popupHeight + 8.toPx())

            popupWindow.showAsDropDown(anchorView, 0, yOffset)

            // 뷰가 detach될 때 PopupWindow 해제
            anchorView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View) {}
                override fun onViewDetachedFromWindow(v: View) {
                    if (popupWindow.isShowing) {
                        popupWindow.dismiss()
                    }
                }
            })

            binding.root.postDelayed({
                if (popupWindow.isShowing) {
                    popupWindow.dismiss()
                }
            }, 2000)
        }
    }
}