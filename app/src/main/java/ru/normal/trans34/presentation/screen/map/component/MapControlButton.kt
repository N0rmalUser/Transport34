package ru.normal.trans34.presentation.screen.map.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun MapControlButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String? = "description"
) {
    FilledTonalIconButton(
        modifier = Modifier
            .size(48.dp)
            .zIndex(1f),
        shape = RoundedCornerShape(12.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.7f)
        ),
        onClick = {
            onClick()
        }) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = contentDescription
        )
    }
}