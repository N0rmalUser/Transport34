package ru.normal.trans34.presentation.screen.schedule.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.normal.trans34.R
import ru.normal.trans34.presentation.model.TransportType
import ru.normal.trans34.presentation.model.UnitCardUiModel


@Composable
fun <T> GenericList(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    modifier: Modifier = Modifier
) {

    if (items.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_available_routes),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .animateContentSize()
            )
        }
    } else {
        LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(vertical = 2.dp, horizontal = 2.dp)
        ) {
            items(items) { item ->
                itemContent(item)
            }
        }
    }
}

@Preview
@Composable
fun RouteListPreview(
    modifier: Modifier = Modifier
) {
    GenericList(
        listOf(
            UnitCardUiModel(
                routeId = 0,
                routeNumber = "55",
                destination = "Детский центр (Б)",
                arrivalTime = "11:36",
                minutesUntilArrival = 0,
                transportType = TransportType.BUS
            ),
            UnitCardUiModel(
                routeId = 1,
                routeNumber = "5a",
                destination = "Родниковая долина",
                arrivalTime = "11:37",
                minutesUntilArrival = 1,
                transportType = TransportType.BUS
            ),
            UnitCardUiModel(
                routeId = 2,
                routeNumber = "15Э",
                destination = "Ж/д вокзал",
                arrivalTime = "11:38",
                minutesUntilArrival = 2,
                transportType = TransportType.BUS
            ),
            UnitCardUiModel(
                routeId = 3,
                routeNumber = "77",
                destination = "ТРЦ КомсоМОЛЛ (Б)",
                arrivalTime = "11:39",
                minutesUntilArrival = 3,
                transportType = TransportType.BUS
            ),
        ),
        itemContent = { route -> RouteCardItem(route) },
        modifier = modifier
    )
}
