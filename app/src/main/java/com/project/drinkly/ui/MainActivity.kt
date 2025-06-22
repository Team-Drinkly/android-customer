package com.project.drinkly.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentManager
import com.google.firebase.messaging.FirebaseMessaging
import com.project.drinkly.R
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.TokenUtil
import com.project.drinkly.databinding.ActivityMainBinding
import com.project.drinkly.ui.dialog.DialogEvent
import com.project.drinkly.ui.mypage.MypageFragment
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.ui.onboarding.LoginFragment
import com.project.drinkly.ui.store.StoreDetailFragment
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.ui.subscribe.SubscribeFragment
import com.project.drinkly.ui.subscribe.viewModel.SubscriptionChecker
import com.project.drinkly.ui.subscribe.viewModel.SubscriptionChecker.removeSubscriptionLastCheckedDate
import com.project.drinkly.util.GlobalApplication.Companion.mixpanel
import com.project.drinkly.util.MainUtil.setStatusBarTransparent
import com.project.drinkly.util.MyApplication
import com.project.drinkly.util.PreferenceUtil
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferenceManager: PreferenceUtil

    private var pendingPushStoreId: Long? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        this.setStatusBarTransparent()
        getKeyHash()
        MyApplication.preferences = PreferenceUtil(applicationContext)

        setFCMToken()
        setBottomNavigationView()
        handleNotificationIntent(intent)

        window.apply {
            //상태바 아이콘(true: 검정 / false: 흰색)
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars = false
        }

        setContentView(binding.root)
    }

    private fun handleNotificationIntent(intent: Intent) {
        when(intent.getStringExtra("type").toString()) {
            "COUPON" -> {
                mixpanel.track("click_push_alarm_coupon", null)

                val storeId = intent.getLongExtra("storeId", 0L)
                if (storeId != 0L) {
                    pendingPushStoreId = storeId
                }
            }
            "PROMOTION" -> {
                mixpanel.track("click_push_alarm_promotion", null)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // 새로운 Intent 설정

        intent.let { handleNotificationIntent(it) } // 앱 실행 중 알림 클릭 처리
    }

    private fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    mixpanel.track("click_tab_home", null)

                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, StoreMapFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.menu_subscribe -> {
                    if(MyApplication.isLogin) {
                        updateSubscriptionStatusIfNeeded(activity = this) { success ->
                            if (success) {
                                // 구독 상태가 오늘 날짜 기준으로 정상 체크됨 → 이후 로직 실행
                                Log.d("SubscriptionCheck", "✅ 상태 확인 완료 후 이어서 작업 실행")

                                mixpanel.track("click_tab_subscribe", null)

                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView_main, SubscribeFragment())
                                    .addToBackStack(null)
                                    .commit()
                            } else {
                                Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                                goToLogin()
                            }
                        }
                    } else {
                        val bundle = Bundle().apply { putBoolean("isEnter", true) }

                        // 전달할 Fragment 생성
                        var nextFragment = LoginFragment().apply {
                            arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                        }

                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, nextFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    true
                }

                R.id.menu_mypage -> {
                    if(MyApplication.isLogin) {
                        updateSubscriptionStatusIfNeeded(activity = this) { success ->
                            if (success) {
                                // 구독 상태가 오늘 날짜 기준으로 정상 체크됨 → 이후 로직 실행
                                Log.d("SubscriptionCheck", "✅ 상태 확인 완료 후 이어서 작업 실행")

                                mixpanel.track("click_tab_mypage", null)

                                supportFragmentManager.beginTransaction()
                                    .replace(R.id.fragmentContainerView_main, MypageFragment())
                                    .addToBackStack(null)
                                    .commit()
                            } else {
                                Log.e("SubscriptionCheck", "❌ 상태 체크 실패")

                                goToLogin()
                            }
                        }
                    } else {
                        val bundle = Bundle().apply { putBoolean("isEnter", true) }

                        // 전달할 Fragment 생성
                        var nextFragment = LoginFragment().apply {
                            arguments = bundle // 생성한 Bundle을 Fragment의 arguments에 설정
                        }

                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, nextFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    true
                }

                else -> false
            }
        }
    }

    fun goToLogin() {
        MyApplication.isLogin = false

        TokenManager(this).deleteAccessToken()
        TokenManager(this).deleteRefreshToken()
        removeSubscriptionLastCheckedDate(this)
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView_main, LoginFragment())
            .commit()
    }

    // ✅ HomeFragment에 들어오면 Bottom Navigation을 "Home"으로 설정하는 함수
    fun setBottomNavigationHome() {
        binding.bottomNavigationView.selectedItemId = R.id.menu_home
    }

    fun getPendingPushStoreId(): Long? {
        val temp = pendingPushStoreId
        pendingPushStoreId = null // 한 번 사용했으면 초기화
        return temp
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            for (signature in info.signingInfo?.apkContentsSigners!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d("KeyHash", keyHash)  // 키 해시를 로그로 출력
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("KeyHash", "Unable to get MessageDigest. signature= $e")
        } catch (e: Exception) {
            Log.e("KeyHash", "Exception: $e")
        }
    }

    fun updateSubscriptionStatusIfNeeded(activity: MainActivity, onComplete: (Boolean) -> Unit) {
        if (!SubscriptionChecker.isSubscriptionCheckedToday(activity)) {
            // 🔄 토큰 재발급 및 구독 정보 최신화
            TokenUtil.refreshToken(activity) {
                SubscriptionChecker.saveSubscriptionLastCheckedDate(activity)
                Log.d("SubscriptionStatus", "✅ 구독 상태 최신화 & 날짜 저장")
                onComplete(true)
            }
        } else {
            Log.d("SubscriptionStatus", "☑️ 오늘 이미 구독 상태 체크됨")
            onComplete(true)
        }
    }


    fun hideKeyboard() {
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                currentFocusView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun hideBottomNavigation(state: Boolean) {
        if (state) binding.bottomNavigationView.visibility =
            View.GONE else binding.bottomNavigationView.visibility = View.VISIBLE
    }

    fun hideOrderHistoryButton(state: Boolean) {
        if (state) binding.buttonOrderHistory.visibility =
            View.GONE else binding.buttonOrderHistory.visibility = View.VISIBLE
    }

    fun hideMapButton(state: Boolean) {
        if (state) binding.buttonList.visibility =
            View.GONE else binding.buttonList.visibility = View.VISIBLE
    }

    fun hideMyLocationButton(state: Boolean) {
        if (state) binding.buttonMyLocation.visibility =
            View.GONE else binding.buttonMyLocation.visibility = View.VISIBLE
    }

    fun setFCMToken() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("FCM Token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("FCM Token", "$token")
            MyApplication.preferences.setFCMToken(token)
            Log.d("FCM Token", "FCM 토큰 : ${MyApplication.preferences.getFCMToken()}")

            if (this::sharedPreferenceManager.isInitialized) {
                Log.d("FCM Token", "this::sharedPreferenceManager.isInitialized")
                sharedPreferenceManager.setFCMToken(token)
            }
        }
    }
}