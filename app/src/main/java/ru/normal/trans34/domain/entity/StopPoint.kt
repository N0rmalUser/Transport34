package ru.normal.trans34.domain.entity

data class StopPoint(
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val destinationRu: String,
    val destinationEn: String,
)