package com.project.drinkly.ui.event.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.event.DrinklyEventResponse
import com.project.drinkly.api.response.event.GeneralEventResponse
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel: ViewModel() {

    var drinklyEventList: MutableLiveData<MutableList<DrinklyEventResponse>?> = MutableLiveData()
    var generalEventList: MutableLiveData<MutableList<GeneralEventResponse>?> = MutableLiveData()

    fun getDrinklyEventList(activity: MainActivity) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.getDrinklyEventList()
            .enqueue(object :
                Callback<BaseResponse<List<DrinklyEventResponse>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<DrinklyEventResponse>>>,
                    response: Response<BaseResponse<List<DrinklyEventResponse>>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<DrinklyEventResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        drinklyEventList.value = result?.payload as MutableList<DrinklyEventResponse>?

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<DrinklyEventResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<DrinklyEventResponse>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }

    fun getGeneralEventList(activity: MainActivity) {
        val apiClient = ApiClient(activity)

        apiClient.apiService.getGeneralEventList()
            .enqueue(object :
                Callback<BaseResponse<List<GeneralEventResponse>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<GeneralEventResponse>>>,
                    response: Response<BaseResponse<List<GeneralEventResponse>>>
                ) {
                    Log.d("DrinklyViewModel", "onResponse 성공: " + response.body().toString())
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        val result: BaseResponse<List<GeneralEventResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 성공: " + result?.toString())

                        generalEventList.value = result?.payload as MutableList<GeneralEventResponse>?

                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        var result: BaseResponse<List<GeneralEventResponse>>? = response.body()
                        Log.d("DrinklyViewModel", "onResponse 실패: " + response.body())
                        val errorBody = response.errorBody()?.string() // 에러 응답 데이터를 문자열로 얻음
                        Log.d("DrinklyViewModel", "Error Response: $errorBody")

                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<GeneralEventResponse>>>, t: Throwable) {
                    // 통신 실패
                    Log.d("DrinklyViewModel", "onFailure 에러: " + t.message.toString())

                    activity.goToLogin()
                }
            })
    }
}