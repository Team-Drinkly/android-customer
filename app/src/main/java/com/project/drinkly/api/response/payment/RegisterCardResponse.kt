package com.project.drinkly.api.response.payment

data class RegisterCardResponse(
    val cardName: String,
    val cardFirst: Int,
    val cardLast: Int,
    val resultCode: String,
    val resultMsg: String
)