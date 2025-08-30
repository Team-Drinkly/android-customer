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
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentEventDetailBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.event.adapter.DrinklyEventImageAdapter
import com.project.drinkly.ui.event.adapter.DrinklyEventListAdapter
import com.project.drinkly.ui.event.adapter.MainGeneralEventListAdapter
import com.project.drinkly.ui.event.viewModel.EventViewModel

class EventDetailFragment : Fragment() {

    lateinit var binding: FragmentEventDetailBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: EventViewModel by lazy {
        ViewModelProvider(requireActivity())[EventViewModel::class.java]
    }

    lateinit var drinklyEventImageAdapter: DrinklyEventImageAdapter

    var eventUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEventDetailBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()
        observeViewModel()

        binding.run {
            recyclerViewEventImage.apply {
                adapter = drinklyEventImageAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }

            buttonEvent.setOnClickListener {
                // 이벤트 참여 링크 연동
                var intent = Intent(Intent.ACTION_VIEW, Uri.parse(eventUrl))
                startActivity(intent)
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
            eventDetail.observe(viewLifecycleOwner) {
                if(it == null) return@observe

                eventUrl = it.redirectUrl
                drinklyEventImageAdapter.updateList(it.eventImagePaths as MutableList<String>?)

                binding.run {
                    if(it.redirectUrl != null) {
                        buttonEvent.visibility = View.VISIBLE
                    } else {
                        buttonEvent.visibility = View.GONE
                    }
                    textViewEventTitle.text = it.title
                    textViewPeriod.text = "${it.startDate} ~ ${it.endDate}"
                    textViewBenefit.text = it.benefit
                    textViewDetailInfo.text = it.description
                    textViewTagType.text = it.status
                    textViewTagDay.text = it.dDay
                    when(it.status) {
                        "진행중" -> {
                            textViewTagType.run {
                                setTextColor(resources.getColor(R.color.white))
                                setBackgroundResource(R.drawable.background_primary50_radius6)
                            }
                            textViewTagDay.setTextColor(resources.getColor(R.color.primary_50))
                        }
                        "마감" -> {
                            textViewTagType.run {
                                setTextColor(resources.getColor(R.color.gray6))
                                setBackgroundResource(R.drawable.background_gray8_radius6)
                            }
                            textViewTagDay.setTextColor(resources.getColor(R.color.gray7))
                        }
                    }
                }
            }
        }
    }

    fun initAdapter() {
        drinklyEventImageAdapter = DrinklyEventImageAdapter(
            mainActivity,
            viewModel.eventDetail.value?.eventImagePaths as MutableList<String>?
        )
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMapButton(true)
            hideMyLocationButton(true)
        }

        viewModel.getEventDetail(mainActivity, arguments?.getLong("eventId") ?: 0)

        binding.toolbar.run {
            textViewHead.text = "이벤트"

            buttonBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

}