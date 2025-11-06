package ru.normal.trans34.domain.entity

data class SavedStop(
    val id: Int,
    val tabId: Int,
    val destinationRu: String,
    val destinationEn: String
)