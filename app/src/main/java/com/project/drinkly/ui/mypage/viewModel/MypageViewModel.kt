package com.project.drinkly.ui.mypage.viewModel

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.R
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.coupon.CouponListResponse
import com.project.drinkly.api.response.login.LoginResponse
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.api.response.subscribe.UserIdResponse
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.DialogEvent
import com.project.drinkly.ui.onboarding.signUp.SignUpAgreementFragment
import com.project.drinkly.ui.subscribe.viewModel.SubscribeViewModel
import com.project.drinkly.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MypageViewModel: ViewModel() {

    var availableCouponInfo: MutableLiveData<MutableList<CouponListResponse>> = MutableLiveData()
    var usedCouponInfo: MutableLiveData<MutableList<CouponListResponse>> = MutableLiveData()

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

    fun getCoupon(activity: MainActivity, couponType: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getCoupon("Bearer ${tokenManager.getAccessToken()}", couponType)
            .enqueue(object :
                Callback<BaseResponse<Long?>> {
                override fun onResponse(
                    call: Call<BaseResponse<Long?>>,
                    response: Response<BaseResponse<Long?>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<Long?>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        val dialog = DialogEvent()

                        dialog.show(activity.supportFragmentManager, "DialogEvent")
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<Long?>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<Long?>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun getAvailableCouponList(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        var tempCouponInfoList = mutableListOf<CouponListResponse>()

        apiClient.apiService.getAvailableCouponList("Bearer ${tokenManager.getAccessToken()}")
            .enqueue(object :
                Callback<BaseResponse<List<CouponListResponse?>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<CouponListResponse?>>>,
                    response: Response<BaseResponse<List<CouponListResponse?>>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<CouponListResponse?>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        for (c in 0 until (result?.payload?.size ?: 0)) {
                            var couponId = result?.payload?.get(c)?.id
                            var memberId = result?.payload?.get(c)?.memberId
                            var type = result?.payload?.get(c)?.type
                            var status = result?.payload?.get(c)?.status
                            var used = result?.payload?.get(c)?.used
                            var expirationDate = result?.payload?.get(c)?.expirationDate

                            tempCouponInfoList.add(CouponListResponse(couponId!!, memberId!!, type!!, status!!, used!!, expirationDate!!))
                        }

                        availableCouponInfo.value = tempCouponInfoList
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<CouponListResponse?>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<CouponListResponse?>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun getUsedCouponList(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        var tempCouponInfoList = mutableListOf<CouponListResponse>()

        apiClient.apiService.getUsedCouponList("Bearer ${tokenManager.getAccessToken()}")
            .enqueue(object :
                Callback<BaseResponse<List<CouponListResponse?>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<CouponListResponse?>>>,
                    response: Response<BaseResponse<List<CouponListResponse?>>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<CouponListResponse?>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        for (c in 0 until (result?.payload?.size ?: 0)) {
                            var couponId = result?.payload?.get(c)?.id
                            var memberId = result?.payload?.get(c)?.memberId
                            var type = result?.payload?.get(c)?.type
                            var status = result?.payload?.get(c)?.status
                            var used = result?.payload?.get(c)?.used
                            var expirationDate = result?.payload?.get(c)?.expirationDate

                            tempCouponInfoList.add(CouponListResponse(couponId!!, memberId!!, type!!, status!!, used!!, expirationDate!!))
                        }

                        usedCouponInfo.value = tempCouponInfoList
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<CouponListResponse?>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<CouponListResponse?>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun useCoupon(activity: MainActivity, couponId: Long) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)
        val viewModel = ViewModelProvider(activity)[SubscribeViewModel::class.java]

        apiClient.apiService.useCoupon("Bearer ${tokenManager.getAccessToken()}", couponId)
            .enqueue(object :
                Callback<BaseResponse<String?>> {
                override fun onResponse(
                    call: Call<BaseResponse<String?>>,
                    response: Response<BaseResponse<String?>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<String?>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        getAvailableCouponList(activity)
                        viewModel.getUserId(activity)
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<String?>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
}