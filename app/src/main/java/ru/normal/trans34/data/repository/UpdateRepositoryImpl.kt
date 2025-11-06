package ru.normal.trans34.data.repository

import jakarta.inject.Inject
import ru.normal.trans34.data.remote.GitHubApi
import ru.normal.trans34.domain.entity.ReleaseInfo
import ru.normal.trans34.domain.repository.UpdateRepository

class UpdateRepositoryImpl @Inject constructor(
    private val gitHubApi: GitHubApi
) : UpdateRepository {

    override suspend fun getLatestRelease(): ReleaseInfo {
        val obj = gitHubApi.getLatestRelease()
            ?: throw Exception("Couldn't get release data")

        val assetsArray = obj.optJSONArray("assets")
        val firstAsset = assetsArray?.optJSONObject(0)
        val downloadUrl = firstAsset?.optString("browser_download_url", "") ?: ""

        return ReleaseInfo(
            version = obj.optString("tag_name", ""),
            changelog = obj.optString("body", ""),
            downloadUrl = downloadUrl
        )
    }
}

