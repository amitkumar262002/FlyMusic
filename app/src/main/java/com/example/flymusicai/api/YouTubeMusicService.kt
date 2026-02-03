package com.example.flymusicai.api

import com.example.flymusicai.api.innertube.YouTubeInnerTubeService

class YouTubeMusicService : MusicStreamingService {
    private val innerTubeService = YouTubeInnerTubeService()

    override suspend fun searchSong(title: String, artist: String): String? {
        return innerTubeService.searchSong(title, artist)
    }

    override suspend fun getSongStreamUrl(songId: String): String? {
        return innerTubeService.getSongStreamUrl(songId)
    }

    override suspend fun getRelatedSongs(songId: String): List<SongDetails> {
        return innerTubeService.getRelatedSongs(songId)
    }

    override suspend fun getTrendingMusic(): List<SongDetails> {
        return innerTubeService.getTrendingMusic()
    }

    override suspend fun getSearchSuggestions(query: String): List<String> {
        return innerTubeService.getSearchSuggestions(query)
    }
}
