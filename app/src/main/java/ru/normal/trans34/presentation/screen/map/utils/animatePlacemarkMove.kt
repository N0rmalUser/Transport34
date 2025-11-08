package ru.normal.trans34.presentation.screen.map.utils

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.abs

suspend fun animatePlacemarkMove(
    placemark: PlacemarkMapObject,
    start: Point,
    end: Point,
    steps: Int = 25,
    durationMs: Long = 500L
) = withContext(Dispatchers.Main) {
    if (!placemark.isValid) return@withContext

    val deltaLat = (end.latitude - start.latitude) / steps
    val deltaLon = (end.longitude - start.longitude) / steps
    val delayPerStep = durationMs / steps

    for (i in 1..steps) {
        if (!placemark.isValid) return@withContext
        val newLat = start.latitude + deltaLat * i
        val newLon = start.longitude + deltaLon * i
        try {
            placemark.geometry = Point(newLat, newLon)
        } catch (e: Exception) {
            return@withContext
        }
        delay(delayPerStep)
    }

    if (placemark.isValid &&
        (abs(placemark.geometry.latitude - end.latitude) > 1e-6 ||
                abs(placemark.geometry.longitude - end.longitude) > 1e-6)
    ) {
        try {
            placemark.geometry = end
        } catch (_: Exception) {}
    }
}
