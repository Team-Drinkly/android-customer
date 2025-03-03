package com.project.drinkly.api.response.subscribe

data class UserSubscribeDataResponse(
    val memberId: Int,
    val nickname: String,
    val isSubscribe: Boolean,
    val subscribeInfo: SubscribeInfo?
)

data class SubscribeInfo(
    val leftDays: Int?,
    val expiredDate: String?,
    val usedCount: Int?,
    val drinksHistory: List<DrinksHistory>?
)

data class DrinksHistory(
    val freeDrinkHistoryId: Int?,
    val storeName: String?,
    val providedDrink: String?,
    val usageDate: String?
)
