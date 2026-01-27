package com.example.flymusicai.data

/** Repository class that provides music data mainly from YouTube Music */
object MusicRepository {

    // Enable YouTube integration
    var useYouTubeContent = true

    /** Get all music including YouTube content */
    suspend fun getAllMusicWithYouTube(): List<Music> {
        return try {
            YouTubeMusicRepository.getTrendingMusic(40)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /** Search music including YouTube */
    suspend fun searchMusicWithYouTube(query: String): List<Music> {
        return try {
            YouTubeMusicRepository.searchMusic(query)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /** Get search suggestions based on query */
    suspend fun getSearchSuggestions(query: String, limit: Int = 10): List<String> {
        return try {
            YouTubeMusicRepository.getSearchSuggestions(query).take(limit)
        } catch (e: Exception) {
            listOf(query, "$query song", "$query music")
        }
    }

    /** Get playlists including YouTube results */
    suspend fun getPlaylistsWithYouTube(): List<Playlist> {
        return try {
            YouTubeMusicRepository.getYouTubePlaylists()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
