package com.project.drinkly.api.response.login

data class NiceUrlResponse(
    val token_version_id: String,
    val enc_data: String,
    val integrity_value: String
)
