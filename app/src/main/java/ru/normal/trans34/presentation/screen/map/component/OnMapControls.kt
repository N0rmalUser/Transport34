package ru.normal.trans34.presentation.screen.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.normal.trans34.presentation.model.MapControlButtonModel

@Composable
fun OnMapControls(
    modifier: Modifier = Modifier,
    buttons: List<MapControlButtonModel>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            buttons.forEach { button ->
                MapControlButton(
                    onClick = button.onClick,
                    icon = button.icon,
                    contentDescription = button.contentDescription
                )
            }
        }
    }
}
