package com.example.flymusicai.api

/** Implement this for different music providers (Spotify, FlyMusicAI, YouTube Music, etc.) */
interface MusicStreamingService {
    /** Search for a song and return its ID */
    suspend fun searchSong(title: String, artist: String): String?

    /** Get streaming URL for a song by its ID */
    suspend fun getSongStreamUrl(songId: String): String?

    /** Get song metadata (optional) */
    suspend fun getSongDetails(songId: String): SongDetails?
}

/** Song details from streaming service */
data class SongDetails(
        val id: String,
        val title: String,
        val artist: String,
        val album: String? = null,
        val duration: Int,
        val coverImageUrl: String,
        val streamUrl: String,
        val previewUrl: String? = null, // 30-second preview
        val quality: String = "128kbps"
)

/**
 * FlyMusicAI Implementation - Best for Indian Music
 *
 * NOTE: This uses an internal music API For production, consider official partnerships or other
 * licensed services
 */
class FlyMusicAIMusicService : MusicStreamingService {

    private val baseUrl = "https://www.jiosaavn.com/api.php"

    // Hardcoded sample for demonstration purposes
    private val sampleAudioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"

    override suspend fun searchSong(title: String, artist: String): String? {
        try {
            // Simulation: Return a mock ID if valid query
            if (title.isNotBlank() || artist.isNotBlank()) {
                return "mock_song_id_${System.currentTimeMillis()}"
            }
            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun getSongStreamUrl(songId: String): String? {
        try {
            // Simulation: Return a valid playable URL
            return sampleAudioUrl
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun getSongDetails(songId: String): SongDetails? {
        try {
            // Simulation: Return mock details
            return SongDetails(
                    id = songId,
                    title = "Demo Song",
                    artist = "Demo Artist",
                    duration = 300,
                    coverImageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&q=80",
                    streamUrl = sampleAudioUrl,
                    quality = "320kbps"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}

/**
 * Spotify Implementation - Premium Quality
 *
 * Requires:
 * 1. Spotify Developer Account
 * 2. Client ID and Client Secret
 * 3. Spotify Android SDK
 *
 * Add to build.gradle: implementation("com.spotify.android:auth:1.2.5")
 */
class SpotifyMusicService(private val clientId: String, private val clientSecret: String) :
        MusicStreamingService {

    private val spotifyApiUrl = "https://api.spotify.com/v1"

    override suspend fun searchSong(title: String, artist: String): String? {
        try {
            val query = "$title $artist".replace(" ", "+")

            // TODO: Implement Spotify API authentication and search
            // Need OAuth token first

            return null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun getSongStreamUrl(songId: String): String? {
        // Spotify uses spotify:track:${trackId} format
        // Requires Spotify Premium and Android SDK for streaming
        return "spotify:track:$songId"
    }

    override suspend fun getSongDetails(songId: String): SongDetails? {
        // TODO: Implement Spotify track details API
        return null
    }
}

/**
 * YouTube Music Implementation - Free Option
 *
 * Requires:
 * 1. Google Cloud Project
 * 2. YouTube Data API v3 enabled
 * 3. API Key
 *
 * Add to build.gradle:
 * implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.1.0")
 */
class YouTubeMusicService(private val apiKey: String = "") : MusicStreamingService {

    init {
        try {
            org.schabi.newpipe.extractor.NewPipe.init(NewPipeDownloader.getInstance())
        } catch (e: Exception) {
            // Already initialized
        }
    }

    override suspend fun searchSong(title: String, artist: String): String? {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val service = org.schabi.newpipe.extractor.ServiceList.YouTube
                val query = "$title $artist"
                val qh = service.searchQHFactory.fromQuery(query, listOf("videos"), "")
                val searchInfo = org.schabi.newpipe.extractor.search.SearchInfo.getInfo(service, qh)

                // Get first video result
                val items = searchInfo.relatedItems
                if (items.isNotEmpty()) {
                    val firstItem = items[0]
                    firstItem.url?.replace("https://www.youtube.com/watch?v=", "")
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun getSongStreamUrl(songId: String): String? {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val videoId = songId.removePrefix("yt_")
            val url = "https://www.youtube.com/watch?v=$videoId"
            
            // 1. Try NewPipe Extractor FIRST (Reliable for most)
            try {
                android.util.Log.d("NewPipeService", "Extracting stream via NewPipe: $url")
                val service = org.schabi.newpipe.extractor.ServiceList.YouTube
                val streamInfo = org.schabi.newpipe.extractor.stream.StreamInfo.getInfo(service, url)

                val audioStreams = streamInfo.audioStreams
                if (audioStreams.isNotEmpty()) {
                    val bestStream = audioStreams.maxByOrNull { it.bitrate }
                    android.util.Log.d("NewPipeService", "✅ Stream found via NewPipe: ${bestStream?.bitrate}bps")
                    return@withContext bestStream?.content
                }
            } catch (e: Exception) {
                android.util.Log.w("NewPipeService", "NewPipe failed for $videoId: ${e.message}")
            }

            // 2. Try InnerTube Fallback (Very robust for restricted content)
            try {
                android.util.Log.d("NewPipeService", "NewPipe failed, attempting InnerTube fallback for $videoId")
                val innerTubeService = com.example.flymusicai.api.innertube.YouTubeInnerTubeService()
                val ytUrl = innerTubeService.getSongStreamUrl(videoId)
                if (!ytUrl.isNullOrEmpty()) {
                    android.util.Log.d("NewPipeService", "✅ Stream found via InnerTube Fallback")
                    return@withContext ytUrl
                }
            } catch (e: Exception) {
                android.util.Log.e("NewPipeService", "InnerTube fallback also failed", e)
            }

            android.util.Log.e("NewPipeService", "❌ All extraction methods failed for $videoId")
            null
        }
    }

    override suspend fun getSongDetails(songId: String): SongDetails? {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val url = "https://www.youtube.com/watch?v=$songId"
                val service = org.schabi.newpipe.extractor.ServiceList.YouTube
                val streamInfo =
                        org.schabi.newpipe.extractor.stream.StreamInfo.getInfo(service, url)

                SongDetails(
                        id = songId,
                        title = streamInfo.name,
                        artist = streamInfo.uploaderName,
                        duration = streamInfo.duration.toInt(),
                        coverImageUrl = streamInfo.thumbnails.maxByOrNull { it.width }?.url
                                ?: streamInfo.thumbnails.firstOrNull()?.url
                                ?: "",
                        streamUrl = streamInfo.audioStreams.maxByOrNull { it.bitrate }?.content
                                        ?: "",
                        quality = "High"
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun searchVideos(query: String): List<SongDetails> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val service = org.schabi.newpipe.extractor.ServiceList.YouTube
                val qh = service.searchQHFactory.fromQuery(query, listOf("videos"), "")
                val searchInfo = org.schabi.newpipe.extractor.search.SearchInfo.getInfo(service, qh)
                
                searchInfo.relatedItems
                    .filterIsInstance<org.schabi.newpipe.extractor.stream.StreamInfoItem>()
                    .map { item ->
                        SongDetails(
                            id = item.url.replace("https://www.youtube.com/watch?v=", ""),
                            title = item.name,
                            artist = item.uploaderName,
                            duration = item.duration.toInt(),
                            coverImageUrl = item.thumbnails.maxByOrNull { it.width }?.url 
                                    ?: item.thumbnails.firstOrNull()?.url 
                                    ?: "",
                            streamUrl = "", // Fetch when needed
                            quality = "High"
                        )
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getSearchSuggestions(query: String): List<String> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val service = org.schabi.newpipe.extractor.ServiceList.YouTube
                service.suggestionExtractor.suggestionList(query)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

/** Music Service Factory Choose which music service to use */
object MusicServiceFactory {

    enum class ServiceType {
        FLYMUSICAI, // Best for Indian music, free
        SPOTIFY, // Premium quality, requires subscription
        YOUTUBE_MUSIC, // Free, video-based
        LOCAL // Local files only
    }

    fun getService(type: ServiceType): MusicStreamingService? {
        return when (type) {
            ServiceType.FLYMUSICAI -> FlyMusicAIMusicService()
            ServiceType.SPOTIFY -> {
                // Requires API credentials
                // SpotifyMusicService(clientId = "YOUR_CLIENT_ID", clientSecret = "YOUR_SECRET")
                null
            }
            ServiceType.YOUTUBE_MUSIC -> {
                YouTubeMusicService()
            }
            ServiceType.LOCAL -> null // Use local files
        }
    }

    // Default service for the app
    val currentService: MusicStreamingService? = getService(ServiceType.YOUTUBE_MUSIC)
}
