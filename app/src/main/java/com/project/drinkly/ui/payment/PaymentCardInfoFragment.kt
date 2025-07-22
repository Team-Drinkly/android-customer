package com.project.drinkly.ui.payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.project.drinkly.BuildConfig
import com.project.drinkly.R
import com.project.drinkly.databinding.FragmentPaymentCardInfoBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.util.EncryptManager

class PaymentCardInfoFragment : Fragment() {

    lateinit var binding: FragmentPaymentCardInfoBinding
    lateinit var mainActivity: MainActivity

    private var cardNumber = ""
    private var expiredDate = ""
    private var password = ""
    private var birth = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPaymentCardInfoBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity

        binding.run {
            scrollView.setOnTouchListener { v, event ->
                mainActivity.hideKeyboard()
                false
            }

            editTextCardNumber.run {
                addTextChangedListener(object : TextWatcher {
                    private var currentText = ""
                    private var isFormatting = false

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        checkEnabled()
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        checkEnabled()
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (isFormatting) return

                        val digitsOnly = s.toString().filter { it.isDigit() }
                        val maxLength = 16
                        val limited = digitsOnly.take(maxLength)

                        val formatted = buildString {
                            for (i in limited.indices) {
                                append(limited[i])
                                if (i == 3 || i == 7 || i == 11) append("-")
                            }
                        }

                        if (formatted == currentText) return

                        isFormatting = true
                        currentText = formatted
                        val selectionIndex = formatted.length

                        setText(formatted)
                        setSelection(selectionIndex.coerceAtMost(formatted.length))
                        isFormatting = false

                        cardNumber = limited // 하이픈 없는 숫자만 저장
                        checkEnabled()
                    }
                })
            }

            editTextExpiredDate.run {
                addTextChangedListener(object : TextWatcher {
                    private var currentText = ""
                    private var isFormatting = false

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        checkEnabled()
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        checkEnabled()
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (isFormatting) return

                        val digitsOnly = s.toString().filter { it.isDigit() }
                        val maxLength = 16
                        val limited = digitsOnly.take(maxLength)

                        val formatted = buildString {
                            for (i in limited.indices) {
                                append(limited[i])
                                if (i == 1) append("/")
                            }
                        }

                        if (formatted == currentText) return

                        isFormatting = true
                        currentText = formatted
                        val selectionIndex = formatted.length

                        setText(formatted)
                        setSelection(selectionIndex.coerceAtMost(formatted.length))
                        isFormatting = false

                        expiredDate = limited // 하이픈 없는 숫자만 저장
                        checkEnabled()
                    }
                })
            }

            editTextPassword.addTextChangedListener {
                password = it.toString()

                checkEnabled()
            }

            editTextBirth.addTextChangedListener {
                birth = it.toString()

                checkEnabled()
            }

            buttonRegister.setOnClickListener {
                val encData = EncryptManager.encryptCardInfo(
                    cardNo = cardNumber,
                    expYear = expiredDate.substring(2..3),
                    expMonth = expiredDate.substring(0..1),
                    idNo = birth,
                    cardPw = password,
                    secretKey = BuildConfig.NICE_PAYMENT_KEY
                )
                println(encData)
            }


        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initView()
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

        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView.rootView) { _, insets ->
            val sysBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            updateViewPositionForKeyboard(imeHeight - sysBarInsets.bottom)
            insets
        }

        binding.run {
            toolbar.run {
                textViewTitle.text = "카드 정보 입력"
                buttonBack.setOnClickListener {
                    fragmentManager?.popBackStack()
                }
            }
        }
    }

    fun checkEnabled() {
        binding.run {
            if(cardNumber.length == 16 && expiredDate.length == 4 && password.length == 2 && (birth.length == 6 || birth.length == 10)) {
                buttonRegister.isEnabled = true
            } else {
                buttonRegister.isEnabled = false
            }
        }
    }

    private fun updateViewPositionForKeyboard(keyboardHeight: Int) {
        val layoutParams =
            binding.scrollView.layoutParams as ConstraintLayout.LayoutParams
        if (keyboardHeight > 0) {
            layoutParams.bottomMargin = keyboardHeight
        } else {
            layoutParams.bottomMargin = 0
        }
        binding.scrollView.layoutParams = layoutParams
    }
}