package ru.normal.trans34.presentation.screen.main.component

import android.content.Intent
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.normal.trans34.R
import ru.normal.trans34.domain.entity.ReleaseInfo
import ru.normal.trans34.presentation.model.Style
import ru.normal.trans34.presentation.screen.main.utils.downloadApk
import ru.normal.trans34.presentation.screen.main.utils.formatMarkdown
import ru.normal.trans34.presentation.screen.main.utils.getDownloadProgress
import ru.normal.trans34.presentation.screen.main.utils.installApk
import java.io.File

@Composable
fun UpdateDialog(
    info: ReleaseInfo, onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var isDownloading by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0f) }

    fun startDownload() {
        scope.launch {
            isDownloading = true
            val fileName = "${context.packageName}-${info.version}.apk"

            val downloadId = downloadApk(
                context = context,
                url = info.downloadUrl,
                fileName = fileName
            )

            while (true) {
                val p = getDownloadProgress(context, downloadId)
                progress = p / 100f
                if (p >= 100) break
                delay(500)
            }

            isDownloading = false

            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                fileName
            )
            if (file.exists()) {
                installApk(context, file)
            }
        }
    }
    val installPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (context.packageManager.canRequestPackageInstalls()) {
            startDownload()
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.new_version, info.version)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )
            }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isDownloading) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val animatedProgress by animateFloatAsState(
                            targetValue = progress,
                            animationSpec = androidx.compose.animation.core.tween(
                                durationMillis = 800 // длительность анимации в мс
                            ),
                            label = "progressAnimation"
                        )

                        LinearWavyProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            color = ProgressIndicatorDefaults.linearColor
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            modifier = Modifier.padding(top = 12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                    ) {
                        val lines = formatMarkdown(info.changelog.trimIndent())
                        Column(modifier = Modifier.padding(12.dp)) {
                            lines.forEach { line ->
                                Text(
                                    text = when (line.style) {
                                        Style.HEADER1 -> line.text
                                        Style.HEADER2 -> line.text
                                        Style.HEADER3 -> line.text
                                        Style.LIST_ITEM -> "• ${line.text}"
                                        Style.NORMAL -> line.text
                                    },
                                    style = when (line.style) {
                                        Style.HEADER1 -> MaterialTheme.typography.titleLarge
                                        Style.HEADER2 -> MaterialTheme.typography.titleMedium
                                        Style.HEADER3 -> MaterialTheme.typography.titleSmall
                                        Style.LIST_ITEM -> MaterialTheme.typography.bodyMedium
                                        Style.NORMAL -> MaterialTheme.typography.bodyMedium
                                    },
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (!context.packageManager.canRequestPackageInstalls()) {
                                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                                    .setData("package:${context.packageName}".toUri())
                                installPermissionLauncher.launch(intent)
                            } else {
                                startDownload()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(stringResource(R.string.download_update))
                    }
                }
            }
        }
    }
}
