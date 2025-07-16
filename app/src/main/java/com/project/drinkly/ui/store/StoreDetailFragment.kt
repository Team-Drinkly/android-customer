package com.project.drinkly.ui.store

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.R
import com.project.drinkly.api.InfoManager
import com.project.drinkly.api.response.coupon.StoreCouponResponse
import com.project.drinkly.api.response.store.StoreDetailResponse
import com.project.drinkly.databinding.FragmentStoreDetailBinding
import com.project.drinkly.ui.BasicToast
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.adapter.StoreAvailableDrinkAdapter
import com.project.drinkly.ui.store.adapter.StoreImagePagerAdapter
import com.project.drinkly.ui.store.adapter.StoreMenuImageAdapter
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.ui.subscribe.SubscribePaymentFragment
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel
import com.project.drinkly.util.MyApplication
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignTop

class StoreDetailFragment : Fragment() {

    lateinit var binding: FragmentStoreDetailBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StoreViewModel

    lateinit var storeMainImageAdapter: StoreImagePagerAdapter
    lateinit var storeMenuImageAdapter: StoreMenuImageAdapter
    lateinit var storeAvaiableDrinkAdapter: StoreAvailableDrinkAdapter

    private var getStoreDetailInfo: StoreDetailResponse? = null
    private var isUsedToday: Boolean? = null
    private var couponInfo: MutableList<StoreCouponResponse>? = null

    private var hasShownTooltip = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]

        storeMainImageAdapter =
            StoreImagePagerAdapter(mainActivity, listOf()).apply {
                itemClickListener = object : StoreImagePagerAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {

                    }
                }
            }
        storeMenuImageAdapter = StoreMenuImageAdapter(mainActivity, listOf()).apply {
            itemClickListener = object : StoreMenuImageAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 메뉴판 확대 기능
                    val imageUrls = getStoreDetailInfo?.menuImageUrls?.map { it.imageUrl } ?: emptyList()
                    val bundle = Bundle().apply {
                        putString("storeName", getStoreDetailInfo?.storeName.toString())
                    }

                    // 전달할 Fragment 생성
                    var nextFragment = MenuFullscreenFragment(imageUrls, position).apply {
                        arguments = bundle
                    }

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
        storeAvaiableDrinkAdapter = StoreAvailableDrinkAdapter(mainActivity, listOf())

        observeViewModel()

        binding.run {
            viewPagerStoreImage.apply {
                adapter = storeMainImageAdapter
            }

            recyclerViewMenu.run {
                adapter = storeMenuImageAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }

            recyclerViewAvailableDrink.run {
                adapter = storeAvaiableDrinkAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }

            layoutCoupon.layoutCoupon.layoutCouponFrame.setOnClickListener {
                clickCoupon(0, couponInfo?.get(0)?.status.toString())
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hasShownTooltip = false
    }

    fun observeViewModel() {
        viewModel.run {
            storeDetailInfo.observe(viewLifecycleOwner) {
                getStoreDetailInfo = it

                checkButtonEnabled()
                applyStoreDetailInfo()
            }

            isUsed.observe(viewLifecycleOwner) {
                isUsedToday = it

                binding.run {
                    buttonMembership.visibility = View.VISIBLE
                    checkButtonEnabled()
                }
            }

            // 쿠폰 데이터
            storeCouponInfo.observe(viewLifecycleOwner) {
                couponInfo = it

                applyCouponDetailInfo(0, couponInfo?.get(0)?.status.toString())

            }
        }
    }

    fun applyCouponDetailInfo(position: Int, status: String) {
        binding.layoutCoupon.layoutCoupon.run {
            if (couponInfo == null) {
                binding.layoutCoupon.layoutStoreCoupon.visibility = View.GONE
            } else {
                binding.layoutCoupon.layoutStoreCoupon.visibility = View.VISIBLE

                textViewStoreName.text = couponInfo?.get(position)?.storeName ?: ""
                textViewCouponTitle.text = couponInfo?.get(position)?.title ?: ""
                textViewCouponDescription.text = couponInfo?.get(position)?.description ?: ""
                textViewCouponDate.text = "유효기간: ${couponInfo?.get(position)?.expirationDate}까지"

                if((couponInfo?.size ?: 0) > 1) {
                    textViewCouponDownload.text = "모두\n보기"
                    textViewCouponDownload.visibility = View.VISIBLE
                    imageViewCouponDownload.visibility = View.INVISIBLE
                } else {
                    when (status) {
                        // 쿠폰 다운로드 전
                        "NONE" -> {
                            textViewCouponDownload.visibility = View.INVISIBLE
                            imageViewCouponDownload.visibility = View.VISIBLE
                        }
                        // 쿠폰 다운로드 후 사용 X
                        "AVAILABLE" -> {
                            textViewCouponDownload.text = "쿠폰\n사용"
                            textViewCouponDownload.visibility = View.VISIBLE
                            imageViewCouponDownload.visibility = View.INVISIBLE
                        }
                        // 쿠폰 사용 완료
                        "USED" -> {
                            textViewCouponDownload.text = "사용\n완료"
                            textViewCouponDownload.visibility = View.VISIBLE
                            imageViewCouponDownload.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    fun applyStoreDetailInfo() {
        binding.run {
            toolbar.textViewTitle.text = getStoreDetailInfo?.storeName ?: ""

            // 가게 정보
            if(getStoreDetailInfo?.storeDescription.isNullOrEmpty()) {
                textViewStoreDescription.visibility = View.GONE
                dividerStoreInfo.visibility = View.GONE
            } else {
                textViewStoreDescription.visibility = View.VISIBLE
                dividerStoreInfo.visibility = View.VISIBLE
                textViewStoreDescription.text = getStoreDetailInfo?.storeDescription
            }

            if (getStoreDetailInfo?.storeDetailAddress?.isEmpty() == false) {
                textViewStoreAddress.text =
                    "${getStoreDetailInfo?.storeAddress ?: ""}, ${getStoreDetailInfo?.storeDetailAddress ?: ""}"
            } else {
                textViewStoreAddress.text = "${getStoreDetailInfo?.storeAddress ?: ""}"
            }
            textViewStoreIsOpen.text = getStoreDetailInfo?.isOpen
            textViewStoreCloseOrOpenTime.text = getStoreDetailInfo?.openingInfo
            textViewStoreAvailableDaysTitle.visibility = View.VISIBLE
            textViewStoreAvailableDaysValue.text =
                getStoreDetailInfo?.availableDays?.replace(" ", ",") ?: ""
            textViewStoreCall.text = getStoreDetailInfo?.storeTel
            if (getStoreDetailInfo?.instagramUrl?.isEmpty() == false) {
                layoutStoreInstagram.visibility = View.VISIBLE
                textViewStoreInstagram.paintFlags = textViewStoreInstagram.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                textViewStoreInstagram.run {
                    text = "@${getStoreDetailInfo?.instagramUrl}"
                    setOnClickListener {
                        mixpanel.track("move_detail_to_instagram", null)

                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.instagram.com/${getStoreDetailInfo?.instagramUrl}")
                        )
                        startActivity(intent)
                    }
                }
            } else {
                layoutStoreInstagram.visibility = View.GONE
            }

            // 가게 이미지
            val imageUrl = getStoreDetailInfo?.storeMainImageUrl
            if (imageUrl.isNullOrEmpty()) {
                // 기본 이미지 표시를 위해 null 대신 dummy 리스트 넘기기
                storeMainImageAdapter.updateList(listOf("basic"))
            } else {
                storeMainImageAdapter.updateList(listOf(imageUrl))
            }

            if(getStoreDetailInfo?.menuImageUrls?.isEmpty() == true) {
                textViewMenu.visibility = View.GONE
                recyclerViewMenu.visibility = View.GONE
            } else {
                textViewMenu.visibility = View.VISIBLE
                recyclerViewMenu.visibility = View.VISIBLE

                storeMenuImageAdapter.updateList(
                    getStoreDetailInfo?.menuImageUrls?.map { it.imageUrl }
                )
            }

            storeAvaiableDrinkAdapter.updateList(getStoreDetailInfo?.availableDrinkImageUrls)
        }
    }

    fun clickCoupon(position: Int, status: String) {
        if((couponInfo?.size ?: 0) > 1) {
            // 쿠폰 모두보기 bottom sheet
            val couponBottomSheet = CouponListBottomSheetFragment(mainActivity, couponInfo)

            couponBottomSheet.apply {
                clickListener = object : CouponListBottomSheetFragment.OnCouponClickListener {
                    override fun onBottomSheetClicked(position: Int, type: String) {
                        useCoupon(position, type)
                    }
                }
            }

            couponBottomSheet.show(childFragmentManager, couponBottomSheet.tag)
        } else {
            useCoupon(position, status)
        }
    }

    fun useCoupon(position: Int, status: String) {
        when (status) {
            "NONE" -> {
                mixpanel.track("click_detail_coupon_download", null)

                // (쿠폰 다운로드 전) 쿠폰 다운로드
                viewModel.downloadCoupon(
                    mainActivity,
                    couponInfo?.get(position)?.id ?: 0L,
                    arguments?.getLong("storeId", 0) ?: 0
                ) {
                    BasicToast.showBasicToast(requireContext(), "쿠폰 다운로드가 완료되었어요.", R.drawable.ic_check, binding.buttonMembership)
                }
            }

            "AVAILABLE" -> {
                mixpanel.track("move_detail_to_coupon_use", null)

                // (쿠폰 다운로드 후 사용 X) 쿠폰 사용 화면으로 이동
                val bundle = Bundle().apply {
                    putString("storeName", getStoreDetailInfo?.storeName.toString())
                    putLong("couponId", couponInfo?.get(position)?.id ?: 0)
                    putString("couponTitle", couponInfo?.get(position)?.title)
                    putString("couponDescription", couponInfo?.get(position)?.description)
                    putString("couponDate", couponInfo?.get(position)?.expirationDate)
                }

                // 전달할 Fragment 생성
                var nextFragment = StoreCouponFragment().apply {
                    arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, nextFragment)
                    .addToBackStack(null)
                    .commit()
            }

            "USED" -> {
                // (쿠폰 사용 완료)
            }
        }
    }

    fun checkButtonEnabled() {
        if (isUsedToday != null && getStoreDetailInfo != null) {
            binding.run {
                if (isUsedToday == true) {
                    buttonMembership.run {
                        isEnabled = false
                        text = "멤버십 사용 완료"
                    }
                } else {
                    if (getStoreDetailInfo?.isAvailable == true) {
                        if (InfoManager(mainActivity).getIsSubscribe() == true) {
                            buttonMembership.run {
                                text = "멤버십 사용하기"
                                isEnabled = true
                                setOnClickListener {
                                    mixpanel.track("move_select_to_membership_use", null)

                                    mainActivity.supportFragmentManager.beginTransaction()
                                        .replace(
                                            R.id.fragmentContainerView_main,
                                            StoreMembershipSelectFragment()
                                        )
                                        .addToBackStack("membership")
                                        .commit()
                                }
                            }
                        } else {
                            buttonMembership.run {
                                text = "멤버십 구독하러 가기"
                                setOnClickListener {
                                    mixpanel.track("move_select_to_subscribe", null)

                                    mainActivity.supportFragmentManager.beginTransaction()
                                        .replace(
                                            R.id.fragmentContainerView_main,
                                            SubscribePaymentFragment()
                                        )
                                        .addToBackStack(null)
                                        .commit()
                                }
                            }
                        }
                    } else {
                        if (!hasShownTooltip) {
                            showToolTip()
                            hasShownTooltip = true
                        }

                        buttonMembership.run {
                            isEnabled = false
                            text = "멤버십 사용 불가"
                            setOnClickListener {
                                showToolTip()
                            }
                        }
                    }
                }
            }
        }
    }

    fun showToolTip() {
        val balloon = Balloon.Builder(mainActivity)
            .setWidth(BalloonSizeSpec.WRAP)
//            .setWidthRatio(0.6f) // sets width as 60% of the horizontal screen's size.
            .setHeight(BalloonSizeSpec.WRAP)
            .setText("지금은 멤버십 사용이 불가능한 시간이에요")
            .setTextColorResource(R.color.gray9)
            .setTextSize(14f)
            .setTextTypeface(ResourcesCompat.getFont(mainActivity, R.font.pretendard_medium)!!)
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowOrientation(ArrowOrientation.BOTTOM)
            .setArrowSize(5)
            .setArrowPosition(0.5f)
            .setArrowColorResource(R.color.gray1)
            .setTextGravity(Gravity.CENTER)
            .setElevation(0)
            .setPaddingHorizontal(12)
            .setPaddingVertical(8)
            .setMarginVertical(10)
            .setCornerRadius(8f)
            .setBackgroundDrawableResource(R.drawable.background_tooltip_gray1)
            .setBalloonAnimation(BalloonAnimation.ELASTIC)
            .build()

        binding.buttonMembership.showAlignTop(balloon)

        Handler().postDelayed({
            balloon.dismiss()
        }, 3000)
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
            hideOrderHistoryButton(true)
        }

        if (!MyApplication.isLogin) {
            binding.buttonMembership.visibility = View.INVISIBLE
        } else {
            mainActivity.updateSubscriptionStatusIfNeeded(activity = mainActivity) { success ->
                if (success) {
                    // 구독 상태가 오늘 날짜 기준으로 정상 체크됨 → 이후 로직 실행
                    Log.d("SubscriptionCheck", "✅ 상태 확인 완료 후 이어서 작업 실행")

                    viewModel.getUsedMembership(mainActivity, arguments?.getLong("storeId", 0) ?: 0)
                    binding.buttonMembership.visibility = View.INVISIBLE
                } else {
                    Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                    mainActivity.goToLogin()
                }
            }
        }

        viewModel.getStoreDetail(mainActivity, arguments?.getLong("storeId", 0) ?: 0)

        binding.run {
            layoutCoupon.layoutStoreCoupon.visibility = View.GONE
            textViewStoreAvailableDaysTitle.visibility = View.INVISIBLE

            toolbar.run {
                buttonBack.setOnClickListener {
                    viewModel.storeDetailInfo.removeObservers(viewLifecycleOwner)
                    viewModel.storeDetailInfo.value = null
                    fragmentManager?.popBackStack()
                }
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.storeDetailInfo.removeObservers(viewLifecycleOwner)
                    viewModel.storeDetailInfo.value = null
                    fragmentManager?.popBackStack()
                }
            })
        }
    }

}