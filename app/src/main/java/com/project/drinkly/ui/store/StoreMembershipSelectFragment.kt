package com.project.drinkly.ui.store

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.R
import com.project.drinkly.api.InfoManager
import com.project.drinkly.api.response.store.StoreDetailResponse
import com.project.drinkly.databinding.FragmentStoreMembershipSelectBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.onboarding.LoginFragment
import com.project.drinkly.ui.store.adapter.StoreAvailableDrinkAdapter
import com.project.drinkly.ui.store.adapter.StoreAvailableDrinkSelectAdapter
import com.project.drinkly.ui.store.adapter.StoreListAdapter
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel
import com.project.drinkly.util.MyApplication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoreMembershipSelectFragment : Fragment() {

    lateinit var binding: FragmentStoreMembershipSelectBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StoreViewModel

    private var getStoreDetailInfo: StoreDetailResponse? = null
    lateinit var storeAvaiableDrinkAdapter: StoreAvailableDrinkSelectAdapter

    var selectedPosition = -1

    private var isExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentStoreMembershipSelectBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]

        initAdapter()
        observeViewModel()

        getStoreDetailInfo = viewModel.storeDetailInfo.value

        binding.run {
            recyclerViewGetMembershipDrink.run {
                adapter = storeAvaiableDrinkAdapter
                layoutManager = GridLayoutManager(context, 2)
            }

            layoutTitle.setOnClickListener {
                isExpanded = !isExpanded
                if(isExpanded) {
                    textViewMembershipGuide.visibility = View.VISIBLE
                    imageViewArrow.setImageResource(R.drawable.ic_arrow_up)
                } else {
                    textViewMembershipGuide.visibility = View.GONE
                    imageViewArrow.setImageResource(R.drawable.ic_arrow_down)
                }
            }

            buttonNext.setOnClickListener {
                mixpanel.track("click_membership_use", null)
                // 멤버십 사용
                viewModel.useMembership(mainActivity, getStoreDetailInfo?.storeId ?: 0, getStoreDetailInfo?.availableDrinkImageUrls?.get(selectedPosition)?.imageId ?: 0, getStoreDetailInfo?.availableDrinkImageUrls?.get(selectedPosition)?.description.toString()) {
                    val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREAN)
                    val currentDate = Date() // 현재 시간 가져오기

                    val bundle = Bundle().apply {
                        putString("usedTime",  dateFormat.format(currentDate).toString())
                        putString("storeName",  getStoreDetailInfo?.storeName)
                        putString("availableDrinkName",  getStoreDetailInfo?.availableDrinkImageUrls?.get(selectedPosition)?.description.toString())
                        putString("availableDrinkImage",  getStoreDetailInfo?.availableDrinkImageUrls?.get(selectedPosition)?.imageUrl)
                    }

                    // 전달할 Fragment 생성
                    var nextFragment = StoreMembershipFragment().apply {
                        arguments = bundle
                    }

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, nextFragment)
                        .commit()
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        storeAvaiableDrinkAdapter = StoreAvailableDrinkSelectAdapter(
            mainActivity,
            getStoreDetailInfo?.availableDrinkImageUrls
        ).apply {
            itemClickListener = object : StoreAvailableDrinkSelectAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    selectedPosition = position

                    binding.buttonNext.isEnabled = true
                }
            }
        }
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)

            updateSubscriptionStatusIfNeeded(activity = mainActivity) { success ->
                if (success) {
                    // 구독 상태가 오늘 날짜 기준으로 정상 체크됨 → 이후 로직 실행
                    Log.d("SubscriptionCheck", "✅ 상태 확인 완료 후 이어서 작업 실행")

                } else {
                    Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                    mainActivity.goToLogin()
                }
            }
        }

        binding.run {
            toolbar.run {
                textViewHead.text = "멤버십 사용"
                buttonBack.setOnClickListener {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            storeDetailInfo.observe(viewLifecycleOwner) { storeDetail ->
                if (storeDetail != null) {
                    getStoreDetailInfo = storeDetail

                    binding.run {
                        textViewStoreName.text = storeDetail.storeName
                        textViewStoreIsOpen.text = storeDetail.isOpen
                        textViewStoreCloseOrOpenTime.text = storeDetail.openingInfo
                    }

                    storeAvaiableDrinkAdapter.updateList(storeDetail.availableDrinkImageUrls)
                }
            }
        }
    }
}