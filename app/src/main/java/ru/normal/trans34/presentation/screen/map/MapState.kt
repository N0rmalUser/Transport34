package ru.normal.trans34.presentation.screen.map

import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import ru.normal.trans34.presentation.model.StopCardUiModel
import ru.normal.trans34.presentation.model.UnitCardUiModel
import ru.normal.trans34.presentation.model.StopPointUiModel
import ru.normal.trans34.presentation.model.UnitPointUiModel

data class MapState(
    val position: CameraPosition,
    val animation: Animation,
    val stops: List<StopPointUiModel>? = null,
    val units: List<UnitPointUiModel>? = null,
    val selectedStop: StopPointUiModel? = null,
    val selectedUnit: UnitPointUiModel? = null,
    val routesByStop: Map<Int, List<UnitCardUiModel>> = emptyMap(),
    val stopsByUnit: Map<String, List<StopCardUiModel>> = emptyMap(),
    val showUnits: Boolean = true,
    val error: String? = null
)