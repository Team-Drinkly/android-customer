package com.project.drinkly.ui.event

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
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
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
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
            textViewCode.text = code.toString()

            layoutCopy.setOnClickListener {
                // 초대 코드 복사
                copyLinkToClipboard(requireContext(), code.toString())
            }

            buttonShareLink.setOnClickListener {
                // 초대 링크 복사
                var inviteLink = "드링클리에서 나와 같이 혜택 받자!\n초대 코드 : ${code}\n드링클리 다운로드 링크 : https://tosto.re/drinkly"
                copyLinkToClipboard(requireContext(), inviteLink)
            }

            buttonShareKakao.setOnClickListener {
                // 카카오톡 공유
                shareToKakaoWithCustomTemplate(requireContext())
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

    fun shareToKakaoWithCustomTemplate(context: Context) {
        val templateId = 123793L

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            ShareClient.instance.shareCustom(context, templateId, null) { result, error ->
                if (error != null) {
                    Log.e("KakaoShare", "카카오톡 커스텀 템플릿 공유 실패", error)
                } else if (result != null) {
                    Log.d("KakaoShare", "공유 성공: ${result.intent.data}") // ✅ 딥링크 URI 로그
                    context.startActivity(result.intent)
                }
            }
        } else {
            WebSharerClient.instance.makeCustomUrl(templateId, null)?.let { url ->
                Log.d("KakaoShare", "웹 공유 URI: $url")
                context.startActivity(Intent(Intent.ACTION_VIEW, url))
            } ?: Log.e("KakaoShare", "웹 공유 URL 생성 실패")
        }
    }

    fun copyLinkToClipboard(context: Context, text: String) {
        // ClipboardManager 인스턴스 가져오기
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // 클립 데이터 생성
        val clip = ClipData.newPlainText("복사된 초대 코드", text)

        // 클립보드에 데이터 복사
        clipboard.setPrimaryClip(clip)
    }
}