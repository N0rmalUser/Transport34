package ru.normal.trans34.domain.entity

data class UnitPoint (
    val id: String,
    val routeNumber: String,
    val destinationRu: String,
    val destinationEn: String,
    val transportType: Int,
    val speed: String,
    val latitude: Double,
    val longitude: Double,
    val azimuth: String,
    val systemTime: String
)