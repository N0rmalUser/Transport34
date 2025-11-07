package ru.normal.trans34.presentation.model

import androidx.compose.ui.graphics.Color

enum class TransportType(val color: Color) {
    BUS(Color(0xFF4CAF50)),
    ELECTROBUS(Color(0xA23BB6FF)),
    TROLLEY(Color(0xFFFF9800)),
    TRAM(Color(0xFF2196F3)),
    OTHER(Color.Companion.Gray)
}