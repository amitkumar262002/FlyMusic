package com.example.flymusicai.data

/** Data class representing a music track */
data class Music(
        val id: String,
        val title: String,
        val artist: String,
        val album: String = "", // Kept for compatibility but not displayed
        val duration: Int, // Duration in seconds
        val coverImageUrl: String,
        val audioUrl: String = "",
        val genre: String = "Pop",
        val releaseYear: Int = 2024,
        var isFavorite: Boolean = false,
        val playCount: Int = 0,
        val isRingtone: Boolean = false, // New field to identify ringtones
        var isDownloaded: Boolean = false, // ✅ Download status
        var downloadProgress: Int = 0, // ✅ 0-100%
        var localFilePath: String = "" // ✅ Local path
)

/** Data class representing a playlist */
data class Playlist(
        val id: String,
        val name: String,
        val description: String,
        val coverImageUrl: String,
        val songs: List<Music>,
        val category: PlaylistCategory = PlaylistCategory.GENERAL
)

/** Data class for user profile */
data class UserProfile(
        val userId: String,
        val username: String,
        val email: String,
        val fullName: String = "",
        val address: String = "",
        val country: String = "",
        val state: String = "",
        val pincode: String = "",
        val favoriteGenres: List<String> = emptyList(),
        val favoriteSongs: List<String> = emptyList(), // List of song IDs
        val listeningHistory: List<String> = emptyList() // List of song IDs
)
