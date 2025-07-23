package com.project.drinkly.util

import android.app.Application
import com.project.drinkly.api.response.subscribe.UserSubscribeDataResponse

class MyApplication : Application() {
    companion object {

        lateinit var preferences: PreferenceUtil

        // 회원가입 정보
        var oauthId = 0
        var signUpPassAuthorization: Boolean? = null

        // 유저 정보
        var isLogin = false

        // 지도
        var latitude = 37.63022195215973
        var longitude = 127.07671771357782
        var radius = 0

        // 카드 정보
        var isCardRegistered = false
    }
}