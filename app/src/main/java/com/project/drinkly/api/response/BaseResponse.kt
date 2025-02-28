package com.project.drinkly.api.response

data class BaseResponse<T>(
    val result: BaseResult,
    val payload: T?
)

data class BaseResult(
    val code: Int,
    val message: String
)