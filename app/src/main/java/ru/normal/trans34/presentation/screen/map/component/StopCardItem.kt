package ru.normal.trans34.presentation.screen.map.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.normal.trans34.R
import ru.normal.trans34.presentation.model.StopCardUiModel
import ru.normal.trans34.presentation.model.TransportType
import ru.normal.trans34.presentation.model.UnitCardUiModel
import ru.normal.trans34.presentation.screen.schedule.component.RouteCardItem

@Composable
fun StopCardItem(
    stop: StopCardUiModel
) {
    val cardColor = MaterialTheme.colorScheme.surfaceContainerLow

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp)
            .height(IntrinsicSize.Min),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, cardColor)
                        )
                    )
                    .clip(
                        RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stop.title,
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(
                            id = R.string.route_arrival_time,
                            stop.arrivalTime
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                val arrivalText = if (stop.minutesUntilArrival == 0)
                    stringResource(R.string.route_arriving)
                else
                    stringResource(R.string.route_minutes_until, stop.minutesUntilArrival)

                Text(
                    text = arrivalText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}



@Preview
@Composable
fun StopCardItemPreview() {
    Column {
        StopCardItem(
            StopCardUiModel(
                id = 0,
                title = "Детский центр (Б)",
                arrivalTime = "11:36",
                minutesUntilArrival = 0,
            )
        )
        StopCardItem(StopCardUiModel(
            id = 1,
            title = "Родниковая долина",
            arrivalTime = "11:37",
            minutesUntilArrival = 1,
        ))
    }
}