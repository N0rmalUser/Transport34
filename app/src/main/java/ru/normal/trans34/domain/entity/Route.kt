package ru.normal.trans34.domain.entity

data class Route(
    val id: Int,
    val routeNumber: String,
    val destinationRu: String,
    val destinationEn: String,
    val arrivalTime: String,
    val systemTime: String,
    val transportType: Int
)