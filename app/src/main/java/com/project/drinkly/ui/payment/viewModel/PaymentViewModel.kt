package com.project.drinkly.ui.payment.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.payment.SubscribeStatusInfoResponse
import com.project.drinkly.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentViewModel: ViewModel() {

    var subscribeStatus: MutableLiveData<String> = MutableLiveData()

    fun getSubscribeStatusInfo(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getSubscribeStatusInfo("Bearer ${tokenManager.getAccessToken()}")
            .enqueue(object :
                Callback<BaseResponse<SubscribeStatusInfoResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<SubscribeStatusInfoResponse>>,
                    response: Response<BaseResponse<SubscribeStatusInfoResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<SubscribeStatusInfoResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        if(result?.payload != null) {
                            subscribeStatus.value = result?.payload?.status
                        } else {
                            subscribeStatus.value = ""
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<SubscribeStatusInfoResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<SubscribeStatusInfoResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}