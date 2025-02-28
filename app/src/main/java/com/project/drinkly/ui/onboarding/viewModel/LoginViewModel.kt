package com.project.drinkly.ui.onboarding.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.project.drinkly.R
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.login.LoginResponse
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.onboarding.LoginFragment
import com.project.drinkly.ui.onboarding.signUp.SignUpAgreementFragment
import com.project.drinkly.ui.store.StoreMapFragment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel : ViewModel() {
    fun login(activity: MainActivity, provider: String, token: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.login(provider,token)
            .enqueue(object :
                Callback<BaseResponse<LoginResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<LoginResponse>>,
                    response: Response<BaseResponse<LoginResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<LoginResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        if(result?.payload?.isRegistered == true) {
                            tokenManager.saveTokens(result.payload.accessToken, result.payload.refreshToken)

                            // 홈화면 이동
                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView_main, StoreMapFragment())
                                .addToBackStack(null)
                                .commit()
                        } else {
                            // 회원가입 화면 이동
                            activity.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainerView_main, SignUpAgreementFragment())
                                .addToBackStack(null)
                                .commit()
                        }

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<LoginResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<LoginResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}