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
                copyLinkToClipboard(requireContext(), code.toString(), "초대 코드가 복사되었어요")
            }

            buttonShareLink.setOnClickListener {
                // 초대 링크 복사
                var inviteLink =
                                "[친구 초대 이벤트 \uD83C\uDF81]\n" +
                                "드링클리는 제휴 매장에서 술 한 잔 혜택과 쿠폰을 받을 수 있는 멤버십 앱이에요.\n" +
                                "\n" +
                                "가입하실 때 아래 초대코드를 입력해주시면, 초대한 분이 멤버십 구독권을 받을 수 있어요. 친구와 함께 가볍게 한 잔 하러 가요!\n" +
                                "\n" +
                                "\uD83D\uDCCD 초대코드: ${code}\n" +
                                "\n" +
                                "지금 바로 드링클리 다운받기\n" +
                                "https://tosto.re/drinkly"
                copyLinkToClipboard(requireContext(), inviteLink, "초대 링크가 복사되었어요")
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
        val templateArgs = mapOf("code" to code.toString())

        if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
            ShareClient.instance.shareCustom(context, templateId, templateArgs) { result, error ->
                if (error != null) {
                    Log.e("KakaoShare", "카카오톡 커스텀 템플릿 공유 실패", error)
                } else if (result != null) {
                    context.startActivity(result.intent)
                }
            }
        } else {
            WebSharerClient.instance.makeCustomUrl(templateId, templateArgs)?.let { url ->
                context.startActivity(Intent(Intent.ACTION_VIEW, url))
            } ?: Log.e("KakaoShare", "웹 공유 URL 생성 실패")
        }
    }

    fun copyLinkToClipboard(context: Context, text: String, message: String) {
        // ClipboardManager 인스턴스 가져오기
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // 클립 데이터 생성
        val clip = ClipData.newPlainText("복사된 초대 코드", text)

        // 클립보드에 데이터 복사
        clipboard.setPrimaryClip(clip)

        BasicToast.showBasicToastBottom(requireContext(), message, R.drawable.ic_check, binding.root)
    }
}