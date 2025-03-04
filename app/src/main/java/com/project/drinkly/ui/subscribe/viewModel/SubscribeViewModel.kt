package com.project.drinkly.ui.subscribe.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.api.response.subscribe.UserIdResponse
import com.project.drinkly.api.response.subscribe.UserSubscribeDataResponse
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubscribeViewModel : ViewModel() {

    var userInfo: MutableLiveData<UserSubscribeDataResponse?> = MutableLiveData()

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

                        getUserInfo(activity, result?.payload?.memberId!!)
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

    fun getUserInfo(activity: MainActivity, memberId: Long) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.getUserInfo(memberId)
            .enqueue(object :
                Callback<BaseResponse<UserSubscribeDataResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<UserSubscribeDataResponse>>,
                    response: Response<BaseResponse<UserSubscribeDataResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<UserSubscribeDataResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        MyApplication.isSubscribe = result?.payload?.isSubscribe == true
                        MyApplication.isUsedToday = result?.payload?.subscribeInfo?.isUsedToday == true

                        userInfo.value = result?.payload
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<UserSubscribeDataResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<UserSubscribeDataResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}