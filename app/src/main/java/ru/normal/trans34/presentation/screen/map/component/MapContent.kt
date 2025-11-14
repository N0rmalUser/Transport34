package ru.normal.trans34.presentation.screen.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import ru.normal.trans34.R
import ru.normal.trans34.presentation.screen.map.MapIntent
import ru.normal.trans34.presentation.screen.map.MapViewModel

@Composable
fun MapContent(
    mapView: MapView, userLocationLayerState: MutableState<UserLocationLayer?>
) {
    val viewModel: MapViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()
    var selectedIndex by remember {
        mutableIntStateOf(
            when {
                state.showSavedRoutes -> 1
                state.showUnits -> 2
                else -> 0
            }
        )
    }

    val options = listOf(
        stringResource(R.string.nothing),
        stringResource(R.string.my_transport),
        stringResource(R.string.all_transport)
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView }, modifier = Modifier.fillMaxWidth()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    FilledTonalIconButton(
                        modifier = Modifier
                            .size(48.dp)
                            .zIndex(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
                        ),
                        onClick = {
                            val layer = userLocationLayerState.value ?: return@FilledTonalIconButton
                            val target =
                                layer.cameraPosition()?.target ?: return@FilledTonalIconButton
                            val currentCamera = mapView.mapWindow.map.cameraPosition
                            val newCamera = CameraPosition(
                                target, 15.5f, currentCamera.azimuth, currentCamera.tilt
                            )
                            mapView.mapWindow.map.move(
                                newCamera, Animation(Animation.Type.SMOOTH, 0.7f), null
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            contentDescription = "My location"
                        )
                    }
                }
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size
                    ), colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.surfaceContainer.copy(
                            alpha = 0.8f
                        ),
                        inactiveContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    ), onClick = {
                        selectedIndex = index
                        viewModel.handleIntent(
                            MapIntent.SetVisibility(
                                showSavedRoutes = index == 1, showUnits = index == 2
                            )
                        )
                    }, selected = index == selectedIndex, label = { Text(label) })
                }
            }
        }
    }
}
