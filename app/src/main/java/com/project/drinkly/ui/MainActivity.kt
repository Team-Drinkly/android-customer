package com.project.drinkly.ui

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
import com.project.drinkly.R
import com.project.drinkly.databinding.ActivityMainBinding
import com.project.drinkly.ui.mypage.MypageFragment
import com.project.drinkly.ui.onboarding.LoginFragment
import com.project.drinkly.ui.store.StoreDetailFragment
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.ui.subscribe.SubscribeFragment
import com.project.drinkly.util.MainUtil.setStatusBarTransparent
import com.project.drinkly.util.MyApplication
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        this.setStatusBarTransparent()
        getKeyHash()


        setBottomNavigationView()


        window.apply {
            //상태바 아이콘(true: 검정 / false: 흰색)
            WindowInsetsControllerCompat(this, this.decorView).isAppearanceLightStatusBars = false
        }

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView_main, StoreMapFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }

                R.id.menu_subscribe -> {
                    if(MyApplication.isLogin) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, SubscribeFragment())
                            .addToBackStack(null)
                            .commit()
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
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView_main, MypageFragment())
                            .addToBackStack(null)
                            .commit()
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

    // ✅ HomeFragment에 들어오면 Bottom Navigation을 "Home"으로 설정하는 함수
    fun setBottomNavigationHome() {
        binding.bottomNavigationView.selectedItemId = R.id.menu_home
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

    fun hideMapButton(state: Boolean) {
        if (state) binding.buttonList.visibility =
            View.GONE else binding.buttonList.visibility = View.VISIBLE
    }

    fun hideMyLocationButton(state: Boolean) {
        if (state) binding.buttonMyLocation.visibility =
            View.GONE else binding.buttonMyLocation.visibility = View.VISIBLE
    }
}