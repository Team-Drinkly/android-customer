package com.project.drinkly.api.response.store

data class StoreListResponse(
    val id: Int,
    val storeName: String?,
    val storeMainImageUrl: String?,
    val latitude: String?,
    val longitude: String?,
    val isOpen: String?,
    val openingInfo: String?,
    val storeTel: String?,
    val storeAddress: String?,
    val availableDrinks: List<String>?
)
