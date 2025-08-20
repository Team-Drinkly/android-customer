package com.project.drinkly.api.response.event

data class GeneralEventResponse(
    val externalEventId: Int,
    val thumbnailPath: String,
    val title: String,
    val organizer: String,
    val description: String,
    val redirectUrl: String,
    val status: String
)
