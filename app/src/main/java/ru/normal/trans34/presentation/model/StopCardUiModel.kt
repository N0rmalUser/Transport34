package ru.normal.trans34.presentation.model

data class StopCardUiModel(
    val id: Int,
    val title: String,
    val arrivalTime: String,
    val minutesUntilArrival: Int
)