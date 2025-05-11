package com.project.drinkly.ui.store.viewModel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.project.drinkly.R
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.TokenUtil
import com.project.drinkly.api.request.subscribe.UseMembershipRequest
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.coupon.StoreCouponResponse
import com.project.drinkly.api.response.store.StoreDetailResponse
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.onboarding.signUp.SignUpAgreementFragment
import com.project.drinkly.ui.onboarding.viewModel.LoginViewModel
import com.project.drinkly.ui.store.StoreDetailFragment
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.ui.subscribe.viewModel.SubscribeViewModel
import com.project.drinkly.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StoreViewModel: ViewModel() {

    var storeInfo: MutableLiveData<MutableList<StoreListResponse>> = MutableLiveData()
    var storeDetailInfo: MutableLiveData<StoreDetailResponse> = MutableLiveData()
    var storeCouponInfo: MutableLiveData<StoreCouponResponse?> = MutableLiveData()

    var isUsed: MutableLiveData<Boolean> = MutableLiveData()

    var usedMembershipTime: MutableLiveData<String> = MutableLiveData()
    var usedCouponTime: MutableLiveData<String> = MutableLiveData()

    fun getStoreList(activity: MainActivity, latitude: String?, longitude: String?, radius: Int?, searchInput: String?) {
        val apiClient = ApiClient(activity)

        var tempStoreListInfo = mutableListOf<StoreListResponse>()

        apiClient.apiService.getStoreList(latitude, longitude, radius, searchInput)
            .enqueue(object :
                Callback<BaseResponse<List<StoreListResponse>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<StoreListResponse>>>,
                    response: Response<BaseResponse<List<StoreListResponse>>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<StoreListResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        for (s in 0 until (result?.payload?.size ?: 0)) {
                            var tempAvailableDrink = mutableListOf<String>()
                            
                            var storeId = result?.payload?.get(s)?.id
                            var storeName = result?.payload?.get(s)?.storeName
                            var storeMainImageUrl = result?.payload?.get(s)?.storeMainImageUrl
                            var latitude = result?.payload?.get(s)?.latitude
                            var longitude = result?.payload?.get(s)?.longitude
                            var isOpen = result?.payload?.get(s)?.isOpen
                            var isAvailable = result?.payload?.get(s)?.isAvailable
                            var openingInfo = result?.payload?.get(s)?.openingInfo
                            var storeTel = result?.payload?.get(s)?.storeTel
                            var storeAddress = result?.payload?.get(s)?.storeAddress
                            for (available in 0 until (result?.payload?.get(s)?.availableDrinks?.size ?: 0)) {
                                tempAvailableDrink.add(result?.payload?.get(s)?.availableDrinks?.get(available).toString())
                            }
                            tempStoreListInfo.add(StoreListResponse(storeId!!, storeName, storeMainImageUrl, latitude, longitude, isOpen, isAvailable, openingInfo, storeTel, storeAddress, tempAvailableDrink))
                        }

                        storeInfo.value = tempStoreListInfo

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<StoreListResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<StoreListResponse>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun getStoreCoupon(activity: MainActivity, storeId: Long) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getStoreCoupon("Bearer ${tokenManager.getAccessToken()}", storeId)
            .enqueue(object :
                Callback<BaseResponse<List<StoreCouponResponse>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<StoreCouponResponse>>>,
                    response: Response<BaseResponse<List<StoreCouponResponse>>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<StoreCouponResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        when(result?.result?.code) {
                            in 200..299 -> {
                                if(result?.payload?.size != 0) {
                                    storeCouponInfo.value = result?.payload?.get(0)
                                } else {
                                    storeCouponInfo.value = null
                                }
                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<StoreCouponResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getStoreCoupon(activity, storeId)
                                }
                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<StoreCouponResponse>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun downloadCoupon(activity: MainActivity, couponId: Long, storeId: Long) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.downloadCoupon("Bearer ${tokenManager.getAccessToken()}", couponId)
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

                        when(result?.result?.code) {
                            in 200..299 -> {
                                getStoreCoupon(activity, storeId)
                            }
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    downloadCoupon(activity, couponId, storeId)
                                }
                            }

                            else -> {
                                activity.goToLogin()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String?>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun useStoreCoupon(activity: MainActivity, couponId: Long) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.useStoreCoupon("Bearer ${tokenManager.getAccessToken()}", couponId)
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

                        when(result?.result?.code) {
                            in 200..299 -> {
                                val dateFormat =
                                    SimpleDateFormat("yyyy년 M월 d일 HH시 mm분", Locale.KOREAN) // 한국어 형식
                                val currentDate = Date() // 현재 시간 가져오기

                                usedCouponTime.value = dateFormat.format(currentDate).toString()
                            }
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    useStoreCoupon(activity, couponId)
                                }
                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String?>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun getStoreDetail(activity: MainActivity, storeId: Long) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.getStoreDetail(storeId)
            .enqueue(object :
                Callback<BaseResponse<StoreDetailResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<StoreDetailResponse>>,
                    response: Response<BaseResponse<StoreDetailResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        when(result?.result?.code) {
                            200 -> {
                                storeDetailInfo.value = result.payload!!
                            }
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<StoreDetailResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<StoreDetailResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun getUsedMembership(activity: MainActivity, storeId: Long) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getUsedMembership("Bearer ${tokenManager.getAccessToken()}", storeId)
            .enqueue(object :
                Callback<BaseResponse<Boolean>> {
                override fun onResponse(
                    call: Call<BaseResponse<Boolean>>,
                    response: Response<BaseResponse<Boolean>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<Boolean>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        isUsed.value = result?.payload ?: false

                        getStoreCoupon(activity, storeId)
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<Boolean>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        isUsed.value = false

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getUsedMembership(activity, storeId)
                                }
                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Boolean>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }


    fun useMembership(activity: MainActivity, storeId: Long, drinkName: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.useMembership("Bearer ${tokenManager.getAccessToken()}", UseMembershipRequest(storeId, drinkName))
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

                        val dateFormat = SimpleDateFormat("yyyy년 M월 d일 HH시 mm분", Locale.KOREAN)
                        val currentDate = Date() // 현재 시간 가져오기

                        when (result?.result?.code) {
                            201 -> {
                                usedMembershipTime.value = dateFormat.format(currentDate).toString()
                            }
                            400 -> {
                                // 이미 멤버십을 사용한 경우

                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    useMembership(activity, storeId, drinkName)
                                }
                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<String>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }
}