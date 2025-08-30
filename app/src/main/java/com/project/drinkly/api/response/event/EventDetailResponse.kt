package com.project.drinkly.api.response.event

data class EventDetailResponse(
    val eventId: Int,
    val eventImagePaths: List<String>?,
    val title: String,
    val status: String,
    val dDay: String,
    val startDate: String,
    val endDate: String,
    val benefit: String,
    val description: String,
    val eventCategory: String,
    val redirectUrl: String?
)
