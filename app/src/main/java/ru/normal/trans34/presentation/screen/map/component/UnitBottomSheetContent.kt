package ru.normal.trans34.presentation.screen.map.component

import androidx.compose.foundation.layout.Column
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
import ru.normal.trans34.presentation.model.StopCardUiModel
import ru.normal.trans34.presentation.model.UnitPointUiModel

@Composable
fun UnitBottomSheetContent(
    unit: UnitPointUiModel,
    timetable: List<StopCardUiModel>,
    isSaved: Boolean = false,
    onSaveUnit: (UnitPointUiModel) -> Unit
) {
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        unit.id, overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                actions = {
                    IconButton(onClick = { onSaveUnit(unit) }) {
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .navigationBarsPadding()
        ) {

        }

//        RouteList(
//            routes = timetable,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(paddingValues)
//                .navigationBarsPadding()
//        )
    }
}

