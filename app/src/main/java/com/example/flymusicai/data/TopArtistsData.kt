package com.example.flymusicai.data

/**
 * Top Artists Data with Real Images
 */
data class TopArtist(
    val id: String,
    val name: String,
    val imageUrl: String,
    val genre: String,
    val monthlyListeners: String
)

/**
 * Genre/Mood Card with Visual Theme
 */
data class GenreMoodCard(
    val id: String,
    val title: String,
    val imageUrl: String,
    val gradient: Pair<String, String>, // Start and end colors for gradient
    val description: String
)

object TopArtistsData {
    fun getTopArtists(): List<TopArtist> = listOf(
        TopArtist(
            id = "arijit_singh",
            name = "Arijit Singh",
            imageUrl = "file:///android_asset/artists/arijit_singh.png",
            genre = "Romantic",
            monthlyListeners = "82M"
        ),
        TopArtist(
            id = "atif_aslam",
            name = "Atif Aslam",
            imageUrl = "file:///android_asset/artists/atif_aslam.png",
            genre = "Romantic",
            monthlyListeners = "45M"
        ),
        TopArtist(
            id = "neha_kakkar",
            name = "Neha Kakkar",
            imageUrl = "file:///android_asset/artists/neha_kakkar.png",
            genre = "Pop",
            monthlyListeners = "52M"
        ),
        TopArtist(
            id = "ap_dhillon",
            name = "AP Dhillon",
            imageUrl = "file:///android_asset/artists/ap_dhillon.png",
            genre = "Punjabi",
            monthlyListeners = "38M"
        ),
        TopArtist(
            id = "sidhu_moosewala",
            name = "Sidhu Moose Wala",
            imageUrl = "file:///android_asset/artists/sidhu_moosewala.png",
            genre = "Punjabi Hip-Hop",
            monthlyListeners = "67M"
        ),
        TopArtist(
            id = "badshah",
            name = "Badshah",
            imageUrl = "https://i.scdn.co/image/ab6761610000e5ebd0e5e4810b77c0f7a1d952ec",
            genre = "Hip-Hop",
            monthlyListeners = "29M"
        ),
        TopArtist(
            id = "honey_singh",
            name = "Yo Yo Honey Singh",
            imageUrl = "https://i.scdn.co/image/ab6761610000e5eb822e35ab98c19ef7e6dafe0d",
            genre = "Punjabi Pop",
            monthlyListeners = "23M"
        ),
        TopArtist(
            id = "shreya_ghoshal",
            name = "Shreya Ghoshal",
            imageUrl = "https://i.scdn.co/image/ab6761610000e5ebfc75f6dc34d0b04d907c0144",
            genre = "Classical",
            monthlyListeners = "41M"
        )
    )
}

object GenreMoodCardsData {
    fun getMoodCards(): List<GenreMoodCard> = listOf(
        GenreMoodCard(
            id = "romantic",
            title = "Romantic",
            imageUrl = "file:///android_asset/moods/romantic.png",
            gradient = Pair("#FF6B9D", "#C06C84"),
            description = "Love songs for your heart"
        ),
        GenreMoodCard(
            id = "party",
            title = "Party",
            imageUrl = "file:///android_asset/moods/party.png",
            gradient = Pair("#7F00FF", "#E100FF"),
            description = "Get the party started"
        ),
        GenreMoodCard(
            id = "workout",
            title = "Workout",
            imageUrl = "file:///android_asset/moods/workout.png",
            gradient = Pair("#FF6B00", "#FF9500"),
            description = "Pump up your energy"
        ),
        GenreMoodCard(
            id = "chill",
            title = "Chill",
            imageUrl = "file:///android_asset/moods/chill.png",
            gradient = Pair("#00C9FF", "#92FE9D"),
            description = "Relax and unwind"
        ),
        GenreMoodCard(
            id = "sad",
            title = "Sad",
            imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400",
            gradient = Pair("#4A5568", "#718096"),
            description = "When you need to feel"
        ),
        GenreMoodCard(
            id = "happy",
            title = "Happy",
            imageUrl = "https://images.unsplash.com/photo-1533174072545-7a4b6ad7a6c3?w=400",
            gradient = Pair("#FFD700", "#FFA500"),
            description = "Feel good vibes"
        )
    )
    
    fun getGenreCards(): List<GenreMoodCard> = listOf(
        GenreMoodCard(
            id = "punjabi",
            title = "Punjabi",
            imageUrl = "file:///android_asset/genres/punjabi.png",
            gradient = Pair("#FFB800", "#FF6B00"),
            description = "Bhangra & Punjabi hits"
        ),
        GenreMoodCard(
            id = "bollywood",
            title = "Bollywood",
            imageUrl = "file:///android_asset/genres/bollywood.png",
            gradient = Pair("#FF0844", "#FFB199"),
            description = "Latest Hindi film songs"
        ),
        GenreMoodCard(
            id = "hip_hop",
            title = "Hip-Hop",
            imageUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400",
            gradient = Pair("#000000", "#434343"),
            description = "Desi rap & hip-hop"
        ),
        GenreMoodCard(
            id = "classical",
            title = "Classical",
            imageUrl = "https://images.unsplash.com/photo-1511192336575-5a79af67a629?w=400",
            gradient = Pair("#8B4513", "#D2691E"),
            description = "Timeless melodies"
        ),
        GenreMoodCard(
            id = "bhojpuri",
            title = "Bhojpuri",
            imageUrl = "https://images.unsplash.com/photo-1524492412937-b28074a5d7da?w=400",
            gradient = Pair("#FF6B35", "#F7931E"),
            description = "Bhojpuri chartbusters"
        ),
        GenreMoodCard(
            id = "devotional",
            title = "Devotional",
            imageUrl = "https://images.unsplash.com/photo-1535131749006-b7f58c99034b?w=400",
            gradient = Pair("#FFD700", "#FF8C00"),
            description = "Spiritual & bhajans"
        )
    )
}
