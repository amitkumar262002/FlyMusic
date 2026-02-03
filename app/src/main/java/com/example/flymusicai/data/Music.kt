package com.example.flymusicai.data

import kotlinx.serialization.Serializable

/** Data class representing a music track */
@Serializable
data class Music(
        val id: String,
        val title: String,
        val artist: String,
        val album: String = "", // Kept for compatibility but not displayed
        val duration: Int, // Duration in seconds
        val coverImageUrl: String,
        val audioUrl: String = "",
        val genre: String = "Pop",
        val year: Int = 2024, // Renamed from releaseYear for consistency
        val language: String = "Hindi", // New field for smart recommendations
        var isFavorite: Boolean = false,
        val playCount: Int = 0,
        val isRingtone: Boolean = false, // New field to identify ringtones
        var isDownloaded: Boolean = false, // ✅ Download status
        var downloadProgress: Int = 0, // ✅ 0-100%
        var localFilePath: String = "", // ✅ Local path
        val lyrics: String = "" // Full lyrics text
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
        val username: String, // Public handle
        val email: String,
        val firstName: String = "",
        val lastName: String = "",
        val phoneNumber: String = "",
        val status: String = "Music Enthusiast", // Profile status/bio
        val dob: String = "",
        val gender: String = "Male",
        val favoriteGenres: List<String> = emptyList(),
        val isPremium: Boolean = false,
        val profileImageUrl: String? = null,
        val isFacebookConnected: Boolean = false
)
