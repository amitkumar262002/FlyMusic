package com.example.flymusicai.manager

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UpdateInfo(
        @SerialName("version_name") val versionName: String,
        @SerialName("title") val title: String,
        @SerialName("message") val message: String,
        @SerialName("update_now_text") val updateNowText: String,
        @SerialName("later_text") val laterText: String,
        @SerialName("show_later_button") val showLaterButton: Boolean,
        @SerialName("update_link") val updateLink: String
)

class UpdateManager(private val context: Context) {
    private val client =
            HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                            Json {
                                ignoreUnknownKeys = true
                                coerceInputValues = true
                            }
                    )
                }
            }

    // Replace with your actual JSON URL
    private val UPDATE_JSON_URL =
            "https://raw.githubusercontent.com/amitkumar262002/FlyMusicAI/main/version.json"

    suspend fun checkForUpdate(): UpdateInfo? {
        return try {
            val response = client.get(UPDATE_JSON_URL)
            if (response.status == io.ktor.http.HttpStatusCode.OK) {
                val updateInfo: UpdateInfo = response.body()
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val currentVersionName = pInfo.versionName ?: "1.0" // Handle nullable string

                // Compare versions
                if (isNewVersion(currentVersionName, updateInfo.versionName)) {
                    updateInfo
                } else {
                    null
                }
            } else {
                // Only log non-404 errors to reduce spam (404 is expected if version.json doesn't exist)
                if (response.status != io.ktor.http.HttpStatusCode.NotFound) {
                    Log.e("UpdateManager", "Failed to fetch update info: ${response.status}")
                }
                null
            }
        } catch (e: Exception) {
            Log.e("UpdateManager", "Error checking for update", e)
            null
        }
    }

    private fun isNewVersion(current: String, remote: String): Boolean {
        return try {
            val currentParts = current.split(".").map { it.toInt() }
            val remoteParts = remote.split(".").map { it.toInt() }

            val length = maxOf(currentParts.size, remoteParts.size)

            for (i in 0 until length) {
                val c = if (i < currentParts.size) currentParts[i] else 0
                val r = if (i < remoteParts.size) remoteParts[i] else 0
                if (r > c) return true
                if (r < c) return false
            }
            false
        } catch (e: Exception) {
            // Fallback to simple string comparison if parsing fails
            remote != current
        }
    }
}
