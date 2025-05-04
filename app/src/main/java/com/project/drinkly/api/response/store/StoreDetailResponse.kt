package com.project.drinkly.api.response.store

data class StoreDetailResponse(
    val storeId: Long,
    val ownerId: Long,
    val storeName: String,
    val storeMainImageUrl: String?,
    val storeDescription: String?,
    val isOpen: String,
    val isAvailable: Boolean,
    val openingInfo: String,
    val openingHours: List<StoreOpeningHour>,
    val storeTel: String,
    val storeAddress: String,
    val storeDetailAddress: String?,
    val instagramUrl: String?,
    val availableDays: String?,
    val latitude: String,
    val longitude: String,
    val availableDrinkImageUrls: List<StoreImageInfo>?,
    val menuImageUrls: List<StoreImageInfo>?,
    val isReady: Boolean
)

data class StoreOpeningHour(
    val day: String,
    val isOpen: Boolean,
    val openTime: String?,
    val closeTime: String?
)

data class StoreImageInfo(
    val imageId: Int,
    val imageUrl: String,
    val description: String
)
