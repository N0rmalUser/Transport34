package ru.normal.trans34.presentation.model

import androidx.compose.ui.graphics.Color

enum class TransportType(val color: Color) {
    BUS(Color(0xFF4CAF50)),
    ELECTROBUS(Color(0xFF0090FD)),
    TROLLEY(Color(0xFFFF9800)),
    TRAM(Color(0xFF6C1DDE)),
    OTHER(Color.Companion.Gray)
}