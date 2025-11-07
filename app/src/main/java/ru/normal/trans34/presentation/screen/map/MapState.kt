package ru.normal.trans34.presentation.screen.map

import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import ru.normal.trans34.presentation.model.RouteUiModel
import ru.normal.trans34.presentation.model.StopPointUiModel
import ru.normal.trans34.presentation.model.UnitPointUiModel

data class MapState(
    val position: CameraPosition,
    val animation: Animation,
    val stops: List<StopPointUiModel>? = null,
    val units: List<UnitPointUiModel>? = null,
    val selectedStop: StopPointUiModel? = null,
    val routesByStop: Map<Int, List<RouteUiModel>> = emptyMap(),
    val error: String? = null
)