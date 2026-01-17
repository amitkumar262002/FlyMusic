package com.example.flymusicai.data

/**
 * Music Library Generator
 * Uses LOCAL DATABASE - No API calls needed!
 * All songs stored in app
 */
object MusicGenerator {
    
    /**
     * Generate music library from LOCAL database
     * No internet required - instant loading!
     */
    fun generateLargeMusicLibrary(): List<Music> {
        // Use local database - no API calls!
        return LocalSongsDatabase.getAllSongs()
    }
    
    /**
     * Get popular song suggestions for search autocomplete
     */
    fun getPopularSongSuggestions(count: Int = 10): List<String> {
        return listOf(
            "Tum Hi Ho", "Kesariya", "Chaleya", "Apna Bana Le",
            "Excuses", "Brown Munde", "Lollipop Lagelu",
            "Vaathi Coming", "Oo Antava", "Butta Bomma"
        ).take(count)
    }
    
    /**
     * Get popular artist suggestions
     */
    fun getPopularArtistSuggestions(count: Int = 10): List<String> {
        return listOf(
            "Arijit Singh", "Shreya Ghoshal", "Diljit Dosanjh",
            "AP Dhillon", "Anirudh Ravichander", "DSP",
            "Pawan Singh", "Atif Aslam", "Neha Kakkar"
        ).take(count)
    }
}
