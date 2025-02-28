package com.project.drinkly.api

import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.login.LoginResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PartMap
import retrofit2.http.Path

interface ApiService {
    // OAuth로그인
    @POST("/api/v1/member/oauth/MEMBER/{provider}")
    fun login(
        @Path("provider") provider: String,
        @Header("Authorization") token: String
    ): Call<BaseResponse<LoginResponse>>
}