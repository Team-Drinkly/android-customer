package com.project.drinkly.api.response.payment

data class SubscribePaymentResponse(
    val resultCode: String,
    val resultMsg: String,
    val tid: String,
    val paidAt: String?,
    val cardName: String?,     // 카드사 이름
    val receiptUrl: String?    // 영수증 링크
)
