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
import com.project.drinkly.databinding.FragmentEventFriendInviteBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.event.adapter.DrinklyEventImageAdapter
import com.project.drinkly.ui.event.adapter.DrinklyEventListAdapter
import com.project.drinkly.ui.event.adapter.EventFriendInviteAdapter
import com.project.drinkly.ui.event.adapter.EventFriendInviteGuideAdapter
import com.project.drinkly.ui.event.viewModel.EventViewModel
import com.project.drinkly.ui.store.CouponListBottomSheetFragment

class EventFriendInviteFragment : Fragment() {

    lateinit var binding: FragmentEventFriendInviteBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: EventViewModel by lazy {
        ViewModelProvider(requireActivity())[EventViewModel::class.java]
    }

    lateinit var friendListAdapter: EventFriendInviteAdapter
    lateinit var guideAdapter: EventFriendInviteGuideAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEventFriendInviteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        binding.run {
            recyclerViewFriend.run {
                adapter = friendListAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            }

            recyclerViewGuide.run {
                adapter = guideAdapter
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            buttonInvite.setOnClickListener {
                // 초대 bottom sheet
                viewModel.getEventFriendInviteCode(mainActivity) { inviteCode ->
                    val bottomSheet = EventFriendInviteBottomSheetFragment(mainActivity, inviteCode)

                    bottomSheet.show(childFragmentManager, bottomSheet.tag)
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
        friendListAdapter = EventFriendInviteAdapter(
            mainActivity,
            viewModel.eventFriendInviteHistory.value?.nickname as MutableList<String>?
        )

        guideAdapter = EventFriendInviteGuideAdapter(
            mainActivity,
            resources.getTextArray(R.array.event_guide_friend_invite).map { it.toString() }
        )
    }

    fun observeViewModel() {
        viewModel.run {
            eventFriendInviteHistory.observe(viewLifecycleOwner) {
                binding.run {
                    textViewFriendNumber.text = "${it}명"

                    friendListAdapter.updateList(it?.nickname as MutableList<String>?)
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

        viewModel.getEventFriendInviteHistory(mainActivity)

        binding.toolbar.run {
            textViewHead.text = "이벤트"

            buttonBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }
}