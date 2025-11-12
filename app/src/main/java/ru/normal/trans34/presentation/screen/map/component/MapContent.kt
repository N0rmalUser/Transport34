package ru.normal.trans34.presentation.screen.map.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer

@Composable
fun MapContent(
    mapView: MapView,
    userLocationLayerState: MutableState<UserLocationLayer?>
) {
    Box{
        AndroidView(factory = { mapView }, modifier = Modifier.matchParentSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.End
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
                        val target = layer.cameraPosition()?.target ?: return@FilledTonalIconButton
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
    }
}