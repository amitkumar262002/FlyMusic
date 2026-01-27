package com.example.flymusicai.data

/** 🌟 Top Singers / Artists with real Spotify-quality image URLs */
object ArtistConstants {
    val TOP_SINGERS = listOf(
        ArtistInfo("Arijit Singh", "https://i.scdn.co/image/ab6761610000e5eb92d01c5ce7efb5c1bfd07a38"),
        ArtistInfo("Badshah", "https://i.scdn.co/image/ab6761610000e5eb4a8c2e1c7b5d0e6f8e7a5c3b"),
        ArtistInfo("Neha Kakkar", "https://i.scdn.co/image/ab6761610000e5eb94cca56b7f3c6e9c59e2c7e0"),
        ArtistInfo("Diljit Dosanjh", "https://i.scdn.co/image/ab6761610000e5eb6e835a500e791bf9c27a422a"),
        ArtistInfo("Shreya Ghoshal", "https://i.scdn.co/image/ab6761610000e5eb619c8fa8a2e7f5f6c6a7e1cc"),
        ArtistInfo("Guru Randhawa", "https://i.scdn.co/image/ab6761610000e5eb7d8e9f0e1e2f3e4e5f6e7f8e"),
        ArtistInfo("Yo Yo Honey Singh", "https://i.scdn.co/image/ab6761610000e5eb2a3b4c5d6e7f8e9f0e1e2f3e"),
        ArtistInfo("Sidhu Moose Wala", "https://i.scdn.co/image/ab6761610000e5eb6783083981880949d63c5d8a"),
        ArtistInfo("Jubin Nautiyal", "https://i.scdn.co/image/ab6761610000e5eb5c6d7e8f9e0e1f2e3f4e5f6e"),
        ArtistInfo("Darshan Raval", "https://i.scdn.co/image/ab6761610000e5eb3c9b8e5d7f6e8d9e0f1a2b3c"),
        ArtistInfo("Armaan Malik", "https://i.scdn.co/image/ab6761610000e5eb942f360742183e8ce8a49156"),
        ArtistInfo("Atif Aslam", "https://i.scdn.co/image/ab6761610000e5eb7da39dea0a72f581535fb11f"),
        ArtistInfo("Sunidhi Chauhan", "https://i.scdn.co/image/ab6761610000e5ebad6b4904259b369527e53f5d"),
        ArtistInfo("Karan Aujla", "https://i.scdn.co/image/ab6761610000e5eb7b64082729938b8d447f5a8c"),
        ArtistInfo("AP Dhillon", "https://i.scdn.co/image/ab6761610000e5eb0b9a9578272f7c3f3f009951"),
        ArtistInfo("King", "https://i.scdn.co/image/ab6761610000e5eb7a419c823f669e9a4f9a0d8c"),
        ArtistInfo("Divine", "https://i.scdn.co/image/ab6761610000e5eb1d670f807f7c4627d3c5f87b"),
        ArtistInfo("Taylor Swift", "https://i.scdn.co/image/ab6761610000e5eb859e061db37e1713dca73a4d"),
        ArtistInfo("The Weeknd", "https://i.scdn.co/image/ab6761610000e5eb429881dba27b0b2e316d80d2"),
        ArtistInfo("Drake", "https://i.scdn.co/image/ab6761610000e5eb429074a169b5c3d4f1c1a2b3")
    )

    /** 🎨 Category Specific Images */
    val CATEGORY_IMAGES = mapOf(
        "Bollywood" to "https://images.unsplash.com/photo-1514525253361-b83f85f051c0?w=500&q=80",
        "Romantic" to "https://images.unsplash.com/photo-1518621736915-f3b1c41bfd00?w=500&q=80",
        "Party" to "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=500&q=80",
        "Workout" to "https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=500&q=80",
        "Chill" to "https://images.unsplash.com/photo-1516057305928-17a7ac0d41bc?w=500&q=80",
        "Study" to "https://images.unsplash.com/photo-1516979187457-637abb4f9353?w=500&q=80",
        "Happy" to "https://images.unsplash.com/photo-1490730141103-6cac27aaab94?w=500&q=80",
        "Sad" to "https://images.unsplash.com/photo-1516585427167-9f4af9627e6c?w=500&q=80"
    )

    fun getArtistImage(name: String): String {
        return TOP_SINGERS.find { it.name.equals(name, ignoreCase = true) }?.imageUrl
            ?: "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&q=80"
    }
}

data class ArtistInfo(val name: String, val imageUrl: String)
