package com.example.flymusicai.api

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

class SaavnService {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; explicitNulls = false })
        }
    }

    // Use a more reliable public instance for Saavn API
    private val baseUrl = "https://saavn.dev" 

    suspend fun searchSongs(query: String): List<SongDetails> = withContext(Dispatchers.IO) {
        try {
            val response: String = httpClient.get("$baseUrl/api/search/songs") {
                parameter("query", query)
            }.body()
            
            val json = Json { ignoreUnknownKeys = true }
            val root = json.parseToJsonElement(response).jsonObject
            val data = root["data"]?.jsonObject?.get("results")?.jsonArray ?: emptyList()
            
            data.map { it.jsonObject }.map { song ->
                val images = song["image"]?.jsonArray
                val thumb = images?.lastOrNull()?.jsonObject?.get("url")?.jsonPrimitive?.content ?: ""
                
                SongDetails(
                    id = "saavn_${song["id"]?.jsonPrimitive?.content}",
                    title = song["name"]?.jsonPrimitive?.content ?: "",
                    artist = song["primaryArtists"]?.jsonPrimitive?.content ?: "",
                    thumbnailUrl = thumb
                )
            }
        } catch (e: Exception) {
            Log.e("SaavnService", "Search failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun getStreamUrl(songId: String): String? = withContext(Dispatchers.IO) {
        try {
            val realId = songId.removePrefix("saavn_")
            val response: String = httpClient.get("$baseUrl/api/songs") {
                parameter("ids", realId)
            }.body()
            
            val json = Json { ignoreUnknownKeys = true }
            val root = json.parseToJsonElement(response).jsonObject
            val data = root["data"]?.jsonArray
            val song = data?.getOrNull(0)?.jsonObject
            
            val downloadUrls = song?.get("downloadUrl")?.jsonArray
            // Get highest quality url ( Usually last in the array is 320kbps )
            downloadUrls?.lastOrNull()?.jsonObject?.get("url")?.jsonPrimitive?.content
        } catch (e: Exception) {
            Log.e("SaavnService", "Get stream failed: ${e.message}")
            null
        }
    }
}
