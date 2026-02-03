package com.example.flymusicai.api

import kotlinx.serialization.Serializable

@Serializable
data class SongDetails(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val thumbnailUrl: String = "",
    val duration: String = "0:00",
    val provider: String = "youtube"
)

interface MusicStreamingService {
    suspend fun searchSong(title: String, artist: String): String?
    suspend fun getSongStreamUrl(songId: String): String?
    suspend fun getRelatedSongs(songId: String): List<SongDetails>
    suspend fun getTrendingMusic(): List<SongDetails>
    suspend fun getSearchSuggestions(query: String): List<String>
}
