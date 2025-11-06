package ru.normal.trans34.presentation.model

import com.yandex.mapkit.geometry.Point

data class StopPointUiModel (
    val id: Int,
    val point: Point,
    val destination: String
)