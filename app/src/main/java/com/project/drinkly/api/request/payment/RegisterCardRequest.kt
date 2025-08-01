package com.project.drinkly.api.request.payment

data class RegisterCardRequest(
    val encData: String,
    val cardFirst: Int,
    val cardLast: Int
)
