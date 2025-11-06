package ru.normal.trans34.presentation.screen.map

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.VisibleRegion
import ru.normal.trans34.presentation.model.StopPointUiModel

sealed class MapIntent {
    data class LoadStops(
        val center: Point,
        val visibleRegion: VisibleRegion
    ) : MapIntent()
    data class SelectStop(val stop: StopPointUiModel) : MapIntent()
    object DismissBottomSheet : MapIntent()
    data class ToggleStop(val stop: StopPointUiModel) : MapIntent()

}
