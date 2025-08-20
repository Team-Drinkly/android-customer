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
import com.project.drinkly.databinding.FragmentDrinklyEventListBinding
import com.project.drinkly.databinding.FragmentGeneralEventListBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.event.adapter.DrinklyEventListAdapter
import com.project.drinkly.ui.event.adapter.GeneralEventListAdapter
import com.project.drinkly.ui.event.viewModel.EventViewModel

class GeneralEventListFragment : Fragment() {

    lateinit var binding: FragmentGeneralEventListBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: EventViewModel by lazy {
        ViewModelProvider(requireActivity())[EventViewModel::class.java]
    }

    lateinit var generalEventListAdapter: GeneralEventListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGeneralEventListBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        initAdapter()

        binding.run {
            recyclerViewEvent.apply {
                adapter = generalEventListAdapter
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
        generalEventListAdapter = GeneralEventListAdapter(
            mainActivity,
            viewModel.generalEventList.value
        ).apply {
            itemClickListener = object : GeneralEventListAdapter.OnItemClickListener {
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