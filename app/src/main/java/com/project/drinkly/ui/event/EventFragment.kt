package com.project.drinkly.ui.event

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentEventBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.event.viewModel.EventViewModel
import com.project.drinkly.ui.payment.PaymentCardInfoFragment
import com.project.drinkly.ui.payment.viewModel.PaymentViewModel

class EventFragment : Fragment() {

    lateinit var binding: FragmentEventBinding
    lateinit var mainActivity: MainActivity
    private val viewModel: EventViewModel by lazy {
        ViewModelProvider(requireActivity())[EventViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentEventBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        observeViewModel()

        binding.run {
            layoutDrinklyEventMore.setOnClickListener {
                // 드링클리 이벤트 목록 화면
                mainActivity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView_main, DrinklyEventListFragment())
                    .addToBackStack(null)
                    .commit()
            }

            layoutGeneralEventMore.setOnClickListener {

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
    }

}