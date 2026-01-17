package com.example.flymusicai.data

/**
 * Mood and Feeling Categories for Smart Music Organization
 * Better than Spotify/JioSaavn with more precise mood detection
 */
enum class MoodCategory(val displayName: String, val emoji: String, val description: String) {
    // Emotional Moods
    HAPPY("Happy", "😊", "Feel-good vibes, uplifting songs"),
    SAD("Sad", "😢", "Emotional, melancholic, heartbreak songs"),
    ROMANTIC("Romantic", "❤️", "Love songs, romantic melodies"),
    BROKEN_HEART("Broken Heart", "💔", "Heartbreak, pain, emotional"),
    PEACEFUL("Peaceful", "🕊️", "Calm, relaxing, meditation"),
    ANGRY("Angry", "😤", "Aggressive, intense, powerful"),
    MOTIVATED("Motivated", "💪", "Inspiring, energetic, workout"),
    
    // Lifestyle Moods
    SIGMA("Sigma", "🗿", "Boss vibes, attitude, confidence"),
    ALONE("Alone", "🌙", "Solo vibes, introspective, lonely"),
    PARTY("Party", "🎉", "Dance, club, celebration"),
    CHILL("Chill", "😎", "Relaxed, laid-back, easy listening"),
    STUDY("Study", "📚", "Focus, concentration, lo-fi beats"),
    WORKOUT("Workout", "🏋️", "High energy, gym, sports"),
    TRAVEL("Travel", "✈️", "Road trip, adventure, wanderlust"),
    
    // Time-based Moods
    MORNING("Morning", "🌅", "Fresh start, energizing"),
    NIGHT("Night", "🌃", "Late night vibes, dreamy"),
    RAINY("Rainy", "🌧️", "Monsoon, cozy, nostalgic"),
    
    // Special Categories
    TRENDING("Trending", "🔥", "What's hot right now"),
    NOSTALGIC("Nostalgic", "⏰", "Old classics, memories"),
    UNDERGROUND("Underground", "🎧", "Hidden gems, indie"),
    VIRAL("Viral", "📱", "TikTok, Instagram trending"),
    
    // Activity-based
    DRIVING("Driving", "🚗", "Road trip, highway songs"),
    COOKING("Cooking", "👨‍🍳", "Kitchen vibes, feel-good"),
    GAMING("Gaming", "🎮", "Epic, energetic, intense"),
    
    // Vibe Categories
    DESI("Desi", "🇮🇳", "Pure Indian vibes, traditional"),
    WESTERNIZED("Westernized", "🌍", "Modern, international feel"),
    FUSION("Fusion", "🎵", "East meets West"),
    
    // Special Feelings
    CONFIDENCE("Confidence", "👑", "Boss moves, swagger"),
    MYSTERY("Mystery", "🕵️", "Dark, intriguing, suspenseful"),
    SPIRITUAL("Spiritual", "🙏", "Devotional, peaceful, divine"),
    COMEDY("Comedy", "😂", "Funny, light-hearted, jokes");
    
    companion object {
        /**
         * Get all mood categories as a list
         */
        fun getAllMoods(): List<MoodCategory> = values().toList()
        
        /**
         * Get mood by name
         */
        fun fromString(name: String): MoodCategory? {
            return values().find { it.name.equals(name, ignoreCase = true) }
        }
        
        /**
         * Get popular moods for quick access
         */
        fun getPopularMoods(): List<MoodCategory> = listOf(
            HAPPY, SAD, ROMANTIC, SIGMA, ALONE, 
            PARTY, CHILL, WORKOUT, TRENDING, VIRAL
        )
    }
}

/**
 * Enhanced Music model with mood support
 */
fun Music.getMoodCategories(): List<MoodCategory> {
    // Smart mood detection based on title, artist, genre
    val moods = mutableListOf<MoodCategory>()
    
    val titleLower = title.lowercase()
    val artistLower = artist.lowercase()
    
    // Sad mood detection
    if (titleLower.contains("dil") || titleLower.contains("tujhe") || 
        titleLower.contains("yaad") || titleLower.contains("khuda") ||
        titleLower.contains("sad") || titleLower.contains("lonely")) {
        moods.add(MoodCategory.SAD)
    }
    
    // Happy mood detection
    if (titleLower.contains("happy") || titleLower.contains("khush") ||
        titleLower.contains("dance") || titleLower.contains("nachdi")) {
        moods.add(MoodCategory.HAPPY)
    }
    
    // Romantic mood detection
    if (titleLower.contains("pyar") || titleLower.contains("love") ||
        titleLower.contains("ishq") || titleLower.contains("mohabbat") ||
        titleLower.contains("dil") || titleLower.contains("heart")) {
        moods.add(MoodCategory.ROMANTIC)
    }
    
    // Sigma/Boss mood detection
    if (titleLower.contains("attitude") || titleLower.contains("boss") ||
        titleLower.contains("king") || titleLower.contains("badshah") ||
        titleLower.contains("baap") || titleLower.contains("legend")) {
        moods.add(MoodCategory.SIGMA)
    }
    
    // Party mood detection
    if (titleLower.contains("party") || titleLower.contains("nachle") ||
        titleLower.contains("dance") || titleLower.contains("dj") ||
        genre.contains("EDM") || genre.contains("Electronic")) {
        moods.add(MoodCategory.PARTY)
    }
    
    // Workout mood detection
    if (titleLower.contains("power") || titleLower.contains("strong") ||
        titleLower.contains("fighter") || titleLower.contains("energy")) {
        moods.add(MoodCategory.WORKOUT)
    }
    
    // Night mood
    if (titleLower.contains("night") || titleLower.contains("raat") ||
        titleLower.contains("midnight") || titleLower.contains("moon")) {
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
