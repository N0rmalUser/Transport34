package ru.normal.trans34.presentation.model

import com.yandex.mapkit.geometry.Point

data class UnitPointUiModel (
    val id: String,
    val routeNumber: String,
    val destination: String,
    val point: Point,
    val azimuth: String,
    val speed: String,
    val systemTime: String,
    val transportType: TransportType
)