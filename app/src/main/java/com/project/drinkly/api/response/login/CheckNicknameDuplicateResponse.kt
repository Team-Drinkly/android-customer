package com.project.drinkly.api.response.login

data class CheckNicknameDuplicateResponse(
    val nickname: String,
    val isExist: Boolean
)