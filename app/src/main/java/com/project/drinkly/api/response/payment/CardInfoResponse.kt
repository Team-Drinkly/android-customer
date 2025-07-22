package com.project.drinkly.api.response.payment

data class CardInfoResponse(
    val orderId: String,
    val cardName: String,
    val cardFirst: Int,
    val cardLast: Int
)
