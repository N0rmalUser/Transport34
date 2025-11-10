package ru.normal.trans34.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector


data class MapControlButtonModel(
    val onClick: () -> Unit,
    val icon: ImageVector,
    val contentDescription: String? = null
)
