@file:Suppress("OPT_IN_IS_NOT_ENABLED")

package ru.normal.trans34.presentation.screen.map.component

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.edit
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import ru.normal.trans34.R

private const val PREFS_NAME = "permissions_prefs"
private const val KEY_REQUESTED_FINE_LOCATION = "requested_fine_location"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationButton(
    onLocationAvailable: () -> Unit,
    permission: String = android.Manifest.permission.ACCESS_FINE_LOCATION
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    val requestedBefore =
        remember { mutableStateOf(prefs.getBoolean(KEY_REQUESTED_FINE_LOCATION, false)) }

    val permissionState = rememberPermissionState(permission = permission)

    var showSettingsDialog by remember { mutableStateOf(false) }

    FilledTonalIconButton(
        modifier = Modifier
            .size(48.dp)
            .zIndex(1f),
        shape = RoundedCornerShape(12.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
        ),
        onClick = {
            when (permissionState.status) {
                is PermissionStatus.Granted -> {
                    onLocationAvailable()
                }

                is PermissionStatus.Denied -> {
                    val shouldShowRationale =
                        (permissionState.status as PermissionStatus.Denied).shouldShowRationale
                    when {
                        !shouldShowRationale && requestedBefore.value -> {
                            showSettingsDialog = true
                        }

                        shouldShowRationale -> {
                            prefs.edit { putBoolean(KEY_REQUESTED_FINE_LOCATION, true) }
                            requestedBefore.value = true
                            permissionState.launchPermissionRequest()
                        }

                        else -> {
                            prefs.edit { putBoolean(KEY_REQUESTED_FINE_LOCATION, true) }
                            requestedBefore.value = true
                            permissionState.launchPermissionRequest()
                        }
                    }
                }
            }
        }) {
        Icon(
            imageVector = Icons.Filled.MyLocation,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = "My location"
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(stringResource(R.string.location_permission_title)) },
            text = { Text(stringResource(R.string.location_permission_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    context.startActivity(intent)
                }) {
                    Text(stringResource(R.string.location_permission_open_settings))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text(stringResource(R.string.location_permission_cancel))
                }
            })
    }
}
