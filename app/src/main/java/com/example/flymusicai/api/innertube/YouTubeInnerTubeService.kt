package com.example.flymusicai.api.innertube

import android.util.Log
import com.example.flymusicai.api.MusicStreamingService
import com.example.flymusicai.api.SaavnService
import com.example.flymusicai.api.SongDetails
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

class YouTubeInnerTubeService : MusicStreamingService {

    private val saavnService = SaavnService()
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true 
                explicitNulls = false
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 20000
        }
    }

    private val json = Json { ignoreUnknownKeys = true; explicitNulls = false }
    private var gl = "IN"
    private var hl = "hi"

    fun setRegionAndLanguage(gl: String, hl: String) {
        this.gl = gl
        this.hl = hl
    }

    override suspend fun searchSong(title: String, artist: String): String? {
        val query = "$title $artist"
        return searchVideos(query).firstOrNull()?.id
    }

    suspend fun searchVideos(query: String): List<SongDetails> = withContext(Dispatchers.IO) {
        try {
            val client = YouTubeClients.WEB_REMIX
            val response: String = httpClient.post("https://music.youtube.com/youtubei/v1/search") {
                parameter("key", client.apiKey)
                contentType(ContentType.Application.Json)
                header("User-Agent", client.userAgent)
                header("X-YouTube-Client-Name", client.clientName)
                header("X-YouTube-Client-Version", client.clientVersion)
                setBody(SearchBody(
                    context = client.toInnerTubeContext(gl = gl, hl = hl),
                    query = query,
                    params = "EgWKAQIIAWoREAMQBBAJEAoQDRAEEAoQBA%3D%3D" // Filter for songs
                ))
            }.body()

            val root = json.parseToJsonElement(response).jsonObject
            val songs = mutableListOf<SongDetails>()
            findSongs(root, songs)
            songs
        } catch (e: Exception) {
            Log.e("InnerTubeService", "Search failed for: $query", e)
            emptyList()
        }
    }

    override suspend fun getSongStreamUrl(songId: String): String? = withContext(Dispatchers.IO) {
        val videoId = songId.removePrefix("yt_")
        Log.d("InnerTubeService", "📡 Resolving stream for: $videoId")

        // Priority List: ANDROID -> IOS -> WEB_REMIX -> TVHTML5
        val clients = listOf(
            YouTubeClients.ANDROID,
            YouTubeClients.IOS,
            YouTubeClients.WEB_REMIX,
            YouTubeClients.TVHTML5
        )

        for (client in clients) {
            try {
                val url = fetchStreamUrl(client, videoId)
                if (!url.isNullOrEmpty()) {
                    Log.d("InnerTubeService", "✅ Success: [${client.clientName}]")
                    return@withContext url
                }
            } catch (e: Exception) {
                Log.w("InnerTubeService", "⚠️ [${client.clientName}] failed: ${e.message}")
            }
        }

        // Fallback to Piped (Try multiple instances)
        val pipedInstances = listOf(
            "https://pipedapi.kavin.rocks",
            "https://api.piped.private.coffee",
            "https://pipedapi.drgns.space"
        )

        for (instance in pipedInstances) {
            try {
                Log.d("InnerTubeService", "📡 Trying Piped API Fallback ($instance)...")
                val url = fetchFromPiped(instance, videoId)
                if (!url.isNullOrEmpty()) return@withContext url
            } catch (e: Exception) {
                Log.w("InnerTubeService", "❌ Piped instance failed: $instance")
            }
        }

        // LAST RESORT: Try Saavn if it's a known song
        try {
            Log.d("InnerTubeService", "📡 Trying Saavn Fallback as last resort...")
            val metadata = getSongMetadata(videoId)
            if (metadata != null) {
                val searchStr = "${metadata.first} ${metadata.second}"
                val saavnResults = saavnService.searchSongs(searchStr)
                val firstMatch = saavnResults.firstOrNull()
                if (firstMatch != null) {
                    val sUrl = saavnService.getStreamUrl(firstMatch.id)
                    if (sUrl != null) {
                        Log.d("InnerTubeService", "✅ Success: [Saavn Fallback]")
                        return@withContext sUrl
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("InnerTubeService", "❌ Saavn fallback failed", e)
        }

        Log.e("InnerTubeService", "💀 CRITICAL: All extraction methods failed for $videoId")
        null
    }

    private suspend fun getSongMetadata(videoId: String): Pair<String, String>? {
        return try {
            val client = YouTubeClients.WEB_REMIX
            val response: String = httpClient.post("https://music.youtube.com/youtubei/v1/player") {
                parameter("key", client.apiKey)
                setBody(PlayerBody(
                    context = client.toInnerTubeContext(gl = gl, hl = hl),
                    videoId = videoId
                ))
            }.body()
            val root = json.parseToJsonElement(response).jsonObject
            val details = root["videoDetails"]?.jsonObject
            val title = details?.get("title")?.jsonPrimitive?.content ?: ""
            val artist = details?.get("author")?.jsonPrimitive?.content ?: ""
            if (title.isNotEmpty()) title to artist else null
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchStreamUrl(client: YouTubeClientConfig, videoId: String): String? {
        val responseText: String = httpClient.post("https://www.youtube.com/youtubei/v1/player") {
            parameter("key", client.apiKey)
            contentType(ContentType.Application.Json)
            header("User-Agent", client.userAgent)
            header("X-YouTube-Client-Name", client.clientName)
            header("X-YouTube-Client-Version", client.clientVersion)
            header("X-Goog-Api-Format-Version", "2")
            header("Origin", "https://music.youtube.com")
            header("Referer", "https://music.youtube.com/")
            
            setBody(PlayerBody(
                context = client.toInnerTubeContext(videoId, gl = gl, hl = hl),
                videoId = videoId,
                playbackContext = PlaybackContext(
                    contentPlaybackContext = ContentPlaybackContext(signatureTimestamp = 20836) // Updated timestamp
                )
            ))
        }.body()

        val root = json.parseToJsonElement(responseText).jsonObject
        val status = root["playabilityStatus"]?.jsonObject?.get("status")?.jsonPrimitive?.content
        
        if (status != "OK") {
            val reason = root["playabilityStatus"]?.jsonObject?.get("reason")?.jsonPrimitive?.content
            Log.w("InnerTubeService", "Status: $status, Reason: $reason")
            return null
        }

        val streamingData = root["streamingData"]?.jsonObject ?: return null
        val formats = mutableListOf<JsonObject>()
        streamingData["adaptiveFormats"]?.jsonArray?.forEach { formats.add(it.jsonObject) }
        streamingData["formats"]?.jsonArray?.forEach { formats.add(it.jsonObject) }

        val bestAudioFormat = formats
            .filter { it["mimeType"]?.jsonPrimitive?.content?.contains("audio") == true }
            .maxByOrNull { it["bitrate"]?.jsonPrimitive?.intOrNull ?: 0 }

        var url = bestAudioFormat?.get("url")?.jsonPrimitive?.content

        // Simplistic cipher handling
        if (url == null && bestAudioFormat?.containsKey("signatureCipher") == true) {
            val cipher = bestAudioFormat["signatureCipher"]?.jsonPrimitive?.content ?: ""
            val params = cipher.split("&").associate { 
                val parts = it.split("=")
                if (parts.size == 2) parts[0] to java.net.URLDecoder.decode(parts[1], "UTF-8")
                else "" to ""
            }
            url = params["url"]?.let { baseUrl ->
                val sig = params["s"]
                val sp = params["sp"] ?: "sig"
                if (sig != null) "$baseUrl&$sp=$sig" else baseUrl
            }
        }

        return url
    }

    private suspend fun fetchFromPiped(baseUrl: String, videoId: String): String? {
        return try {
            val response: String = httpClient.get("$baseUrl/streams/$videoId").body()
            val root = json.parseToJsonElement(response).jsonObject
            val audioStreams = root["audioStreams"]?.jsonArray
            audioStreams?.maxByOrNull { it.jsonObject["bitrate"]?.jsonPrimitive?.intOrNull ?: 0 }
                ?.jsonObject?.get("url")?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getRelatedSongs(songId: String): List<SongDetails> = withContext(Dispatchers.IO) {
        try {
            val client = YouTubeClients.WEB_REMIX
            val response: String = httpClient.post("https://music.youtube.com/youtubei/v1/next") {
                parameter("key", client.apiKey)
                setBody(NextBody(
                    context = client.toInnerTubeContext(gl = gl, hl = hl),
                    videoId = songId.removePrefix("yt_")
                ))
            }.body()
            val root = json.parseToJsonElement(response).jsonObject
            val songs = mutableListOf<SongDetails>()
            findSongs(root, songs)
            songs.distinctBy { it.id }.filter { it.id != songId }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getTrendingMusic(): List<SongDetails> = withContext(Dispatchers.IO) {
        try {
            val client = YouTubeClients.WEB_REMIX
            val response: String = httpClient.post("https://music.youtube.com/youtubei/v1/browse") {
                parameter("key", client.apiKey)
                setBody(BrowseBody(
                    context = client.toInnerTubeContext(gl = gl, hl = hl),
                    browseId = "FEmusic_trending"
                ))
            }.body()
            val root = json.parseToJsonElement(response).jsonObject
            val songs = mutableListOf<SongDetails>()
            findSongs(root, songs)
            songs
        } catch (e: Exception) {
            searchVideos("trending music india")
        }
    }

    override suspend fun getSearchSuggestions(query: String): List<String> = withContext(Dispatchers.IO) {
        try {
            val client = YouTubeClients.WEB_REMIX
            val response: String = httpClient.post("https://music.youtube.com/youtubei/v1/music/get_search_suggestions") {
                parameter("key", client.apiKey)
                setBody(SearchSuggestionsBody(
                    context = client.toInnerTubeContext(gl = gl, hl = hl),
                    input = query
                ))
            }.body()
            val root = json.parseToJsonElement(response).jsonObject
            val contents = root["contents"]?.jsonArray ?: emptyList()
            val suggestions = mutableListOf<String>()
            
            contents.forEach { content ->
                val suggestionRenderer = content.jsonObject["searchSuggestionRenderer"]?.jsonObject
                val suggestion = suggestionRenderer?.get("suggestion")?.jsonObject?.get("runs")?.jsonArray?.getOrNull(0)?.jsonObject?.get("text")?.jsonPrimitive?.content
                if (suggestion != null) suggestions.add(suggestion)
            }
            suggestions
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getMusicCharts(): List<SongDetails> = getTrendingMusic()

    suspend fun getMoodsAndGenres(): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        listOf(
            "Chill" to "chill",
            "Workout" to "workout",
            "Focus" to "focus",
            "Party" to "party",
            "Sleep" to "sleep",
            "Romance" to "romantic"
        )
    }

    private fun findSongs(element: JsonElement, songs: MutableList<SongDetails>) {
        if (element is JsonObject) {
            if (element.containsKey("musicResponsiveListItemRenderer")) {
                val renderer = element["musicResponsiveListItemRenderer"]?.jsonObject
                if (renderer != null) {
                    parseSongRenderer(renderer)?.let { songs.add(it) }
                }
            } else if (element.containsKey("playlistItemData")) {
                parseSongRenderer(element)?.let { songs.add(it) }
            } else {
                element.values.forEach { findSongs(it, songs) }
            }
        } else if (element is JsonArray) {
            element.forEach { findSongs(it, songs) }
        }
    }

    private fun parseSongRenderer(renderer: JsonObject): SongDetails? {
        try {
            val flexColumns = renderer["flexColumns"]?.jsonArray
            val title = if (flexColumns != null) {
                flexColumns.getOrNull(0)?.jsonObject?.get("musicResponsiveListItemFlexColumnRenderer")?.jsonObject
                    ?.get("text")?.jsonObject?.get("runs")?.jsonArray?.getOrNull(0)?.jsonObject?.get("text")?.jsonPrimitive?.content ?: "Unknown"
            } else {
                renderer["title"]?.jsonObject?.get("runs")?.jsonArray?.getOrNull(0)?.jsonObject?.get("text")?.jsonPrimitive?.content ?: "Unknown"
            }
            
            val artist = if (flexColumns != null && flexColumns.size > 1) {
                flexColumns.getOrNull(1)?.jsonObject?.get("musicResponsiveListItemFlexColumnRenderer")?.jsonObject
                    ?.get("text")?.jsonObject?.get("runs")?.jsonArray?.getOrNull(0)?.jsonObject?.get("text")?.jsonPrimitive?.content ?: "Unknown"
            } else "Unknown"

            val videoId = renderer["playlistItemData"]?.jsonObject?.get("videoId")?.jsonPrimitive?.content
                ?: renderer["navigationEndpoint"]?.jsonObject?.get("watchEndpoint")?.jsonObject?.get("videoId")?.jsonPrimitive?.content
                ?: return null

            val thumbnails = renderer["thumbnail"]?.jsonObject?.get("musicThumbnailRenderer")?.jsonObject?.get("thumbnail")?.jsonObject?.get("thumbnails")?.jsonArray
            val thumbnailUrl = thumbnails?.lastOrNull()?.jsonObject?.get("url")?.jsonPrimitive?.content ?: ""

            return SongDetails(
                id = "yt_$videoId",
                title = title,
                artist = artist,
                thumbnailUrl = thumbnailUrl
            )
        } catch (e: Exception) {
            return null
        }
    }
}

@Serializable
data class NextBody(val context: InnerTubeContext, val videoId: String)

@Serializable
data class BrowseBody(val context: InnerTubeContext, val browseId: String)

@Serializable
data class SearchSuggestionsBody(val context: InnerTubeContext, val input: String)
