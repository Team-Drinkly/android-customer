package com.project.drinkly.api.response.payment

data class DeleteCardResponse(
    val resultCode: String,
    val resultMsg: String,
    val tid: String,
    val paidAt: String?,
    val cardName: String?,
    val receiptUrl: String?
)