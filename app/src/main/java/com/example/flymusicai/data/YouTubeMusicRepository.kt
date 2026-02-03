package com.example.flymusicai.data

import android.util.Log
import com.example.flymusicai.api.YouTubeMusicService
import com.example.flymusicai.api.innertube.YouTubeInnerTubeService
import com.example.flymusicai.api.SongDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

/**
 * YouTube Music Repository Provides trending songs, playlists, and categories from InnerTube
 */
object YouTubeMusicRepository {

    private val innerTubeService = YouTubeInnerTubeService()

    /** 🛡️ Smart Filter: Removes duplicates (covers, making of, teaser, same song) */
    private fun filterUniqueSongs(songs: List<Music>): List<Music> {
        val seenTitles = mutableSetOf<String>()
        val filtered = mutableListOf<Music>()
        
        val noiseWords = listOf("making of", "teaser", "promo", "vlog", "bts", "interview", "full movie", "behind the scenes", "live performing", "reaction")
        
        songs.forEach { song ->
            val titleLower = song.title.lowercase()
            if (noiseWords.any { titleLower.contains(it) }) return@forEach
            
            val normalizedTitle = titleLower
                .replace(Regex("\\(.*?\\)"), "")
                .replace(Regex("\\[.*?\\]"), "")
                .replace(Regex("\\b202[0-9]\\b"), "")
                .replace("official audio", "")
                .replace("official video", "")
                .replace("lyric video", "")
                .replace("full audio", "")
                .trim()
                .replace(Regex("\\s+"), " ")
            
            if (seenTitles.add(normalizedTitle)) {
                filtered.add(song)
            }
        }
        return filtered
    }

    fun updateRegionAndLanguage(gl: String, hl: String) {
        innerTubeService.setRegionAndLanguage(gl, hl)
    }

    suspend fun getTrendingMusic(limit: Int = 20): List<Music> =
        withContext(Dispatchers.IO) {
            try {
                val songs = innerTubeService.getTrendingMusic().take(limit)
                songs.map { s: SongDetails -> s.toMusic("Trending") }
            } catch (e: Exception) {
                // Fallback to search if browse fails
                val searchSongs = innerTubeService.searchVideos("trending music india 2024").take(limit)
                searchSongs.map { s: SongDetails -> s.toMusic("Trending") }
            }
        }

    suspend fun getMusicByCategory(category: String, limit: Int = 100): List<Music> =
        withContext(Dispatchers.IO) {
            try {
                val query = when (category.lowercase()) {
                    "bollywood", "hindi" -> "bollywood hindi hit songs"
                    "punjabi" -> "punjabi latest hits"
                    "english", "pop" -> "top pop songs"
                    "romantic" -> "new romantic hindi songs"
                    "party" -> "latest party music"
                    "sadabahar", "old" -> "old hindi sadabahar songs hits"
                    "bhojpuri" -> "latest bhojpuri hit songs"
                    "haryanvi" -> "latest haryanvi songs hits"
                    "workout" -> "gym workout motivation music"
                    else -> "$category music"
                }

                val songs = innerTubeService.searchVideos(query).take(limit)
                songs.map { s: SongDetails -> s.toMusic(category) }
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun searchMusic(query: String, limit: Int = 100): List<Music> =
        withContext(Dispatchers.IO) {
            try {
                val songs = innerTubeService.searchVideos(query).take(limit)
                songs.map { s: SongDetails -> s.toMusic("Search") }
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun getSearchSuggestions(query: String): List<String> =
        withContext(Dispatchers.IO) { innerTubeService.getSearchSuggestions(query) }

    suspend fun getRelatedMusic(musicId: String, title: String? = null, artist: String? = null, limit: Int = 50): List<Music> =
        withContext(Dispatchers.IO) {
            try {
                val id = musicId.removePrefix("yt_")
                val related = innerTubeService.getRelatedSongs(id)
                if (related.isNotEmpty()) {
                    return@withContext related.take(limit).map { s: SongDetails -> s.toMusic("Recommended") }
                }
                
                val query = if (title != null) "more like $title $artist music" else "more like $id music"
                val suggestions = innerTubeService.searchVideos(query).take(limit)
                suggestions.map { s: SongDetails -> s.toMusic("Recommended") }
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun getYouTubePlaylists(): List<Playlist> =
        withContext(Dispatchers.IO) {
            try {
                val categories = listOf(
                    "Bollywood Hits" to "Bollywood",
                    "Punjabi Beats" to "Punjabi",
                    "Romantic Melodies" to "Romantic",
                    "Party" to "Party",
                    "Sadabahar" to "Sadabahar"
                )

                categories.map { (name, type) ->
                    val songs = getMusicByCategory(type, 30)
                    Playlist(
                        id = "yt_playlist_${type.lowercase()}",
                        name = name,
                        description = "Top $name for you",
                        coverImageUrl = songs.firstOrNull()?.coverImageUrl 
                                ?: ArtistConstants.CATEGORY_IMAGES[type] 
                                ?: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&q=80",
                        songs = songs,
                        category = PlaylistCategory.TRENDING
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun getIndiaRising(): List<Music> = withContext(Dispatchers.IO) {
        val keywords = listOf("latest trending songs india 2025", "new viral songs india")
        val allSongs = keywords.flatMap { kw ->
            try { innerTubeService.searchVideos(kw).take(30).map { s: SongDetails -> s.toMusic("India Rising") } }
            catch (e: Exception) { emptyList() }
        }
        filterUniqueSongs(allSongs).distinctBy { it.id }.take(100)
    }

    suspend fun getHindiHits(): List<Music> = withContext(Dispatchers.IO) {
        val songs = try { innerTubeService.searchVideos("latest bollywood hindi hits 2025").take(40).map { s: SongDetails -> s.toMusic("Hindi Hits") } }
        catch (e: Exception) { emptyList() }
        filterUniqueSongs(songs).distinctBy { it.id }
    }

    suspend fun getPopularBhojpuri(): List<Music> = withContext(Dispatchers.IO) {
        val songs = try { innerTubeService.searchVideos("latest bhojpuri hits 2025").take(40).map { s: SongDetails -> s.toMusic("Bhojpuri") } }
        catch (e: Exception) { emptyList() }
        filterUniqueSongs(songs).distinctBy { it.id }
    }

    suspend fun getRomanceNow(): List<Music> = withContext(Dispatchers.IO) {
        val songs = try { innerTubeService.searchVideos("trending romantic hindi songs 2025").take(40).map { s: SongDetails -> s.toMusic("Romance") } }
        catch (e: Exception) { emptyList() }
        filterUniqueSongs(songs).distinctBy { it.id }
    }

    suspend fun getBestOf90s(): List<Music> = withContext(Dispatchers.IO) {
        val songs = try { innerTubeService.searchVideos("best of 90s bollywood hindi songs").take(40).map { s: SongDetails -> s.toMusic("90s") } }
        catch (e: Exception) { emptyList() }
        filterUniqueSongs(songs).distinctBy { it.id }
    }

    suspend fun getAlbumsForYou(): List<Playlist> = withContext(Dispatchers.IO) {
        try {
            val queries = listOf("Animal movie songs", "Pushpa 2 songs", "Jawan songs", "Arijit Singh hits")
            queries.mapIndexed { index, q ->
                val songs = innerTubeService.searchVideos(q).take(30).map { s: SongDetails -> s.toMusic("Album") }
                Playlist(
                    id = "yt_album_$index",
                    name = q.replace("songs", "").trim(),
                    description = "Popular Collection",
                    coverImageUrl = songs.firstOrNull()?.coverImageUrl ?: "",
                    songs = songs,
                    category = PlaylistCategory.ALBUM
                )
            }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getTopIndianSingles(): List<Music> = withContext(Dispatchers.IO) {
        try {
            val songs = innerTubeService.searchVideos("latest hits 2025 india").take(50).map { s: SongDetails -> s.toMusic("Singles") }
            filterUniqueSongs(songs).distinctBy { it.id }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getCharts(limit: Int = 50): List<Music> = withContext(Dispatchers.IO) {
        try {
            val songs = innerTubeService.getMusicCharts().take(limit)
            songs.map { s: SongDetails -> s.toMusic("Charts") }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getMoodsAndGenres(): List<Pair<String, String>> = withContext(Dispatchers.IO) {
        innerTubeService.getMoodsAndGenres()
    }

    // Bridge extension
    private fun SongDetails.toMusic(genre: String): Music {
        return Music(
            id = id, // id already has "yt_" prefix
            title = title,
            artist = artist,
            duration = 300,
            coverImageUrl = if (thumbnailUrl.isEmpty()) "https://i.ytimg.com/vi/${id.removePrefix("yt_")}/hqdefault.jpg" else thumbnailUrl,
            audioUrl = "",
            genre = genre,
            year = 2025,
            playCount = 0
        )
    }
}
