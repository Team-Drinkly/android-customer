package com.project.drinkly.ui.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.drinkly.R
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.databinding.FragmentStoreListBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.store.adapter.StoreListAdapter
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel
import com.project.drinkly.util.MyApplication

class StoreListFragment : Fragment() {

    lateinit var binding: FragmentStoreListBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: StoreViewModel

    lateinit var storeListAdapter: StoreListAdapter
    var getStoreList = mutableListOf<StoreListResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[StoreViewModel::class.java]

        initAdapter()
        observeViewModel()

        viewModel.getStoreList(mainActivity, MyApplication.latitude, MyApplication.longitude, MyApplication.radius, null)

        binding.run {
            recyclerViewStoreList.apply {
                adapter = storeListAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }

        mainActivity.binding.buttonList.run {
            setImageResource(R.drawable.ic_map)
            setOnClickListener {
                mixpanel.track("click_home_listmap", null)

                // 제휴업체 - 지도 화면으로 전환
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, StoreMapFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        mainActivity.run {
            hideBottomNavigation(false)
            hideMyLocationButton(true)
            hideOrderHistoryButton(true)
            hideMapButton(false)
        }
    }

    fun initAdapter() {
        storeListAdapter = StoreListAdapter(
            mainActivity,
            getStoreList
        ).apply {
            itemClickListener = object : StoreListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    mixpanel.track("move_list_to_detail", null)

                    // 제휴업체 - 세부 화면으로 전환
                    var nextFragment = StoreDetailFragment()

                    val bundle = Bundle().apply { putLong("storeId", getStoreList[position].id) }

                    // 전달할 Fragment 생성
                    nextFragment = StoreDetailFragment().apply {
                        arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                    }
                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, nextFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    fun observeViewModel() {
        viewModel.run {
            storeInfo.observe(viewLifecycleOwner) {
                getStoreList = it

                storeListAdapter.updateList(getStoreList)
            }
        }
    }
}