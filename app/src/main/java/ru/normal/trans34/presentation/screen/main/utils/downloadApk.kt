package ru.normal.trans34.presentation.screen.main.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri


fun downloadApk(context: Context, url: String, fileName: String): Long {
    val request = DownloadManager.Request(url.toUri())
        .setTitle("Downloading update")
        .setDescription("Downloading...")
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    return manager.enqueue(request)
}