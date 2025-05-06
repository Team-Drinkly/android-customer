package com.project.drinkly.api.response.coupon

data class StoreCouponListResponse(
    val id: Long,
    val memberId: Long,
    val status: String,
    val expirationDate: String,
    val title: String,
    val description: String,
    val storeName: String
)