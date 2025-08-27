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

class EventFriendInviteFragment : Fragment() {

    lateinit var binding: FragmentEventFriendInviteBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: EventViewModel by lazy {
        ViewModelProvider(requireActivity())[EventViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEventFriendInviteBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        binding.run {
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        initView()
    }

    fun initAdapter() {
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