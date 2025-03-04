package com.project.drinkly.ui.mypage.viewModel

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.project.drinkly.R
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.login.LoginResponse
import com.project.drinkly.api.response.subscribe.UserIdResponse
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.onboarding.signUp.SignUpAgreementFragment
import com.project.drinkly.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageViewModel: ViewModel() {
    fun getUserId(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getUserId("Bearer ${tokenManager.getAccessToken()}")
            .enqueue(object :
                Callback<BaseResponse<UserIdResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<UserIdResponse>>,
                    response: Response<BaseResponse<UserIdResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<UserIdResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())


                        withdrawal(activity, result?.payload?.memberId!!)
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<UserIdResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<UserIdResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun withdrawal(activity: MainActivity, memberId: Long) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.withdrawal(memberId)
            .enqueue(object :
                Callback<BaseResponse<String>> {
                override fun onResponse(
                    call: Call<BaseResponse<String>>,
                    response: Response<BaseResponse<String>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<String>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        tokenManager.deleteAccessToken()
                        tokenManager.deleteRefreshToken()

                        activity.fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<String>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}