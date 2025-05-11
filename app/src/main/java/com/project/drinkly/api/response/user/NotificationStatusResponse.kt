package com.project.drinkly.api.response.user

data class NotificationStatusResponse(
    val memberId: Int,
    val alarmStatus: Boolean
)