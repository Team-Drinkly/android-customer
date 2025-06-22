package com.project.drinkly.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.project.drinkly.BuildConfig
import com.mixpanel.android.mpmetrics.MixpanelAPI

class GlobalApplication : Application() {
    companion object {
        lateinit var mixpanel: MixpanelAPI
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // Kakao Sdk 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_KEY)

        mixpanel = MixpanelAPI.getInstance(this, BuildConfig.MIXPANEL_KEY, false)
    }
}