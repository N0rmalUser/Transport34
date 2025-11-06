package ru.normal.trans34.presentation.screen.schedule

sealed class ScheduleIntent {
    data class LoadData(val stopId: Int, val indicator: Boolean = false) : ScheduleIntent()
    data class SelectStop(val stopId: Int) : ScheduleIntent()
    data object Refresh : ScheduleIntent()
    data class DelStop(val stopId: Int): ScheduleIntent()
}
