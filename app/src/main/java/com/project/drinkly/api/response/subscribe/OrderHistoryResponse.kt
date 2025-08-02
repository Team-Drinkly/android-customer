package com.project.drinkly.api.response.subscribe

data class OrderHistoryResponse(
    val usedCount: Int,
    val drinksHistory: List<OrderHistory>?
)

data class OrderHistory(
    val freeDrinkHistoryId: Int?,
    val storeName: String?,
    val providedDrinkImageId: Int?,
    val providedDrink: String?,
    val usageDate: String?
)
