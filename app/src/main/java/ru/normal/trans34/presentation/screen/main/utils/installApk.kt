package ru.normal.trans34.presentation.screen.main.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


fun installApk(context: Context, apkFile: File) {
    val intent = Intent(Intent.ACTION_VIEW)
    val uri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        apkFile
    )
    intent.setDataAndType(uri, "application/vnd.android.package-archive")
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
    context.startActivity(intent)
}
