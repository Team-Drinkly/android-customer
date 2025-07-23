package com.project.drinkly.ui.onboarding.signUp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentSignUpNicknameBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.onboarding.viewModel.LoginViewModel
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel

class SignUpNickNameFragment : Fragment() {

    lateinit var binding: FragmentSignUpNicknameBinding
    lateinit var mainActivity: MainActivity
    lateinit var viewModel: LoginViewModel

    private val handler = Handler(Looper.getMainLooper())
    private var lastInputTime: Long = 0L
    private val delayMillis: Long = 3000 // 3초 동안 입력이 없으면 중복 확인 실행

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpNicknameBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        observeViewModel()

        binding.run {
            editTextNickname.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    editTextNickname.setBackgroundResource(R.drawable.background_edittext_default)
                    buttonNext.isEnabled = false
                    textViewNicknameDescription.visibility = View.GONE

                    handler.removeCallbacksAndMessages(null) // 기존 예약된 중복 확인 제거
                    lastInputTime = System.currentTimeMillis()

                    handler.postDelayed({
                        if (System.currentTimeMillis() - lastInputTime >= delayMillis) {
                            viewModel.checkNicknameDuplicate(mainActivity, editTextNickname.text.toString())
                        }
                    }, delayMillis)
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            editTextNickname.setOnEditorActionListener { textView, i, keyEvent ->
                // 닉네임 중복 확인
                viewModel.checkNicknameDuplicate(mainActivity, editTextNickname.text.toString())

                true
            }

            buttonNext.setOnClickListener {
                mixpanel.track("click_signup3", null)

                viewModel.signUp(mainActivity, editTextNickname.text.toString())
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
            isUsableNickName.observe(viewLifecycleOwner) {
                binding.run {
                    if(it) {
                        editTextNickname.setBackgroundResource(R.drawable.background_edittext_success)
                        buttonNext.isEnabled = true
                        textViewNicknameDescription.run {
                            visibility = View.VISIBLE
                            text = "사용할 수 있는 닉네임이에요."
                            setTextColor(resources.getColor(R.color.primary_50))
                        }
                    } else {
                        editTextNickname.setBackgroundResource(R.drawable.background_edittext_fail)
                        buttonNext.isEnabled = false
                        textViewNicknameDescription.run {
                            visibility = View.VISIBLE
                            text = "이미 존재하는 닉네임이에요."
                            setTextColor(resources.getColor(R.color.red))
                        }
                    }
                }
            }
        }
    }

    fun initView() {
        mainActivity.run {
            hideBottomNavigation(true)
            hideMyLocationButton(true)
            hideMapButton(true)
            hideOrderHistoryButton(true)
        }

        binding.run {
            textViewNicknameDescription.visibility = View.GONE

            toolbar.run {
                textViewHead.text = "정보 입력"
                buttonBack.setOnClickListener {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

}