package com.project.drinkly.api

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.login.SignUpResponse
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.response.subscribe.SubscribeInfoResponse
import com.project.drinkly.ui.subscribe.viewModel.SubscriptionChecker.removeSubscriptionLastCheckedDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object TokenUtil {

    fun refreshToken(activity: MainActivity, retryRequest: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.refreshToken("Bearer ${tokenManager.getRefreshToken()}")
            .enqueue(object : Callback<BaseResponse<SignUpResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<SignUpResponse>>,
                    response: Response<BaseResponse<SignUpResponse>>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()

                        when(result?.result?.code) {
                            200 -> {
                                tokenManager.saveTokens(
                                    result.payload?.accessToken.toString(),
                                    result.payload?.refreshToken.toString()
                                )

                                getSubscribeInfo(activity)

                                retryRequest()
                            }

                            400 -> {
                                tokenManager.deleteAccessToken()
                                tokenManager.deleteRefreshToken()
                                removeSubscriptionLastCheckedDate(activity)
                                activity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            }
                        }
                    } else {
                        Log.e("TokenUtil", "재발급 실패: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<BaseResponse<SignUpResponse>>, t: Throwable) {
                    Log.e("TokenUtil", "onFailure: ${t.message}")
                }
            })
    }


    fun getSubscribeInfo(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getSubscribeInfo("Bearer ${tokenManager.getRefreshToken().toString()}")
            .enqueue(object :
                Callback<BaseResponse<SubscribeInfoResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<SubscribeInfoResponse>>,
                    response: Response<BaseResponse<SubscribeInfoResponse>>
                ) {

                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<SubscribeInfoResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        var infoManager = InfoManager(activity)

                        infoManager.run {
                            saveUserId(result?.payload?.memberId ?: 0)
                            saveUserNickname(result?.payload?.nickname ?: "")
                            saveIsSubscribe(result?.payload?.isSubscribe ?: false)
                            saveSubscribeLeftDays(result?.payload?.subscribeInformation?.leftDays ?: 0)
                            saveSubscribeDays(result?.payload?.subscribeInformation?.startDate.toString(), result?.payload?.subscribeInformation?.expiredDate.toString())
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<SubscribeInfoResponse>? = response.body()
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

                override fun onFailure(call: Call<BaseResponse<SubscribeInfoResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}
