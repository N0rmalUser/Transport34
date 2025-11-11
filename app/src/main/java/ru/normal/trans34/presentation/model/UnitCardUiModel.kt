package ru.normal.trans34.presentation.model

data class UnitCardUiModel (
    val routeId: Int,
    val routeNumber: String,
    val destination: String,
    val arrivalTime: String,
    val minutesUntilArrival: Int,
    val transportType: TransportType
)