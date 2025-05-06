package com.project.drinkly.ui.subscribe.viewModel

import android.util.Log
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.drinkly.api.ApiClient
import com.project.drinkly.api.InfoManager
import com.project.drinkly.api.TokenManager
import com.project.drinkly.api.TokenUtil
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.login.SignUpResponse
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.api.response.subscribe.SubscribeInfoResponse
import com.project.drinkly.api.response.subscribe.UserIdResponse
import com.project.drinkly.api.response.subscribe.UserSubscribeDataResponse
import com.project.drinkly.ui.MainActivity
import com.project.drinkly.util.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SubscribeViewModel : ViewModel() {

}