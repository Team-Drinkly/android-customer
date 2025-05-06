package com.project.drinkly.api.response.coupon

data class MembershipCouponListResponse(
    val id: Long,
    val memberId: Long,
    val type: String,
    val status: String,
    val used: Boolean,
    val expirationDate: String,
    val title: String,
    val description: String
)