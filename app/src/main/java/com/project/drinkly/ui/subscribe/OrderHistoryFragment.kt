package com.project.drinkly.ui.subscribe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.R
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.api.response.subscribe.OrderHistory
import com.project.drinkly.databinding.FragmentOrderHistoryBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.onboarding.viewModel.LoginViewModel
import com.project.drinkly.ui.store.StoreDetailFragment
import com.project.drinkly.ui.store.StoreMembershipFragment
import com.project.drinkly.ui.store.adapter.StoreListAdapter
import com.project.drinkly.ui.subscribe.adapter.OrderHistoryAdapter
import com.project.drinkly.ui.subscribe.viewModel.SubscribeViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

class OrderHistoryFragment : Fragment() {

    lateinit var binding: FragmentOrderHistoryBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: SubscribeViewModel by lazy {
        ViewModelProvider(requireActivity())[SubscribeViewModel::class.java]
    }

    lateinit var orderHistoryAdapter: OrderHistoryAdapter
    var getOrderHistory = mutableListOf<OrderHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOrderHistoryBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
    }

    fun initAdapter() {
        getOrderHistory = (viewModel.orderHistory.value?.drinksHistory ?: emptyList()).toMutableList()

        binding.run {
            if(getOrderHistory.isEmpty()) {
                layoutEmpty.visibility = View.VISIBLE
                recyclerViewOrderHistory.visibility = View.GONE
            } else {
                layoutEmpty.visibility = View.GONE
                recyclerViewOrderHistory.visibility = View.VISIBLE
            }

            orderHistoryAdapter = OrderHistoryAdapter(mainActivity, getOrderHistory).apply {
                itemClickListener = object : OrderHistoryAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        mixpanel.track("click_subscribe_history_detail", null)
                        
                        val bundle = Bundle().apply {
                            putString("storeName", getOrderHistory[position].storeName.toString())
                            putString("availableDrinkName", getOrderHistory[position].providedDrink.toString())
                            putString("usedTime", getOrderHistory[position].usageDate)
                            putString("availableDrinkImage", getOrderHistory[position].usageDate)
                            putBoolean("history", true)
                        }

                        val nextFragment = StoreMembershipFragment().apply {
                            arguments = bundle
                        }

                        mainActivity.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, nextFragment)
                            .addToBackStack("membership")
                            .commit()
                    }
                }
            }

            recyclerViewOrderHistory.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = orderHistoryAdapter
            }
        }
    }


    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
            hideOrderHistoryButton(true)

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
                textViewHead.text = "멤버십 사용 내역"
                buttonBack.setOnClickListener {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }
}