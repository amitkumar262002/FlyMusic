package com.example.flymusicai.ai

import com.example.flymusicai.data.Music
import com.example.flymusicai.data.Playlist
import com.example.flymusicai.data.PlaylistCategory

/**
 * AI-powered music recommendation engine (rule-based for now)
 * Can be enhanced with ML models in the future
 */
class AIRecommender {
    
    /**
     * Generate personalized recommendations based on user's favorite songs and listening history
     */
    fun getPersonalizedRecommendations(
        allMusic: List<Music>,
        favoriteSongs: List<Music>,
        listeningHistory: List<Music>,
        limit: Int = 10
    ): List<Music> {
        if (favoriteSongs.isEmpty() && listeningHistory.isEmpty()) {
            // If no user data, return trending songs
            return allMusic.sortedByDescending { it.playCount }.take(limit)
        }
        
        // Get favorite genres
        val favoriteGenres = (favoriteSongs + listeningHistory)
            .map { it.genre }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .map { it.key }
            .take(3)
        
        // Get favorite artists
        val favoriteArtists = (favoriteSongs + listeningHistory)
            .map { it.artist }
            .groupingBy { it }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .map { it.key }
            .take(3)
        
        // Score each song based on multiple factors
        val scoredMusic = allMusic.map { music ->
            var score = 0.0
            
            // Skip songs already in favorites
            if (favoriteSongs.any { it.id == music.id }) {
                return@map music to 0.0
            }
            
            // Genre match (40% weight)
            if (music.genre in favoriteGenres) {
                val genreRank = favoriteGenres.indexOf(music.genre)
                score += (3 - genreRank) * 40.0 / 3
            }
            
            // Artist match (30% weight)
            if (music.artist in favoriteArtists) {
                val artistRank = favoriteArtists.indexOf(music.artist)
                score += (3 - artistRank) * 30.0 / 3
            }
            
            // Popularity (20% weight)
            val maxPlayCount = allMusic.maxOfOrNull { it.playCount } ?: 1
            score += (music.playCount.toDouble() / maxPlayCount) * 20.0
            
            // Recency (10% weight)
            if (music.releaseYear == 2024) {
                score += 10.0
            } else if (music.releaseYear == 2023) {
                score += 5.0
            }
            
            music to score
        }
        
        return scoredMusic
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }
    
    /**
     * Generate "Recommended for You" playlist
     */
    fun generateRecommendedPlaylist(
        allMusic: List<Music>,
        favoriteSongs: List<Music>,
        listeningHistory: List<Music>
    ): Playlist {
        val recommendations = getPersonalizedRecommendations(
            allMusic, 
            favoriteSongs, 
            listeningHistory, 
            limit = 15
        )
        
        return Playlist(
            id = "ai_recommended",
            name = "Recommended for You",
            description = "AI-curated songs based on your taste",
            coverImageUrl = "https://picsum.photos/seed/airecommended/400/400",
            songs = recommendations,
            category = PlaylistCategory.RECOMMENDED
        )
    }
    
    /**
     * Find similar songs based on a given song
     */
    fun findSimilarSongs(
        targetSong: Music,
        allMusic: List<Music>,
        limit: Int = 5
    ): List<Music> {
        return allMusic
            .filter { it.id != targetSong.id }
            .map { music ->
                var similarity = 0.0
                
                // Same genre (50% weight)
                if (music.genre == targetSong.genre) {
                    similarity += 50.0
                }
                
                // Same artist (30% weight)
                if (music.artist == targetSong.artist) {
                    similarity += 30.0
                }
                
                // Similar popularity (20% weight)
                val playCountDiff = kotlin.math.abs(music.playCount - targetSong.playCount)
                val maxPlayCount = allMusic.maxOfOrNull { it.playCount } ?: 1
                val playCountSimilarity = 1.0 - (playCountDiff.toDouble() / maxPlayCount)
                similarity += playCountSimilarity * 20.0
                
                music to similarity
            }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }
    
    /**
     * Generate a smart shuffle playlist
     * Mix favorite genres with some exploration
     */
    fun generateSmartShuffle(
        allMusic: List<Music>,
        favoriteGenres: List<String>,
        limit: Int = 20
    ): List<Music> {
        val favoritesRatio = 0.7 // 70% from favorite genres, 30% exploration
        
        val favoriteCount = (limit * favoritesRatio).toInt()
        val explorationCount = limit - favoriteCount
        
        val fromFavorites = if (favoriteGenres.isNotEmpty()) {
            allMusic
                .filter { it.genre in favoriteGenres }
                .shuffled()
                .take(favoriteCount)
        } else {
            emptyList()
        }
        
        val forExploration = allMusic
            .filter { it.genre !in favoriteGenres }
            .sortedByDescending { it.playCount }
            .take(explorationCount)
            .shuffled()
        
        return (fromFavorites + forExploration).shuffled()
    }
}
