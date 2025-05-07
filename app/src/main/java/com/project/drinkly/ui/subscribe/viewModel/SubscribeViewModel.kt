package com.project.drinkly.ui.subscribe.viewModel

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.InfoManager
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.TokenUtil
import com.project.drinkly.api.TokenUtil.getSubscribeInfo
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.coupon.MembershipCouponListResponse
import com.project.drinkly.api.response.login.SignUpResponse
import com.project.drinkly.api.response.store.StoreDetailResponse
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.api.response.subscribe.OrderHistoryResponse
import com.project.drinkly.api.response.subscribe.SubscribeInfoResponse
import com.project.drinkly.api.response.subscribe.UserIdResponse
import com.project.drinkly.api.response.subscribe.UserSubscribeDataResponse
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubscribeViewModel : ViewModel() {

    var orderHistory: MutableLiveData<OrderHistoryResponse?> = MutableLiveData()

    fun getOrderHistory(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getOrderHistory("Bearer ${tokenManager.getRefreshToken().toString()}")
            .enqueue(object :
                Callback<BaseResponse<OrderHistoryResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<OrderHistoryResponse>>,
                    response: Response<BaseResponse<OrderHistoryResponse>>
                ) {

                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<OrderHistoryResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        orderHistory.value = result?.payload
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<OrderHistoryResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getSubscribeInfo(activity)
                                }
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<OrderHistoryResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}