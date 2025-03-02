package com.project.drinkly.api.response.login

data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String
)
