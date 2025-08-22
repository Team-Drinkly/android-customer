package com.project.drinkly.ui.event

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
import com.project.drinkly.databinding.FragmentDrinklyEventListBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.event.adapter.DrinklyEventListAdapter
import com.project.drinkly.ui.event.viewModel.EventViewModel
import com.project.drinkly.ui.onboarding.LoginFragment

class DrinklyEventListFragment : Fragment() {

    lateinit var binding: FragmentDrinklyEventListBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: EventViewModel by lazy {
        ViewModelProvider(requireActivity())[EventViewModel::class.java]
    }

    lateinit var drinklyEventListAdapter: DrinklyEventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDrinklyEventListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        binding.run {
            recyclerViewEvent.apply {
                adapter = drinklyEventListAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        initView()
    }

    fun initAdapter() {
        drinklyEventListAdapter = DrinklyEventListAdapter(
            mainActivity,
            viewModel.drinklyEventList.value
        ).apply {
            itemClickListener = object : DrinklyEventListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val bundle = Bundle().apply { putLong("eventId", viewModel.drinklyEventList.value?.get(position)?.eventId ?: 0) }

                    var nextFragment = EventDetailFragment().apply {
                        arguments = bundle
                    }

                    mainActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, nextFragment)
                        .addToBackStack(null)
                        .commit()
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

        binding.toolbar.run {
            textViewHead.text = "이벤트"

            buttonBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

}