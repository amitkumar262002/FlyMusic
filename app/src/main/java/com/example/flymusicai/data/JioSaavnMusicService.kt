package com.example.flymusicai.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class FlyMusicAIMusicService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
            )
        }
    }

    suspend fun searchSongs(query: String, limit: Int = 10): List<Music> {
        val url =
                "https://www.jiosaavn.com/api.php?__call=search.getResults&_format=json&_marker=0&ctx=web6dot0&p=1&q=$query&n=$limit"
        return try {
            val response = client.get(url)
            val responseBody: String = response.body()
            val json =
                    Json.parseToJsonElement(
                            responseBody.substring(
                                    responseBody.indexOf('{'),
                                    responseBody.lastIndexOf('}') + 1
                            )
                    )
            val results = json.jsonObject["results"]?.jsonArray

            results?.mapNotNull { result ->
                try {
                    Music(
                            id = result.jsonObject["id"]!!.jsonPrimitive.content,
                            title = result.jsonObject["title"]!!.jsonPrimitive.content,
                            artist =
                                    result.jsonObject["more_info"]!!.jsonObject["artistMap"]!!
                                                                    .jsonObject["primary_artists"]!!
                                                            .jsonArray[0]
                                                    .jsonObject["name"]!!
                                            .jsonPrimitive
                                            .content,
                            album =
                                    result.jsonObject["more_info"]!!.jsonObject["album"]!!
                                            .jsonPrimitive
                                            .content,
                            duration =
                                    result.jsonObject["more_info"]!!.jsonObject["duration"]!!
                                            .jsonPrimitive.content.toInt(),
                            coverImageUrl =
                                    result.jsonObject["image"]!!.jsonPrimitive.content.replace(
                                            "150x150",
                                            "500x500"
                                    ),
                            audioUrl =
                                    result.jsonObject["more_info"]!!.jsonObject[
                                                    "encrypted_media_url"]!!
                                            .jsonPrimitive
                                            .content
                    )
                } catch (e: Exception) {
                    Log.e("FlyMusicAIMusicService", "Error parsing song: ${e.message}")
                    null
                }
            }
                    ?: emptyList()
        } catch (e: Exception) {
            Log.e("FlyMusicAIMusicService", "Error fetching songs: ${e.message}")
            emptyList()
        }
    }
}
