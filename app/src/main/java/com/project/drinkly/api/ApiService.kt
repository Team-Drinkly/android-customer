package com.project.drinkly.api

import com.project.drinkly.api.request.login.SignUpRequest
import com.project.drinkly.api.request.subscribe.UseMembershipRequest
import com.project.drinkly.api.response.BaseResponse
import com.project.drinkly.api.response.coupon.CouponListResponse
import com.project.drinkly.api.response.login.CheckNicknameDuplicateResponse
import com.project.drinkly.api.response.login.LoginResponse
import com.project.drinkly.api.response.login.NiceUrlResponse
import com.project.drinkly.api.response.login.SignUpResponse
import com.project.drinkly.api.response.store.StoreDetailResponse
import com.project.drinkly.api.response.store.StoreListResponse
import com.project.drinkly.api.response.subscribe.UserIdResponse
import com.project.drinkly.api.response.subscribe.UserSubscribeDataResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    // OAuth로그인
    @POST("v1/member/oauth/MEMBER/{provider}")
    fun login(
        @Path("provider") provider: String,
        @Header("Authorization") token: String
    ): Call<BaseResponse<LoginResponse>>

    // NICE URL 데이터 받기
    @GET("v1/member/nice/member/{memberId}")
    fun getNiceUrlData(
        @Path("memberId") memberId: Int
    ): Call<BaseResponse<NiceUrlResponse>>

    // NICE 데이터 전송
    @GET("v1/member/nice/call-back")
    fun callBackNiceData(
        @Query("id") id: String,
        @Query("type") type: String,
        @Query("token_version_id") token_version_id: String,
        @Query("enc_data") enc_data: String,
        @Query("integrity_value") integrity_value: String
    ): Call<BaseResponse<String>>

    // 닉네임 중복 확인
    @GET("/api/v1/member/nickname/duplicate/{nickname}")
    fun checkNicknameDuplicate(
        @Path("nickname") nickname: String
    ): Call<BaseResponse<CheckNicknameDuplicateResponse>>

    // 회원가입 - 정보 저장
    @POST("/api/v1/member/signup")
    fun signUp(
        @Body parameters: SignUpRequest
    ): Call<BaseResponse<SignUpResponse>>

    // 유저 ID 조회
    @GET("/api/v1/store/m/temp")
    fun getUserId(
        @Header("Authorization") token: String
    ): Call<BaseResponse<UserIdResponse>>

    // 계정 탈퇴
    @DELETE("/api/v1/member/deactivate")
    fun withdrawal(
        @Header("member-id") memberId: Long
    ): Call<BaseResponse<String>>

    // 유저 정보 조회
    @GET("/api/v1/member/profile/{memberId}")
    fun getUserInfo(
        @Path("memberId") memberId: Long
    ): Call<BaseResponse<UserSubscribeDataResponse>>

    // 제휴업체 조회
    @GET("/api/v1/store/m/list")
    fun getStoreList(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("radius") radius: Int,
        @Query("searchKeyword") searchKeyword: String?
    ): Call<BaseResponse<List<StoreListResponse>>>

    // 제휴업체 상세 조회
    @GET("/api/v1/store/m/list/{storeId}")
    fun getStoreDetail(
        @Path("storeId") storeId: Long
    ): Call<BaseResponse<StoreDetailResponse>>

    // 멤버십 사용
    @POST("/api/v1/store/m/free-drink")
    fun useMembership(
        @Header("Authorization") token: String,
        @Body parameters: UseMembershipRequest
    ): Call<BaseResponse<String>>

    // 쿠폰 발급
    @POST("/api/v1/payment/m/coupon-issue")
    fun getCoupon(
        @Header("Authorization") token: String,
        @Query("type") type: String
    ): Call<BaseResponse<Long?>>

    // 사용 전 쿠폰 조회
    @GET("/api/v1/payment/m/coupons/available")
    fun getAvailableCouponList(
        @Header("Authorization") token: String
    ): Call<BaseResponse<List<CouponListResponse?>>>

    // 사용 완료 쿠폰 조회
    @GET("/api/v1/payment/m/coupons/used")
    fun getUsedCouponList(
        @Header("Authorization") token: String
    ): Call<BaseResponse<List<CouponListResponse?>>>

    // 쿠폰 사용
    @POST("/api/v1/payment/m/coupon-use")
    fun useCoupon(
        @Header("Authorization") token: String,
        @Query("couponId") couponId: Long
    ): Call<BaseResponse<String?>>
}