package com.example.flymusicai.data

/**
 * Top Artists Data with Real Unique Images
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
            imageUrl = "https://c.saavncdn.com/artists/Arijit_Singh_500x500.jpg",
            genre = "Romantic",
            monthlyListeners = "82M"
        ),
        TopArtist(
            id = "shreya_ghoshal",
            name = "Shreya Ghoshal",
            imageUrl = "https://c.saavncdn.com/artists/Shreya_Ghoshal_500x500.jpg",
            genre = "Classical",
            monthlyListeners = "41M"
        ),
        TopArtist(
            id = "atif_aslam",
            name = "Atif Aslam",
            imageUrl = "https://c.saavncdn.com/artists/Atif_Aslam_500x500.jpg",
            genre = "Romantic",
            monthlyListeners = "45M"
        ),
        TopArtist(
            id = "neha_kakkar",
            name = "Neha Kakkar",
            imageUrl = "https://c.saavncdn.com/artists/Neha_Kakkar_500x500.jpg",
            genre = "Pop",
            monthlyListeners = "52M"
        ),
        TopArtist(
            id = "diljit_dosanjh",
            name = "Diljit Dosanjh",
            imageUrl = "https://c.saavncdn.com/artists/Diljit_Dosanjh_500x500.jpg",
            genre = "Punjabi",
            monthlyListeners = "38M"
        ),
        TopArtist(
            id = "sidhu_moosewala",
            name = "Sidhu Moose Wala",
            imageUrl = "https://c.saavncdn.com/artists/Sidhu_Moose_Wala_500x500.jpg",
            genre = "Punjabi Hip-Hop",
            monthlyListeners = "67M"
        ),
        TopArtist(
            id = "badshah",
            name = "Badshah",
            imageUrl = "https://c.saavncdn.com/artists/Badshah_500x500.jpg",
            genre = "Hip-Hop",
            monthlyListeners = "29M"
        ),
        TopArtist(
            id = "sonu_nigam",
            name = "Sonu Nigam",
            imageUrl = "https://c.saavncdn.com/artists/Sonu_Nigam_500x500.jpg",
            genre = "Evergreen",
            monthlyListeners = "35M"
        )
    )
}

object GenreMoodCardsData {
    fun getMoodCards(): List<GenreMoodCard> = listOf(
        GenreMoodCard(
            id = "romantic",
            title = "Romantic",
            imageUrl = "https://c.saavncdn.com/editorial/Romantic_Hits_Hindi_139364_500x500.jpg",
            gradient = Pair("#FF6B9D", "#C06C84"),
            description = "Love songs for your heart"
        ),
        GenreMoodCard(
            id = "party",
            title = "Party",
            imageUrl = "https://c.saavncdn.com/editorial/Party_All_Night_Hindi_500x500.jpg",
            gradient = Pair("#7F00FF", "#E100FF"),
            description = "Get the party started"
        ),
        GenreMoodCard(
            id = "workout",
            title = "Workout",
            imageUrl = "https://c.saavncdn.com/editorial/Gym_Vibe_500x500.jpg",
            gradient = Pair("#FF6B00", "#FF9500"),
            description = "Pump up your energy"
        ),
        GenreMoodCard(
            id = "chill",
            title = "Chill",
            imageUrl = "https://c.saavncdn.com/editorial/Chill_Out_Hindi_500x500.jpg",
            gradient = Pair("#00C9FF", "#92FE9D"),
            description = "Relax and unwind"
        ),
        GenreMoodCard(
            id = "sad",
            title = "Sad",
            imageUrl = "https://c.saavncdn.com/editorial/Sad_Hits_Hindi_500x500.jpg",
            gradient = Pair("#4A5568", "#718096"),
            description = "When you need to feel"
        ),
        GenreMoodCard(
            id = "happy",
            title = "Happy",
            imageUrl = "https://c.saavncdn.com/editorial/Happy_Hits_Hindi_500x500.jpg",
            gradient = Pair("#FFD700", "#FFA500"),
            description = "Feel good vibes"
        )
    )
    
    fun getGenreCards(): List<GenreMoodCard> = listOf(
        GenreMoodCard(
            id = "punjabi",
            title = "Punjabi",
            imageUrl = "https://c.saavncdn.com/editorial/charts_TopWeeklyPunjabi_139364_500x500.jpg",
            gradient = Pair("#FFB800", "#FF6B00"),
            description = "Bhangra & Punjabi hits"
        ),
        GenreMoodCard(
            id = "bollywood",
            title = "Bollywood",
            imageUrl = "https://c.saavncdn.com/editorial/charts_TopWeeklyHindi_139364_20231201123456_500x500.jpg",
            gradient = Pair("#FF0844", "#FFB199"),
            description = "Latest Hindi film songs"
        ),
        GenreMoodCard(
            id = "hip_hop",
            title = "Hip-Hop",
            imageUrl = "https://c.saavncdn.com/editorial/Desi_Hip_Hop_Hindi_500x500.jpg",
            gradient = Pair("#000000", "#434343"),
            description = "Desi rap & hip-hop"
        ),
        GenreMoodCard(
            id = "90s",
            title = "Best of 90s",
            imageUrl = "https://c.saavncdn.com/editorial/90s_Bollywood_Hits_500x500.jpg",
            gradient = Pair("#8B4513", "#D2691E"),
            description = "Timeless melodies"
        ),
        GenreMoodCard(
            id = "rock",
            title = "Rock",
            imageUrl = "https://c.saavncdn.com/153/Rockstar-Hindi-2011-20221212115129-500x500.jpg",
            gradient = Pair("#FF6B35", "#F7931E"),
            description = "Indian Rock hits"
        ),
        GenreMoodCard(
            id = "devotional",
            title = "Devotional",
            imageUrl = "https://c.saavncdn.com/editorial/Devotional_Hits_Hindi_500x500.jpg",
            gradient = Pair("#FFD700", "#FF8C00"),
            description = "Spiritual & bhajans"
        )
    )
}
