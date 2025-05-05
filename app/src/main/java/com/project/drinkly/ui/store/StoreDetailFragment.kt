package com.project.drinkly.ui.store

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[StoreViewModel::class.java]

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
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun observeViewModel() {
        viewModel.run {
            storeDetailInfo.observe(viewLifecycleOwner) {
                getStoreDetailInfo = it

                binding.run {
                    toolbar.textViewTitle.text = getStoreDetailInfo?.storeName.toString()

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
                    storeMainImageAdapter.updateList(
                        listOf(getStoreDetailInfo?.storeMainImageUrl!!)
                    )

                    storeMenuImageAdapter.updateList(
                        getStoreDetailInfo?.menuImageUrls?.map { it.imageUrl }
                    )

                    storeAvaiableDrinkAdapter.updateList(getStoreDetailInfo?.availableDrinkImageUrls)
                }
            }

            isUsed.observe(viewLifecycleOwner) {
                binding.run {
                    buttonMembership.visibility = View.VISIBLE

                    if(it == true) {
                        buttonMembership.run {
                            isEnabled = false
                            text = "멤버십 사용 완료"
                        }
                    } else {
                        if(getStoreDetailInfo?.isAvailable == true) {
                            if(MyApplication.isSubscribe) {
                                buttonMembership.run {
                                    text = "멤버십 사용하기"
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
                            showToolTip()

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
    }

    fun showToolTip() {
        val balloon = Balloon.Builder(mainActivity)
//                .setWidth(BalloonSizeSpec.WRAP)
            .setWidthRatio(0.6f) // sets width as 60% of the horizontal screen's size.
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

        viewModel.getStoreDetail(mainActivity, arguments?.getLong("storeId", 0)!!)

        binding.run {
            if(!MyApplication.isLogin) {
                buttonMembership.visibility = View.INVISIBLE
            } else {
                viewModel.getUsedMembership(mainActivity, arguments?.getLong("storeId", 0)!!)
                buttonMembership.visibility = View.INVISIBLE
            }

            toolbar.run {
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

}