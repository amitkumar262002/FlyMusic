package com.example.flymusicai.data

/**
 * Mood and Feeling Categories for Smart Music Organization Better than Spotify/FlyMusicAI
 * competitors with more precise mood detection
 */
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Mood and Feeling Categories for Smart Music Organization Better than Spotify/FlyMusicAI
 * competitors with more precise mood detection
 */
enum class MoodCategory(val displayName: String, val icon: ImageVector, val description: String) {
    // Emotional Moods
    HAPPY("Happy", Icons.Default.SentimentVerySatisfied, "Feel-good vibes, uplifting songs"),
    SAD("Sad", Icons.Default.SentimentVeryDissatisfied, "Emotional, melancholic, heartbreak songs"),
    ROMANTIC("Romantic", Icons.Default.Favorite, "Love songs, romantic melodies"),
    BROKEN_HEART("Broken Heart", Icons.Default.HeartBroken, "Heartbreak, pain, emotional"),
    PEACEFUL("Peaceful", Icons.Default.SelfImprovement, "Calm, relaxing, meditation"),
    ANGRY("Angry", Icons.Default.Bolt, "Aggressive, intense, powerful"),
    MOTIVATED("Motivated", Icons.Default.FitnessCenter, "Inspiring, energetic, workout"),

    // Lifestyle Moods
    SIGMA("Sigma", Icons.Default.Portrait, "Boss vibes, attitude, confidence"),
    ALONE("Alone", Icons.Default.Nightlight, "Solo vibes, introspective, lonely"),
    PARTY("Party", Icons.Default.Celebration, "Dance, club, celebration"),
    CHILL("Chill", Icons.Default.Bedtime, "Relaxed, laid-back, easy listening"),
    STUDY("Study", Icons.Default.MenuBook, "Focus, concentration, lo-fi beats"),
    WORKOUT("Workout", Icons.Default.FitnessCenter, "High energy, gym, sports"),
    TRAVEL("Travel", Icons.Default.Flight, "Road trip, adventure, wanderlust"),

    // Time-based Moods
    MORNING("Morning", Icons.Default.LightMode, "Fresh start, energizing"),
    NIGHT("Night", Icons.Default.DarkMode, "Late night vibes, dreamy"),
    RAINY("Rainy", Icons.Default.Cloud, "Monsoon, cozy, nostalgic"),

    // Special Categories
    TRENDING("Trending", Icons.Default.TrendingUp, "What's hot right now"),
    NOSTALGIC("Nostalgic", Icons.Default.History, "Old classics, memories"),
    UNDERGROUND("Underground", Icons.Default.GraphicEq, "Hidden gems, indie"),
    VIRAL("Viral", Icons.Default.Star, "TikTok, Instagram trending"),

    // Activity-based
    DRIVING("Driving", Icons.Default.DirectionsCar, "Road trip, highway songs"),
    COOKING("Cooking", Icons.Default.Restaurant, "Kitchen vibes, feel-good"),
    GAMING("Gaming", Icons.Default.Gamepad, "Epic, energetic, intense"),

    // Vibe Categories
    DESI("Desi", Icons.Default.LocationOn, "Pure Indian vibes, traditional"),
    WESTERNIZED("Westernized", Icons.Default.Public, "Modern, international feel"),
    FUSION("Fusion", Icons.Default.MusicNote, "East meets West"),

    // Special Feelings
    CONFIDENCE("Confidence", Icons.Default.Grade, "Boss moves, swagger"),
    MYSTERY("Mystery", Icons.Default.Psychology, "Dark, intriguing, suspenseful"),
    SPIRITUAL("Spiritual", Icons.Default.VolunteerActivism, "Devotional, peaceful, divine"),
    COMEDY("Comedy", Icons.Default.Mood, "Funny, light-hearted, jokes");

    companion object {
        /** Get all mood categories as a list */
        @Deprecated("Use entries instead", ReplaceWith("entries"))
        fun getAllMoods(): List<MoodCategory> = values().toList()

        /** Get mood by name */
        fun fromString(name: String): MoodCategory? {
            return values().find { it.name.equals(name, ignoreCase = true) }
        }

        /** Get popular moods for quick access */
        fun getPopularMoods(): List<MoodCategory> =
                listOf(HAPPY, SAD, ROMANTIC, SIGMA, ALONE, PARTY, CHILL, WORKOUT, TRENDING, VIRAL)
    }
}

/** Enhanced Music model with mood support */
fun Music.getMoodCategories(): List<MoodCategory> {
    // Smart mood detection based on title, artist, genre
    val moods = mutableListOf<MoodCategory>()

    val titleLower = title.lowercase()

    // Sad mood detection
    if (titleLower.contains("dil") ||
                    titleLower.contains("tujhe") ||
                    titleLower.contains("yaad") ||
                    titleLower.contains("khuda") ||
                    titleLower.contains("sad") ||
                    titleLower.contains("lonely")
    ) {
        moods.add(MoodCategory.SAD)
    }

    // Happy mood detection
    if (titleLower.contains("happy") ||
                    titleLower.contains("khush") ||
                    titleLower.contains("dance") ||
                    titleLower.contains("nachdi")
    ) {
        moods.add(MoodCategory.HAPPY)
    }

    // Romantic mood detection
    if (titleLower.contains("pyar") ||
                    titleLower.contains("love") ||
                    titleLower.contains("ishq") ||
                    titleLower.contains("mohabbat") ||
                    titleLower.contains("dil") ||
                    titleLower.contains("heart")
    ) {
        moods.add(MoodCategory.ROMANTIC)
    }

    // Sigma/Boss mood detection
    if (titleLower.contains("attitude") ||
                    titleLower.contains("boss") ||
                    titleLower.contains("king") ||
                    titleLower.contains("badshah") ||
                    titleLower.contains("baap") ||
                    titleLower.contains("legend")
    ) {
        moods.add(MoodCategory.SIGMA)
    }

    // Party mood detection
    if (titleLower.contains("party") ||
                    titleLower.contains("nachle") ||
                    titleLower.contains("dance") ||
                    titleLower.contains("dj") ||
                    genre.contains("EDM") ||
                    genre.contains("Electronic")
    ) {
        moods.add(MoodCategory.PARTY)
    }

    // Workout mood detection
    if (titleLower.contains("power") ||
                    titleLower.contains("strong") ||
                    titleLower.contains("fighter") ||
                    titleLower.contains("energy")
    ) {
        moods.add(MoodCategory.WORKOUT)
    }

    // Night mood
    if (titleLower.contains("night") ||
                    titleLower.contains("raat") ||
                    titleLower.contains("midnight") ||
                    titleLower.contains("moon")
    ) {
        moods.add(MoodCategory.NIGHT)
    }

    // Default moods based on genre
    when (genre) {
        "Bollywood" -> moods.add(MoodCategory.DESI)
        "Punjabi" -> {
            moods.add(MoodCategory.PARTY)
            moods.add(MoodCategory.DESI)
        }
        "Bhojpuri" -> moods.add(MoodCategory.DESI)
        "Pop", "Rock" -> moods.add(MoodCategory.WESTERNIZED)
        "Electronic", "EDM" -> moods.add(MoodCategory.PARTY)
    }

    // If no mood detected, add chill as default
    if (moods.isEmpty()) {
        moods.add(MoodCategory.CHILL)
    }

    return moods
}
