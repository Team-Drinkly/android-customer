package com.project.drinkly.api

import android.util.Log
import androidx.fragment.app.FragmentManager
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.login.SignUpResponse
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.api.TokenManager
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
                        tokenManager.saveTokens(
                            result?.payload?.accessToken.toString(),
                            result?.payload?.refreshToken.toString()
                        )

                        retryRequest()
                    } else {
                        Log.e("TokenUtil", "재발급 실패: ${response.errorBody()?.string()}")

                        if (response.code() == 400) {
                            tokenManager.deleteAccessToken()
                            tokenManager.deleteRefreshToken()
                            activity.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<SignUpResponse>>, t: Throwable) {
                    Log.e("TokenUtil", "onFailure: ${t.message}")
                }
            })
    }
}
