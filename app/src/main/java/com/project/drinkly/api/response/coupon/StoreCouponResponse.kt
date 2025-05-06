package com.project.drinkly.api.response.coupon

data class StoreCouponResponse(
    val id: Long,
    val memberId: Int,
    val status: String,
    val expirationDate: String,
    val title: String,
    val description: String,
    val storeName: String
)
