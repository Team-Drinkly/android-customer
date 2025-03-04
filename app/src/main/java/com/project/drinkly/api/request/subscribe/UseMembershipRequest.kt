package com.project.drinkly.api.request.subscribe

data class UseMembershipRequest(
    val storeId: Long,
    val providedDrink: String
)
