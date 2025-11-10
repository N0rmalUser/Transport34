package ru.normal.trans34.presentation.screen.map.utils

import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView


fun moveCameraToPoint(point: Point, mapView: MapView) {
    val map = mapView.mapWindow.map
    val current = map.cameraPosition
    val newCamera = CameraPosition(point, current.zoom, current.azimuth, current.tilt)
    map.move(newCamera, Animation(Animation.Type.SMOOTH, 0.7f), null)
}