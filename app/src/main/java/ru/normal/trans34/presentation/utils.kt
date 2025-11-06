package ru.normal.trans34.presentation

import ru.normal.trans34.domain.entity.Route
import ru.normal.trans34.presentation.model.TransportType


fun mapTransportType(route: Route): TransportType = when (route.transportType) {
    1 -> TransportType.BUS
    2 -> TransportType.TROLLEYBUS
    3 -> TransportType.TRAM
    4 -> TransportType.OTHER
    5 -> TransportType.ELECTROBUS
    else -> TransportType.OTHER
}

fun computeMinutesUntil(arrivalTime: String): Int = try {
    val (hour, minute) = arrivalTime.split(":").map { it.toInt() }
    val now = java.time.LocalTime.now()
    val arrival = java.time.LocalTime.of(hour, minute)
    val diff = java.time.Duration.between(now, arrival).toMinutes().toInt()
    if (diff >= 0) diff else 0
} catch (e: Exception) { 0 }