package ru.normal.trans34.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BarItem(
    val title: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector,
    val route: String
)
