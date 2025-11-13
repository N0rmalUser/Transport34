package ru.normal.trans34.presentation.screen.map.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import ru.normal.trans34.presentation.model.UnitCardUiModel
import ru.normal.trans34.presentation.model.StopPointUiModel
import ru.normal.trans34.presentation.screen.schedule.component.RouteList

@Composable
fun StopBottomSheetContent(
    stop: StopPointUiModel,
    timetable: List<UnitCardUiModel>,
    isSaved: Boolean = false,
    onSaveStop: (StopPointUiModel) -> Unit
) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stop.destination, overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                actions = {
                    IconButton(onClick = { onSaveStop(stop) }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.BookmarkAdded else Icons.Outlined.BookmarkAdd,
                            contentDescription = "Save stop",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        RouteList(
            routes = timetable,
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .navigationBarsPadding()
        )
    }
}

