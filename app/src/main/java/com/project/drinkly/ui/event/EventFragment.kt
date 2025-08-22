package com.project.drinkly.ui.event

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentEventBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.event.adapter.MainGeneralEventListAdapter
import com.project.drinkly.ui.event.viewModel.EventViewModel

class EventFragment : Fragment() {

    lateinit var binding: FragmentEventBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: EventViewModel by lazy {
        ViewModelProvider(requireActivity())[EventViewModel::class.java]
    }

    lateinit var mainGeneralEventListAdapter: MainGeneralEventListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEventBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            layoutDrinklyEvent.layoutEvent.setOnClickListener {
                val bundle = Bundle().apply { putLong("eventId", viewModel.drinklyEventList.value?.get(0)?.eventId ?: 0) }

                var nextFragment = EventDetailFragment().apply {
                    arguments = bundle
                }

                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, nextFragment)
                    .addToBackStack(null)
                    .commit()
            }

            layoutDrinklyEventMore.setOnClickListener {
                // 드링클리 이벤트 목록 화면
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, DrinklyEventListFragment())
                    .addToBackStack(null)
                    .commit()
            }

            layoutGeneralEventMore.setOnClickListener {
                // 외부 이벤트 목록 화면
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, GeneralEventListFragment())
                    .addToBackStack(null)
                    .commit()
            }

            recyclerViewGeneralEvent.apply {
                adapter = mainGeneralEventListAdapter
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
            drinklyEventList.observe(viewLifecycleOwner) {
                var event = it?.get(0) ?: return@observe
                binding.run {
                    layoutDrinklyEvent.run {
                        Glide.with(requireContext()).load(event.thumbnailPath).into(imageViewEvent)
                        textViewEventName.text = event.title
                        textViewTagType.text = event.status
                        textViewTagDay.text = event.dDay
                        when(event.status) {
                            "진행중" -> {
                                textViewTagType.run {
                                    setTextColor(resources.getColor(R.color.white))
                                    setBackgroundResource(R.drawable.background_primary50_radius6)
                                }
                                textViewTagDay.setTextColor(resources.getColor(R.color.primary_50))
                                layoutExpired.visibility = View.GONE
                            }
                            "마감" -> {
                                textViewTagType.run {
                                    setTextColor(resources.getColor(R.color.gray6))
                                    setBackgroundResource(R.drawable.background_gray8_radius6)
                                }
                                textViewTagDay.setTextColor(resources.getColor(R.color.gray7))
                                layoutExpired.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }

            generalEventList.observe(viewLifecycleOwner) {
                mainGeneralEventListAdapter.updateList(it)
            }
        }
    }

    fun initAdapter() {
        mainGeneralEventListAdapter = MainGeneralEventListAdapter(
            mainActivity,
            viewModel.generalEventList.value
        ).apply {
            itemClickListener = object : MainGeneralEventListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // 외부 행사 링크
                    var intent = Intent(Intent.ACTION_VIEW, Uri.parse(viewModel.generalEventList.value?.get(position)?.redirectUrl))
                    startActivity(intent)
                }
            }
        }
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(false)
            hideMapButton(true)
            hideMyLocationButton(true)
        }

        binding.toolbar.textViewHead.text = "이벤트"

        viewModel.getDrinklyEventList(mainActivity)
        viewModel.getGeneralEventList(mainActivity)
    }

}