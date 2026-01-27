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
            if (music.year == 2024) {
                score += 10.0
            } else if (music.year == 2023) {
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
            coverImageUrl = "https://images.unsplash.com/photo-1514525253361-b83f85f051c0?w=500&q=80",
            songs = recommendations,
            category = PlaylistCategory.RECOMMENDED
        )
    }
    
    /**
     * Find similar songs based on a given song with intelligent mood/category detection
     */
    fun findSimilarSongs(
        targetSong: Music,
        allMusic: List<Music>,
        limit: Int = 5
    ): List<Music> {
        // Detect the mood/category of the target song
        val targetCategory = detectMoodCategory(targetSong)
        
        return allMusic
            .filter { it.id != targetSong.id }
            .map { music ->
                var similarity = 0.0
                
                // Detected mood/category match (60% weight) - HIGHEST PRIORITY
                val musicCategory = detectMoodCategory(music)
                if (musicCategory == targetCategory && targetCategory != "general") {
                    similarity += 60.0
                }
                
                // Same genre (25% weight)
                if (music.genre == targetSong.genre) {
                    similarity += 25.0
                }
                
                // Same artist (10% weight)
                if (music.artist == targetSong.artist) {
                    similarity += 10.0
                }
                
                // Similar popularity (5% weight)
                val playCountDiff = kotlin.math.abs(music.playCount - targetSong.playCount)
                val maxPlayCount = allMusic.maxOfOrNull { it.playCount } ?: 1
                val playCountSimilarity = 1.0 - (playCountDiff.toDouble() / maxPlayCount)
                similarity += playCountSimilarity * 5.0
                
                music to similarity
            }
            .sortedByDescending { it.second }
            .take(limit)
            .map { it.first }
    }
    
    /**
     * Detect mood/category from song metadata
     */
    private fun detectMoodCategory(song: Music): String {
        val title = song.title.lowercase()
        val artist = song.artist.lowercase()
        val genre = song.genre.lowercase()
        val combined = "$title $artist $genre"
        
        return when {
            // Workout / Gym / Fitness
            combined.contains("workout") || combined.contains("gym") || 
            combined.contains("fitness") || combined.contains("exercise") ||
            combined.contains("motivation") || combined.contains("beast mode") ||
            combined.contains("training") || combined.contains("power") -> "workout"
            
            // Party / Dance / Club
            combined.contains("party") || combined.contains("dance") || 
            combined.contains("club") || combined.contains("edm") ||
            combined.contains("dj") || combined.contains("remix") ||
            combined.contains("bass") || combined.contains("beat") -> "party"
            
            // Romance / Love
            combined.contains("love") || combined.contains("romance") || 
            combined.contains("heart") || combined.contains("romantic") ||
            combined.contains("pyaar") || combined.contains("ishq") ||
            combined.contains("mohabbat") || combined.contains("dil") -> "romance"
            
            // Sad / Emotional / Breakup
            combined.contains("sad") || combined.contains("cry") || 
            combined.contains("alone") || combined.contains("breakup") ||
            combined.contains("heartbreak") || combined.contains("tears") ||
            combined.contains("emotional") -> "sad"
            
            // Devotional / Spiritual / Religious
            combined.contains("devotional") || combined.contains("bhajan") || 
            combined.contains("hanuman") || combined.contains("shiv") ||
            combined.contains("krishna") || combined.contains("ram") ||
            combined.contains("prayer") || combined.contains("spiritual") ||
            combined.contains("mantra") || combined.contains("aarti") -> "devotional"
            
            // Chill / Relax / Study
            combined.contains(regex = Regex("\\bchill\\b")) || combined.contains("relax") || 
            combined.contains("calm") || combined.contains("study") ||
            combined.contains("lofi") || combined.contains("ambient") ||
            combined.contains("peaceful") -> "chill"
            
            // Punjabi
            combined.contains("punjabi") || combined.contains("bhangra") ||
            combined.contains("dhol") || combined.contains("panjabi") -> "punjabi"
            
            // Hindi / Bollywood
            combined.contains("hindi") || combined.contains("bollywood") ||
            combined.contains("desh bhakti") || combined.contains("indian") -> "hindi"

            // Bhojpuri
            combined.contains("bhojpuri") || combined.contains("lolipop") ||
            combined.contains("pawan singh") || combined.contains("khesari") -> "bhojpuri"

            // Haryanvi
            combined.contains("haryanvi") || combined.contains("sapna") ||
            combined.contains("gulzaar") -> "haryanvi"

            // South Indian
            combined.contains("tamil") || combined.contains("telugu") ||
            combined.contains("malayalam") || combined.contains("kannada") -> "south_indian"
            
            // Hip Hop / Rap
            combined.contains("rap") || combined.contains("hip hop") || 
            combined.contains("hiphop") || combined.contains("swag") ||
            combined.contains("desi hip hop") -> "hiphop"
            
            // Classical / Ghazal
            combined.contains("classical") || combined.contains("ghazal") ||
            combined.contains("raga") || combined.contains("sitar") ||
            combined.contains("tabla") -> "classical"
            
            // 90s / Retro / Old
            song.year in 1990..1999 || combined.contains("90s") || 
            combined.contains("retro") || combined.contains("old is gold") ||
            combined.contains("classic") -> "90s"
            
            else -> "general"
        }
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
