package ru.normal.trans34.presentation.screen.schedule.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.normal.trans34.presentation.model.TransportType
import ru.normal.trans34.presentation.model.RouteUiModel

import androidx.compose.ui.res.stringResource
import ru.normal.trans34.R

@Composable
fun RouteCardItem(route: RouteUiModel) {
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
                            colors = listOf(route.transportType.color, cardColor)
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
                        text = "${route.routeNumber} → ${route.destination}",
                        fontSize = 16.sp,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(
                            id = R.string.route_arrival_time,
                            route.arrivalTime
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                val arrivalText = if (route.minutesUntilArrival == 0)
                    stringResource(R.string.route_arriving)
                else
                    stringResource(R.string.route_minutes_until, route.minutesUntilArrival)

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
fun RouteCardItemPreview() {
    Column {
        RouteCardItem(
            RouteUiModel(
                routeId = 0,
                routeNumber = "55",
                destination = "Детский центр (Б)",
                arrivalTime = "11:36",
                minutesUntilArrival = 0,
                transportType = TransportType.BUS
            )
        )
        RouteCardItem(RouteUiModel(
            routeId = 1,
            routeNumber = "5a",
            destination = "Родниковая долина",
            arrivalTime = "11:37",
            minutesUntilArrival = 1,
            transportType = TransportType.BUS
        ))
    }
}