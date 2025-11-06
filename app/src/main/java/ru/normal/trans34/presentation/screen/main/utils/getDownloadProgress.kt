package ru.normal.trans34.presentation.screen.main.utils

import android.app.DownloadManager
import android.content.Context


fun getDownloadProgress(context: Context, downloadId: Long): Int {
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val query = DownloadManager.Query().setFilterById(downloadId)
    manager.query(query).use { cursor ->
        if (cursor != null && cursor.moveToFirst()) {
            val total =
                cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
            val downloaded =
                cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            if (total > 0) {
                return ((downloaded * 100) / total).toInt()
            }
        }
    }
    return 0
}
