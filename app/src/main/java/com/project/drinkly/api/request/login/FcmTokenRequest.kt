package com.project.drinkly.api.request.login

data class FcmTokenRequest(
    val memberId: Int,
    val isAlarmOn: Boolean,
    val fcmToken: String,
    val platform: String = "ANDROID"
)
