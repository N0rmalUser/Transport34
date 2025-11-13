package ru.normal.trans34.presentation.screen.map

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.VisibleRegion
import ru.normal.trans34.presentation.model.StopPointUiModel
import ru.normal.trans34.presentation.model.UnitPointUiModel

sealed class MapIntent {
    data class LoadData(
        val center: Point,
        val visibleRegion: VisibleRegion
    ) : MapIntent()
    data class SelectStop(val stop: StopPointUiModel) : MapIntent()
    data class SelectUnit(val unit: UnitPointUiModel) : MapIntent()
    object DismissBottomSheet : MapIntent()
    object RefreshUnits : MapIntent()
    data class ToggleStop(val stop: StopPointUiModel) : MapIntent()
    data class ToggleUnit(val unit: UnitPointUiModel) : MapIntent()
    data class ToggleUnitsVisibility(val show: Boolean) : MapIntent()
}
