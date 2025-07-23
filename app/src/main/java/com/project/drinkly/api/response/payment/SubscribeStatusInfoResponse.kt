package com.project.drinkly.api.response.payment

data class SubscribeStatusInfoResponse(
    val status: String,
    val failedCount: Int,
    val existYn: Boolean
)
