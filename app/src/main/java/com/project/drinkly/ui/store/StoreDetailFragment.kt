package com.project.drinkly.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.api.response.store.StoreDetailResponse
import com.project.drinkly.databinding.FragmentStoreDetailBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.adapter.StoreAvailableDrinkAdapter
import com.project.drinkly.ui.store.adapter.StoreImagePagerAdapter
import com.project.drinkly.ui.store.adapter.StoreMenuImageAdapter
import com.project.drinkly.ui.store.viewModel.StoreViewModel

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
                    textViewStoreAddress.text = "${getStoreDetailInfo?.storeAddress}, ${getStoreDetailInfo?.storeDetailAddress}"
                    textViewStoreIsOpen.text = getStoreDetailInfo?.isOpen
                    textViewStoreCloseOrOpenTime.text = getStoreDetailInfo?.openingInfo
                    textViewStoreAvailableDaysValue.text = getStoreDetailInfo?.availableDays?.replace(" ", ",") ?: ""
                    textViewStoreCall.text = getStoreDetailInfo?.storeTel
                    if(getStoreDetailInfo?.instagramUrl != null) {
                        textViewStoreInstagram.text = getStoreDetailInfo?.instagramUrl
//                        textViewStoreInstagram.text = it.instagramUrl.substring(26, it.instagramUrl.length - 1)
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
        }
    }

    fun initView() {

        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
        }

        viewModel.getStoreDetail(mainActivity, arguments?.getLong("storeId", 0)!!)

        binding.toolbar.run {
            buttonBack.setOnClickListener {
                fragmentManager?.popBackStack()
            }
        }
    }

}