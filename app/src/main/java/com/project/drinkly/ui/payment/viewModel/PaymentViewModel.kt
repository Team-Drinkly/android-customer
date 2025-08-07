package com.project.drinkly.ui.payment.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.TokenUtil
import com.project.drinkly.api.request.payment.DeleteCardRequest
import com.project.drinkly.api.request.payment.RegisterCardRequest
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.payment.CardInfoResponse
import com.project.drinkly.api.response.payment.DeleteCardResponse
import com.project.drinkly.api.response.payment.RegisterCardResponse
import com.project.drinkly.api.response.payment.SubscribePaymentResponse
import com.project.drinkly.api.response.payment.SubscribeStatusInfoResponse
import com.project.drinkly.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentViewModel: ViewModel() {

    var subscribeStatus: MutableLiveData<String> = MutableLiveData()

    var registeredCardInfo: MutableLiveData<CardInfoResponse?> = MutableLiveData()

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
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<SubscribeStatusInfoResponse>? = response.body()

                        if(result?.payload != null) {
                            subscribeStatus.value = result?.payload?.status
                        } else {
                            subscribeStatus.value = ""
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<SubscribeStatusInfoResponse>? = response.body()
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getSubscribeStatusInfo(activity)
                                }
                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<SubscribeStatusInfoResponse>>, t: Throwable) {
                    // 통신 실패
                    activity.goToLogin()
                }
            })
    }

    fun getCardInfo(activity: MainActivity) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.getCardInfo("Bearer ${tokenManager.getAccessToken()}")
            .enqueue(object :
                Callback<BaseResponse<CardInfoResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<CardInfoResponse>>,
                    response: Response<BaseResponse<CardInfoResponse>>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<CardInfoResponse>? = response.body()

                        registeredCardInfo.value = result?.payload
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<CardInfoResponse>? = response.body()
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    getCardInfo(activity)
                                }
                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<CardInfoResponse>>, t: Throwable) {
                    // 통신 실패
                    activity.goToLogin()
                }
            })
    }

    fun registerCard(activity: MainActivity, cardInfo: RegisterCardRequest, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.registerCard("Bearer ${tokenManager.getAccessToken()}", cardInfo)
            .enqueue(object :
                Callback<BaseResponse<RegisterCardResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<RegisterCardResponse>>,
                    response: Response<BaseResponse<RegisterCardResponse>>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<RegisterCardResponse>? = response.body()

                        when(result?.payload?.resultCode) {
                            "0000" -> onSuccess()
                            else -> onFailure()
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<RegisterCardResponse>? = response.body()
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    registerCard(activity, cardInfo, onSuccess, onFailure)
                                }
                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<RegisterCardResponse>>, t: Throwable) {
                    // 통신 실패
                    activity.goToLogin()
                }
            })
    }

    fun deleteCard(activity: MainActivity, cardOrderId: String, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.deleteCard("Bearer ${tokenManager.getAccessToken()}",
            DeleteCardRequest(cardOrderId))
            .enqueue(object :
                Callback<BaseResponse<DeleteCardResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<DeleteCardResponse>>,
                    response: Response<BaseResponse<DeleteCardResponse>>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<DeleteCardResponse>? = response.body()

                        onSuccess()
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<DeleteCardResponse>? = response.body()
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    deleteCard(activity, cardOrderId, onSuccess)
                                }
                            }
                            else -> {
                                activity.goToLogin()
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<BaseResponse<DeleteCardResponse>>, t: Throwable) {
                    // 통신 실패
                    activity.goToLogin()
                }
            })
    }

    fun deleteCardMembership(activity: MainActivity, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.deleteCardMembership("Bearer ${tokenManager.getAccessToken()}")
            .enqueue(object :
                Callback<BaseResponse<String?>> {
                override fun onResponse(
                    call: Call<BaseResponse<String?>>,
                    response: Response<BaseResponse<String?>>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<String?>? = response.body()

                        TokenUtil.refreshToken(activity) {
                            onSuccess()
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    deleteCardMembership(activity, onSuccess)
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
                    activity.goToLogin()
                }
            })
    }

    fun paymentForSubscribe(activity: MainActivity, onSuccess: () -> Unit, onFailure: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.paymentForSubscribe("Bearer ${tokenManager.getAccessToken()}")
            .enqueue(object :
                Callback<BaseResponse<SubscribePaymentResponse?>> {
                override fun onResponse(
                    call: Call<BaseResponse<SubscribePaymentResponse?>>,
                    response: Response<BaseResponse<SubscribePaymentResponse?>>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<SubscribePaymentResponse?>? = response.body()

                        TokenUtil.refreshToken(activity) {
                            onSuccess()
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<SubscribePaymentResponse?>? = response.body()
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    paymentForSubscribe(activity, onSuccess, onFailure)
                                }
                            }
                            else -> {
                                onFailure()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<SubscribePaymentResponse?>>, t: Throwable) {
                    // 통신 실패
                    onFailure()
                }
            })
    }

    fun cancelSubscribe(activity: MainActivity, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.cancelSubscribe("Bearer ${tokenManager.getAccessToken()}")
            .enqueue(object :
                Callback<BaseResponse<String?>> {
                override fun onResponse(
                    call: Call<BaseResponse<String?>>,
                    response: Response<BaseResponse<String?>>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<String?>? = response.body()

                        TokenUtil.refreshToken(activity) {
                            onSuccess()
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    cancelSubscribe(activity, onSuccess)
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
                    activity.goToLogin()
                }
            })
    }

    fun revertCancelSubscribe(activity: MainActivity, onSuccess: () -> Unit) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.revertCancelSubscribe("Bearer ${tokenManager.getAccessToken()}")
            .enqueue(object :
                Callback<BaseResponse<String?>> {
                override fun onResponse(
                    call: Call<BaseResponse<String?>>,
                    response: Response<BaseResponse<String?>>
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<String?>? = response.body()

                        when(result?.result?.code) {
                            200 -> onSuccess()
                            else -> { }
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<String?>? = response.body()
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음

                        when(response.code()) {
                            498 -> {
                                TokenUtil.refreshToken(activity) {
                                    revertCancelSubscribe(activity, onSuccess)
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
                    activity.goToLogin()
                }
            })
    }
}