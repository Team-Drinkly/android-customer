package com.project.drinkly.ui.onboarding.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.drinkly.BuildConfig
import com.project.drinkly.R
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.request.login.FcmTokenRequest
import com.project.drinkly.api.request.login.SignUpRequest
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.login.CheckNicknameDuplicateResponse
import com.project.drinkly.api.response.login.LoginResponse
import com.project.drinkly.api.response.login.NiceUrlResponse
import com.project.drinkly.api.response.login.SignUpResponse
import com.project.drinkly.databinding.DialogBasicBinding
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.ui.dialog.BasicDialogInterface
import com.project.drinkly.ui.dialog.DialogBasic
import com.project.drinkly.ui.mypage.viewModel.MypageViewModel
import com.project.drinkly.ui.onboarding.LoginFragment
import com.project.drinkly.ui.onboarding.signUp.PassWebActivity
import com.project.drinkly.ui.onboarding.signUp.SignUpAgreementFragment
import com.project.drinkly.ui.onboarding.signUp.SignUpNickNameFragment
import com.project.drinkly.ui.store.StoreMapFragment
import com.project.drinkly.ui.store.viewModel.StoreViewModel
import com.project.drinkly.ui.subscribe.viewModel.SubscribeViewModel
import com.project.drinkly.util.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder

class LoginViewModel : ViewModel() {

    var passUrl: MutableLiveData<String> = MutableLiveData()

    var isUsableNickName: MutableLiveData<Boolean> = MutableLiveData()

    fun login(activity: MainActivity, provider: String, token: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)
        val viewModel = ViewModelProvider(activity)[SubscribeViewModel::class.java]

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

                            viewModel.getUserId(activity)

                            MyApplication.isLogin = true

                            // 홈화면 이동
                            activity.setBottomNavigationHome()
                        } else {
                            MyApplication.oauthId = result?.payload?.oauthId!!
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

    fun getNiceUrlData(activity: PassWebActivity, memberId: Int) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.getNiceUrlData(memberId)
            .enqueue(object :
                Callback<BaseResponse<NiceUrlResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<NiceUrlResponse>>,
                    response: Response<BaseResponse<NiceUrlResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<NiceUrlResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        var encode_enc = URLEncoder.encode(result?.payload?.enc_data ?: "", "UTF-8")
                        var encode_integrity = URLEncoder.encode(result?.payload?.integrity_value ?: "", "UTF-8")
                        var URL_INFO = BuildConfig.PASS_URL + "?m=service&token_version_id=${result?.payload?.token_version_id}&enc_data=${encode_enc}&integrity_value=${encode_integrity}"

                        passUrl.value = URL_INFO

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<NiceUrlResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<NiceUrlResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun callBackNiceData(activity: PassWebActivity, memberId: String?, tokenVersionId: String?, encData: String?, integrityValue: String?) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.callBackNiceData(memberId!!, "member", tokenVersionId!!, encData!!, integrityValue!!)
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

                        activity.finish()

                        if(response.body()?.result?.code == 403) {
                            MyApplication.signUpPassAuthorization = false
                        } else {
                            MyApplication.signUpPassAuthorization = true
                        }
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

    fun checkNicknameDuplicate(activity: MainActivity, nickname: String) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.checkNicknameDuplicate(nickname)
            .enqueue(object :
                Callback<BaseResponse<CheckNicknameDuplicateResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<CheckNicknameDuplicateResponse>>,
                    response: Response<BaseResponse<CheckNicknameDuplicateResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<CheckNicknameDuplicateResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        isUsableNickName.value = result?.payload?.isExist == false

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<CheckNicknameDuplicateResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<CheckNicknameDuplicateResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }

    fun signUp(activity: MainActivity, nickname: String) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)
        val viewModel = ViewModelProvider(activity)[SubscribeViewModel::class.java]
        val mypageViewModel = ViewModelProvider(activity)[MypageViewModel::class.java]

        apiClient.apiService.signUp(SignUpRequest(MyApplication.oauthId, nickname))
            .enqueue(object :
                Callback<BaseResponse<SignUpResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<SignUpResponse>>,
                    response: Response<BaseResponse<SignUpResponse>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<SignUpResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        tokenManager.saveTokens(result?.payload?.accessToken.toString(), result?.payload?.refreshToken.toString())
                        viewModel.getUserId(activity)
                        mypageViewModel.getCoupon(activity, "INITIAL")

                        MyApplication.isLogin = true

                        activity.setBottomNavigationHome()
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<SignUpResponse>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<SignUpResponse>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())
                }
            })
    }
    fun saveFcmToken(activity: MainActivity, body: FcmTokenRequest) {
        val apiClient = ApiClient(activity)
        val tokenManager = TokenManager(activity)

        apiClient.apiService.saveFcmToken("Bearer ${tokenManager.getRefreshToken().toString()}", body)
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