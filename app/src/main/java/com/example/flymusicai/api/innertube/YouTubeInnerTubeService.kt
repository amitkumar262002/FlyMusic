package com.example.flymusicai.api.innertube

import android.util.Log
import com.example.flymusicai.api.MusicStreamingService
import com.example.flymusicai.api.SongDetails
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*

class YouTubeInnerTubeService : MusicStreamingService {

        private val httpClient =
                HttpClient(OkHttp) {
                        engine {
                                config {
                                        connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                                        readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                                        writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                                }
                        }
                        install(ContentNegotiation) {
                                json(
                                        Json {
                                                ignoreUnknownKeys = true
                                                explicitNulls = false
                                        }
                                )
                        }
                }

        private val json = Json {
                ignoreUnknownKeys = true
                explicitNulls = false
        }

        private var gl: String = "IN"
        private var hl: String = "hi"

        fun setRegionAndLanguage(newGl: String, newHl: String) {
                gl = newGl
                hl = newHl
        }

        private fun YouTubeClientConfig.toDynamicInnerTubeContext(): InnerTubeContext {
                return InnerTubeContext(
                        InnerTubeContext.Client(
                                clientName = clientName,
                                clientVersion = clientVersion,
                                gl = gl,
                                hl = hl
                        )
                )
        }

        init {
                try {
                        org.schabi.newpipe.extractor.NewPipe.init(
                                com.example.flymusicai.api.NewPipeDownloader.getInstance()
                        )
                } catch (e: Exception) {
                        // Already initialized
                }
        }

        override suspend fun searchSong(title: String, artist: String): String? {
                val query = "$title $artist"
                return searchVideos(query).firstOrNull()?.id
        }

        suspend fun searchVideos(query: String): List<SongDetails> =
                withContext(Dispatchers.IO) {
                        try {
                                val client = YouTubeClients.WEB_REMIX
                                val response: String =
                                        httpClient
                                                .post(
                                                        "https://music.youtube.com/youtubei/v1/search"
                                                ) {
                                                        parameter("key", client.apiKey)
                                                        contentType(ContentType.Application.Json)
                                                        header("User-Agent", client.userAgent)
                                                        header(
                                                                "X-YouTube-Client-Name",
                                                                client.clientName
                                                        )
                                                        header(
                                                                "X-YouTube-Client-Version",
                                                                client.clientVersion
                                                        )
                                                        header(
                                                                "Referer",
                                                                "https://music.youtube.com/"
                                                        )
                                                        setBody(
                                                                SearchBody(
                                                                        context =
                                                                                client.toDynamicInnerTubeContext(),
                                                                        query = query,
                                                                        params =
                                                                                "EgWKAQIIAWoKEAkQBRAKEAMQBA%3D%3D" // Filter for songs
                                                                )
                                                        )
                                                }
                                                .body()

                                val root = json.parseToJsonElement(response).jsonObject
                                val contents =
                                        root["contents"]
                                                ?.jsonObject
                                                ?.get("tabbedSearchResultsRenderer")
                                                ?.jsonObject
                                                ?.get("tabs")
                                                ?.jsonArray
                                                ?.get(0)
                                                ?.jsonObject
                                                ?.get("tabRenderer")
                                                ?.jsonObject
                                                ?.get("content")
                                                ?.jsonObject
                                                ?.get("sectionListRenderer")
                                                ?.jsonObject
                                                ?.get("contents")
                                                ?.jsonArray

                                val songs = mutableListOf<SongDetails>()

                                contents?.forEach { section ->
                                        val shelf =
                                                section.jsonObject["musicShelfRenderer"]?.jsonObject
                                        shelf?.get("contents")?.jsonArray?.forEach { item ->
                                                val renderer =
                                                        item.jsonObject[
                                                                        "musicResponsiveListItemRenderer"]
                                                                ?.jsonObject
                                                if (renderer != null) {
                                                        val videoId =
                                                                renderer["playlistItemData"]
                                                                        ?.jsonObject?.get("videoId")
                                                                        ?.jsonPrimitive
                                                                        ?.content
                                                                        ?: renderer[
                                                                                        "navigationEndpoint"]
                                                                                ?.jsonObject
                                                                                ?.get(
                                                                                        "watchEndpoint"
                                                                                )
                                                                                ?.jsonObject
                                                                                ?.get("videoId")
                                                                                ?.jsonPrimitive
                                                                                ?.content

                                                        if (videoId != null) {
                                                                val title =
                                                                        renderer["flexColumns"]
                                                                                ?.jsonArray
                                                                                ?.get(0)
                                                                                ?.jsonObject
                                                                                ?.get(
                                                                                        "musicResponsiveListItemFlexColumnRenderer"
                                                                                )
                                                                                ?.jsonObject
                                                                                ?.get("text")
                                                                                ?.jsonObject
                                                                                ?.get("runs")
                                                                                ?.jsonArray
                                                                                ?.get(0)
                                                                                ?.jsonObject
                                                                                ?.get("text")
                                                                                ?.jsonPrimitive
                                                                                ?.content
                                                                                ?: "Unknown"

                                                                val artist =
                                                                        renderer["flexColumns"]
                                                                                ?.jsonArray
                                                                                ?.get(1)
                                                                                ?.jsonObject
                                                                                ?.get(
                                                                                        "musicResponsiveListItemFlexColumnRenderer"
                                                                                )
                                                                                ?.jsonObject
                                                                                ?.get("text")
                                                                                ?.jsonObject
                                                                                ?.get("runs")
                                                                                ?.jsonArray
                                                                                ?.get(0)
                                                                                ?.jsonObject
                                                                                ?.get("text")
                                                                                ?.jsonPrimitive
                                                                                ?.content
                                                                                ?: "Unknown"

                                                                val thumbnails =
                                                                        renderer["thumbnail"]
                                                                                ?.jsonObject
                                                                                ?.get(
                                                                                        "musicThumbnailRenderer"
                                                                                )
                                                                                ?.jsonObject
                                                                                ?.get("thumbnail")
                                                                                ?.jsonObject
                                                                                ?.get("thumbnails")
                                                                                ?.jsonArray
                                                                val thumbUrl =
                                                                        thumbnails
                                                                                ?.lastOrNull()
                                                                                ?.jsonObject
                                                                                ?.get("url")
                                                                                ?.jsonPrimitive
                                                                                ?.content
                                                                                ?: ""

                                                                songs.add(
                                                                        SongDetails(
                                                                                id = videoId,
                                                                                title = title,
                                                                                artist = artist,
                                                                                duration = 0,
                                                                                coverImageUrl =
                                                                                        thumbUrl,
                                                                                streamUrl =
                                                                                        "" // To be
                                                                                // fetched later
                                                                                )
                                                                )
                                                        }
                                                }
                                        }
                                }
                                songs
                        } catch (e: Exception) {
                                Log.e("InnerTubeService", "Search failed", e)
                                emptyList()
                        }
                }

        override suspend fun getSongStreamUrl(songId: String): String? {
                return withContext(Dispatchers.IO) {
                        val videoId = songId.removePrefix("yt_")

                        // 1. Try InnerTube ANDROID_VR FIRST (Very Robust for non-logged in)
                        try {
                            Log.d("InnerTubeService", "Attempting fast-path: ANDROID_VR for $videoId")
                            val url = fetchStreamUrl(YouTubeClients.ANDROID_VR, videoId)
                            if (!url.isNullOrEmpty()) {
                                Log.d("InnerTubeService", "✅ Stream URL obtained via ANDROID_VR (Fast Path)")
                                return@withContext url
                            }
                        } catch (e: Exception) {
                            Log.w("InnerTubeService", "ANDROID_VR failed: ${e.message}")
                        }

                        // 2. Try InnerTube ANDROID_TESTSUITE (Secondary Fast Path)
                        try {
                            Log.d("InnerTubeService", "Attempting fast-path: ANDROID_TESTSUITE for $videoId")
                            val url = fetchStreamUrl(YouTubeClients.ANDROID_TESTSUITE, videoId)
                            if (!url.isNullOrEmpty()) {
                                Log.d("InnerTubeService", "✅ Stream URL obtained via ANDROID_TESTSUITE")
                                return@withContext url
                            }
                        } catch (e: Exception) {
                            Log.w("InnerTubeService", "ANDROID_TESTSUITE failed: ${e.message}")
                        }

                        // 2. Try NewPipe Extractor (Highly Reliable, handles signatures)
                        try {
                                Log.d(
                                        "InnerTubeService",
                                        "Attempting NewPipe extractor for $videoId"
                                )
                                val service = org.schabi.newpipe.extractor.ServiceList.YouTube
                                val contentUrl = "https://www.youtube.com/watch?v=$videoId"
                                val streamInfo =
                                        org.schabi.newpipe.extractor.stream.StreamInfo.getInfo(
                                                service,
                                                contentUrl
                                        )
                                val url =
                                        streamInfo.audioStreams.maxByOrNull { it.bitrate }?.content
                                if (!url.isNullOrEmpty()) {
                                        Log.d(
                                                "InnerTubeService",
                                                "✅ Stream URL obtained via NewPipe"
                                        )
                                        return@withContext url
                                }
                        } catch (e: Exception) {
                                Log.w(
                                        "InnerTubeService",
                                        "NewPipe extraction failed, trying other InnerTube fallbacks: ${e.message}"
                                )
                        }

                        // 3. Try other InnerTube Clients as fallback
                        val clients =
                                listOf(
                                        YouTubeClients.ANDROID_MUSIC,
                                        YouTubeClients.ANDROID,
                                        YouTubeClients.WEB,
                                        YouTubeClients.MWEB,
                                        YouTubeClients.TVHTML5
                                )

                        for (client in clients) {
                                Log.d(
                                        "InnerTubeService",
                                        "Trying InnerTube client: ${client.clientName}"
                                )
                                val url = fetchStreamUrl(client, videoId)
                                if (!url.isNullOrEmpty()) {
                                        Log.d(
                                                "InnerTubeService",
                                                "✅ Stream URL obtained via ${client.clientName}"
                                        )
                                        return@withContext url
                                }
                        }

                        Log.e("InnerTubeService", "❌ All extraction methods failed for $videoId")
                        null
                }
        }

        private suspend fun fetchStreamUrl(client: YouTubeClientConfig, videoId: String): String? {
                return try {
                        val response: String =
                                httpClient
                                        .post("https://www.youtube.com/youtubei/v1/player") {
                                                parameter("key", client.apiKey)
                                                contentType(ContentType.Application.Json)
                                                header("User-Agent", client.userAgent)
                                                header("X-YouTube-Client-Name", client.clientName)
                                                header(
                                                        "X-YouTube-Client-Version",
                                                        client.clientVersion
                                                )
                                                header("Referer", "https://music.youtube.com/")
                                                setBody(
                                                        PlayerBody(
                                                                context =
                                                                        client.toDynamicInnerTubeContext(),
                                                                videoId = videoId,
                                                                playbackContext =
                                                                        PlaybackContext(
                                                                                contentPlaybackContext =
                                                                                        ContentPlaybackContext(
                                                                                                signatureTimestamp =
                                                                                                        20241
                                                                                        )
                                                                        )
                                                        )
                                                )
                                        }
                                        .body()

                        val root = json.parseToJsonElement(response).jsonObject

                        // Log playability status if streaming data is missing
                        if (!root.contains("streamingData")) {
                                val status = root["playabilityStatus"]?.jsonObject
                                val statusText = status?.get("status")?.jsonPrimitive?.content
                                val reason = status?.get("reason")?.jsonPrimitive?.content
                                Log.w(
                                        "InnerTubeService",
                                        "Client ${client.clientName} failed for $videoId. Status: $statusText, Reason: $reason"
                                )
                        }

                        val streamingData = root["streamingData"]?.jsonObject
                        val adaptiveFormats = streamingData?.get("adaptiveFormats")?.jsonArray

                        // Prefer audio/mp4 or audio/webm
                        val bestFormat =
                                adaptiveFormats
                                        ?.map { it.jsonObject }
                                        ?.filter {
                                                val mime =
                                                        it["mimeType"]?.jsonPrimitive?.content ?: ""
                                                mime.contains("audio")
                                        }
                                        ?.maxByOrNull { it["bitrate"]?.jsonPrimitive?.int ?: 0 }

                        val url = bestFormat?.get("url")?.jsonPrimitive?.content
                        if (url != null) {
                                Log.d(
                                        "InnerTubeService",
                                        "✅ Stream URL obtained from ${client.clientName}: ${url.take(50)}..."
                                )
                        }
                        url
                } catch (e: Exception) {
                        Log.e("InnerTubeService", "Fetch failed for ${client.clientName}", e)
                        null
                }
        }

        /** Get related songs for a given video ID */
        suspend fun getRelatedSongs(videoId: String): List<SongDetails> =
                withContext(Dispatchers.IO) {
                        try {
                                val client = YouTubeClients.WEB_REMIX
                                val id = videoId.removePrefix("yt_")

                                // Fetch next songs
                                val response: String =
                                        httpClient
                                                .post(
                                                        "https://music.youtube.com/youtubei/v1/next"
                                                ) {
                                                        parameter("key", client.apiKey)
                                                        contentType(ContentType.Application.Json)
                                                        header("User-Agent", client.userAgent)
                                                        header(
                                                                "X-YouTube-Client-Name",
                                                                client.clientName
                                                        )
                                                        header(
                                                                "X-YouTube-Client-Version",
                                                                client.clientVersion
                                                        )
                                                        setBody(
                                                                PlayerBody(
                                                                        context =
                                                                                client.toInnerTubeContext(),
                                                                        videoId = id
                                                                )
                                                        )
                                                }
                                                .body()

                                val root = json.parseToJsonElement(response).jsonObject

                                // Parse related songs from the response
                                // This path can be complex, let's look for
                                // musicResponsiveListItemRenderer in contents
                                // A simpler way often involves browsing the 'related' tab, but
                                // 'next' endpoint gives suggestions too

                                val songs = mutableListOf<SongDetails>()
                                // Simplified extraction for now - searching for videoId in the tree
                                // In a real implementation we would parse the specific
                                // recommendation shelf

                                // For now, let's just use the search method with artist/title of
                                // the song if we had it
                                // But getRelatedSongs is better.

                                songs
                        } catch (e: Exception) {
                                emptyList()
                        }
                }

        suspend fun getSearchSuggestions(query: String): List<String> =
                withContext(Dispatchers.IO) {
                        try {
                                val client = YouTubeClients.WEB_REMIX
                                val response: String =
                                        httpClient
                                                .post(
                                                        "https://music.youtube.com/youtubei/v1/music/get_search_suggestions"
                                                ) {
                                                        parameter("key", client.apiKey)
                                                        contentType(ContentType.Application.Json)
                                                        header("User-Agent", client.userAgent)
                                                        header(
                                                                "X-YouTube-Client-Name",
                                                                client.clientName
                                                        )
                                                        header(
                                                                "X-YouTube-Client-Version",
                                                                client.clientVersion
                                                        )
                                                        header(
                                                                "Referer",
                                                                "https://music.youtube.com/"
                                                        )
                                                        setBody(
                                                                SearchBody(
                                                                        context =
                                                                                client.toDynamicInnerTubeContext(),
                                                                        query = query
                                                                )
                                                        )
                                                }
                                                .body()

                                val root = json.parseToJsonElement(response).jsonObject
                                val contents =
                                        root["contents"]
                                                ?.jsonArray
                                                ?.get(0)
                                                ?.jsonObject
                                                ?.get("searchSuggestionsSectionRenderer")
                                                ?.jsonObject
                                                ?.get("contents")
                                                ?.jsonArray

                                contents?.mapNotNull { item ->
                                        item.jsonObject["searchSuggestionRenderer"]
                                                ?.jsonObject
                                                ?.get("suggestion")
                                                ?.jsonObject
                                                ?.get("runs")
                                                ?.jsonArray
                                                ?.get(0)
                                                ?.jsonObject
                                                ?.get("text")
                                                ?.jsonPrimitive
                                                ?.content
                                }
                                        ?: emptyList()
                        } catch (e: Exception) {
                                emptyList()
                        }
                }

        suspend fun getTrendingMusic(): List<SongDetails> = withContext(Dispatchers.IO) {
            try {
                val client = YouTubeClients.WEB_REMIX
                val response: String = httpClient.post("https://music.youtube.com/youtubei/v1/browse") {
                    parameter("key", client.apiKey)
                    contentType(ContentType.Application.Json)
                    header("User-Agent", client.userAgent)
                    header("X-YouTube-Client-Name", client.clientName)
                    header("X-YouTube-Client-Version", client.clientVersion)
                    setBody(BrowseBody(
                        context = client.toDynamicInnerTubeContext(),
                        browseId = "FEmusic_trending"
                    ))
                }.body()

                val root = json.parseToJsonElement(response).jsonObject
                val contents = root["contents"]?.jsonObject
                    ?.get("sectionListRenderer")?.jsonObject
                    ?.get("contents")?.jsonArray

                val songs = mutableListOf<SongDetails>()
                contents?.forEach { section ->
                    val shelf = section.jsonObject["musicShelfRenderer"]?.jsonObject
                    shelf?.get("contents")?.jsonArray?.forEach { item ->
                        val renderer = item.jsonObject["musicResponsiveListItemRenderer"]?.jsonObject
                        if (renderer != null) {
                            val videoId = renderer["playlistItemData"]?.jsonObject?.get("videoId")?.jsonPrimitive?.content
                            if (videoId != null) {
                                val title = renderer["flexColumns"]?.jsonArray?.get(0)?.jsonObject
                                    ?.get("musicResponsiveListItemFlexColumnRenderer")?.jsonObject
                                    ?.get("text")?.jsonObject?.get("runs")?.jsonArray?.get(0)?.jsonObject
                                    ?.get("text")?.jsonPrimitive?.content ?: "Unknown"

                                val artist = renderer["flexColumns"]?.jsonArray?.get(1)?.jsonObject
                                    ?.get("musicResponsiveListItemFlexColumnRenderer")?.jsonObject
                                    ?.get("text")?.jsonObject?.get("runs")?.jsonArray?.get(0)?.jsonObject
                                    ?.get("text")?.jsonPrimitive?.content ?: "Unknown"

                                val thumbUrl = renderer["thumbnail"]?.jsonObject
                                    ?.get("musicThumbnailRenderer")?.jsonObject
                                    ?.get("thumbnail")?.jsonObject?.get("thumbnails")?.jsonArray?.lastOrNull()
                                    ?.jsonObject?.get("url")?.jsonPrimitive?.content ?: ""

                                songs.add(SongDetails(id = videoId, title = title, artist = artist, duration = 0, coverImageUrl = thumbUrl, streamUrl = ""))
                            }
                        }
                    }
                }
                songs
            } catch (e: Exception) {
                Log.e("InnerTubeService", "Get trending failed", e)
                emptyList()
            }
        }

        override suspend fun getSongDetails(songId: String): SongDetails? {
                return null
        }
}
