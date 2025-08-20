package com.project.drinkly.api.response.event

data class DrinklyEventResponse(
    val eventId: Int,
    val eventCategory: String,
    val thumbnailPath: String,
    val title: String,
    val status: String,
    val dDay: String
)
