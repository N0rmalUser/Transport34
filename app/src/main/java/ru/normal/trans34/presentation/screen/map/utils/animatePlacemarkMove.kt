package ru.normal.trans34.presentation.screen.map.utils

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@OptIn(DelicateCoroutinesApi::class)
fun animatePlacemarkMove(
    placemark: PlacemarkMapObject,
    start: Point,
    end: Point,
    duration: Long = 1000L
) {
    GlobalScope.launch(Dispatchers.Main) {
        val steps = 30
        val stepDelay = duration / steps
        for (i in 1..steps) {
            val t = i.toFloat() / steps
            val lat = start.latitude + (end.latitude - start.latitude) * t
            val lon = start.longitude + (end.longitude - start.longitude) * t
            placemark.geometry = Point(lat, lon)
            delay(stepDelay)
        }
        placemark.geometry = end
    }
}
