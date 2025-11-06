package ru.normal.trans34.data.repository

import jakarta.inject.Inject
import ru.normal.trans34.data.remote.GitHubApi
import ru.normal.trans34.domain.entity.ReleaseInfo
import ru.normal.trans34.domain.repository.UpdateRepository

class UpdateRepositoryImpl @Inject constructor(
    private val gitHubApi: GitHubApi
) : UpdateRepository {
    private fun getDeviceAbi(): String {
        val abi = android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a"
        return when {
            abi.contains("arm64") -> "arm64-v8a"
            abi.contains("armeabi") -> "armeabi-v7a"
            abi.contains("x86_64") -> "x86_64"
            abi.contains("x86") -> "x86"
            else -> "arm64-v8a"
        }
    }

    override suspend fun getLatestRelease(): ReleaseInfo {
        val obj = gitHubApi.getLatestRelease()
            ?: throw Exception("Couldn't get release data")

        val assetsArray = obj.optJSONArray("assets") ?: throw Exception("No assets in release")

        val deviceAbi = getDeviceAbi()
        var downloadUrl = ""

        for (i in 0 until assetsArray.length()) {
            val asset = assetsArray.optJSONObject(i)
            val name = asset?.optString("name", "") ?: ""
            if (name.contains(deviceAbi, ignoreCase = true)) {
                downloadUrl = asset.optString("browser_download_url", "")
                break
            }
        }

        if (downloadUrl.isEmpty()) {
            throw Exception("No matching APK found for ABI: $deviceAbi")
        }

        return ReleaseInfo(
            version = obj.optString("tag_name", ""),
            changelog = obj.optString("body", ""),
            downloadUrl = downloadUrl
        )
    }
}

