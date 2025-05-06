package com.project.drinkly.ui.store

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.R
import com.project.drinkly.api.response.coupon.StoreCouponResponse
import com.project.drinkly.api.response.store.StoreDetailResponse
import com.project.drinkly.databinding.FragmentStoreDetailBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.adapter.StoreAvailableDrinkAdapter
import com.project.drinkly.ui.store.adapter.StoreImagePagerAdapter
import com.project.drinkly.ui.store.adapter.StoreMenuImageAdapter
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.ui.subscribe.SubscribePaymentFragment
import com.project.drinkly.util.MyApplication
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.showAlignStart
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
    private var couponInfo: StoreCouponResponse? = null

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
                    // 메뉴판 확대 기능

                }
            }
        }
        storeMenuImageAdapter = StoreMenuImageAdapter(mainActivity, listOf())
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

            layoutCoupon.layoutCouponFrame.setOnClickListener {
                when(couponInfo?.status) {
                    "NONE" -> {
                        // (쿠폰 다운로드 전) 쿠폰 다운로드
                        viewModel.downloadCoupon(mainActivity, couponInfo?.id ?: 0L, arguments?.getLong("storeId", 0) ?: 0)
                    }
                    "AVAILABLE" -> {
                        // (쿠폰 다운로드 후 사용 X) 쿠폰 사용 화면으로 이동
                        val bundle = Bundle().apply {
                            putString("storeName", getStoreDetailInfo?.storeName.toString())
                            putLong("couponId", couponInfo?.id ?: 0)
                            putString("couponTitle", couponInfo?.title)
                            putString("couponDescription", couponInfo?.description)
                            putString("couponDate", couponInfo?.expirationDate)
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

                binding.run {
                    toolbar.textViewTitle.text = getStoreDetailInfo?.storeName ?: ""

                    // 가게 정보
                    textViewStoreDescription.text = getStoreDetailInfo?.storeDescription.toString()

                    if(getStoreDetailInfo?.storeDetailAddress?.isEmpty() == false) {
                        textViewStoreAddress.text = "${getStoreDetailInfo?.storeAddress}, ${getStoreDetailInfo?.storeDetailAddress}"
                    } else {
                        textViewStoreAddress.text = "${getStoreDetailInfo?.storeAddress}"
                    }
                    textViewStoreIsOpen.text = getStoreDetailInfo?.isOpen
                    textViewStoreCloseOrOpenTime.text = getStoreDetailInfo?.openingInfo
                    textViewStoreAvailableDaysValue.text = getStoreDetailInfo?.availableDays?.replace(" ", ",") ?: ""
                    textViewStoreCall.text = getStoreDetailInfo?.storeTel
                    if(getStoreDetailInfo?.instagramUrl?.isEmpty() == false) {
                        textViewStoreInstagram.run {
                            text = "@${getStoreDetailInfo?.instagramUrl}"
                            setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/${getStoreDetailInfo?.instagramUrl}"))
                                startActivity(intent)
                            }
                        }
                    } else {
                        layoutStoreInstagram.visibility = View.GONE
                    }

                    // 가게 이미지
                    getStoreDetailInfo?.storeMainImageUrl?.let { imageUrl ->
                        storeMainImageAdapter.updateList(listOf(imageUrl))
                    }

                    storeMenuImageAdapter.updateList(
                        getStoreDetailInfo?.menuImageUrls?.map { it.imageUrl }
                    )

                    storeAvaiableDrinkAdapter.updateList(getStoreDetailInfo?.availableDrinkImageUrls)
                }
            }

            isUsed.observe(viewLifecycleOwner) {
                isUsedToday = it

                binding.run {
                    buttonMembership.visibility = View.VISIBLE
                    checkButtonEnabled()
                }
            }

            storeCouponInfo.observe(viewLifecycleOwner) {
                couponInfo = it

                binding.layoutCoupon.run {
                    if(couponInfo == null) {
                        layoutCouponFrame.visibility = View.GONE
                    } else {
                        layoutCouponFrame.visibility = View.VISIBLE

                        textViewCouponTitle.text = couponInfo?.title
                        textViewCouponDescription.text = couponInfo?.description
                        textViewCouponDate.text = "유효기간: ${couponInfo?.expirationDate}까지"

                        when(couponInfo?.status) {
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
    }

    fun checkButtonEnabled() {
        if(isUsedToday != null && getStoreDetailInfo != null) {
            binding.run {
                if(isUsedToday == true) {
                    buttonMembership.run {
                        isEnabled = false
                        text = "멤버십 사용 완료"
                    }
                } else {
                    if(getStoreDetailInfo?.isAvailable == true) {
                        if(MyApplication.isSubscribe) {
                            buttonMembership.run {
                                text = "멤버십 사용하기"
                                isEnabled = true
                                setOnClickListener {
                                    mainActivity.supportFragmentManager.beginTransaction()
                                        .replace(R.id.fragmentContainerView_main, StoreMembershipSelectFragment())
                                        .addToBackStack("membership")
                                        .commit()
                                }
                            }
                        } else {
                            buttonMembership.run {
                                text = "멤버십 구독하러 가기"
                                setOnClickListener {
                                    mainActivity.supportFragmentManager.beginTransaction()
                                        .replace(R.id.fragmentContainerView_main, SubscribePaymentFragment())
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
            .setTextTypeface(ResourcesCompat.getFont(mainActivity,R.font.pretendard_medium)!!)
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
            .setBackgroundDrawableResource(R.drawable.background_tootip_gray1)
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
        }

        viewModel.getStoreDetail(mainActivity, arguments?.getLong("storeId", 0) ?: 0)

        binding.run {
            layoutCoupon.layoutCouponFrame.visibility = View.GONE

            if(!MyApplication.isLogin) {
                buttonMembership.visibility = View.INVISIBLE
            } else {
                viewModel.getUsedMembership(mainActivity, arguments?.getLong("storeId", 0) ?: 0)
                viewModel.getStoreCoupon(mainActivity, arguments?.getLong("storeId", 0) ?: 0)
                buttonMembership.visibility = View.INVISIBLE
            }

            toolbar.run {
                buttonBack.setOnClickListener {
                    viewModel.storeDetailInfo.removeObservers(viewLifecycleOwner)
                    viewModel.storeDetailInfo.value = null
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}