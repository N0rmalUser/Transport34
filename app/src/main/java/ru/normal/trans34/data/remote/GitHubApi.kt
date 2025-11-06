package ru.normal.trans34.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import jakarta.inject.Inject
import org.json.JSONObject
import ru.normal.trans34.BuildConfig

class GitHubApi @Inject constructor(
    private val client: HttpClient
) {
    suspend fun getLatestRelease(): JSONObject? {
        return try {
            val response = client.get(
                "https://api.github.com/repos/${BuildConfig.GITHUB_USER}/${BuildConfig.REPO_NAME}/releases/latest"
            )
            val jsonText = response.bodyAsText()
            JSONObject(jsonText)
        } catch (e: Exception) {
            null
        }
    }
}
