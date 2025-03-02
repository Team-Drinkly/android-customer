package com.project.drinkly.api.response.login

data class LoginResponse(
    val oauthId: Int,
    val isRegistered: Boolean,
    val accessToken: String,
    val refreshToken: String
)