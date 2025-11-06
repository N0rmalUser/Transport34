package ru.normal.trans34.presentation.screen.schedule

import ru.normal.trans34.presentation.model.RouteUiModel
import ru.normal.trans34.presentation.model.StopScheduleUiModel

data class ScheduleState(
    val isInitialLoading: Boolean = true,
    val stops: List<StopScheduleUiModel> = emptyList(),
    val selectedStop: Int = 0,
    val routesByStop: Map<Int, List<RouteUiModel>> = emptyMap(),
    val loadingStops: Set<Int> = emptySet(),
    val error: String? = null
) {
    fun isStopLoading(stopId: Int): Boolean = loadingStops.contains(stopId)
}
