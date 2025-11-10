package ru.normal.trans34.presentation.screen.map.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import ru.normal.trans34.presentation.model.MapControlButtonModel

@Composable
fun MapContent(
    mapView: MapView,
    userLocationLayerState: MutableState<UserLocationLayer?>
) {
    Box{
        AndroidView(factory = { mapView }, modifier = Modifier.matchParentSize())
        val userLocationButton = MapControlButtonModel(
            onClick = {
                val layer = userLocationLayerState.value ?: return@MapControlButtonModel
                val target = layer.cameraPosition()?.target ?: return@MapControlButtonModel
                val currentCamera = mapView.mapWindow.map.cameraPosition
                val newCamera = CameraPosition(
                    target, 15.5f, currentCamera.azimuth, currentCamera.tilt
                )
                mapView.mapWindow.map.move(
                    newCamera, Animation(Animation.Type.SMOOTH, 0.7f), null
                )
            },
            icon = Icons.Filled.MyLocation,
            contentDescription = "Переместиться к геолокации"
        )
        OnMapControls(buttons = listOf(userLocationButton))

    }
}