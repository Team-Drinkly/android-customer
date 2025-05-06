package com.project.drinkly.api.response.subscribe

data class SubscribeInfoResponse(
    val memberId: Long,
    val nickname: String,
    val isSubscribe: Boolean,
    val subscribeInformation: SubscribeDetailInfo?
)

data class SubscribeDetailInfo(
    val leftDays: Int?,
    val startDate: String?,
    val expiredDate: String?,
)
