package com.example.flymusicai.data

/**
 * Repository class that provides dummy music data
 */
object MusicRepository {
    
    // Large music library with 1000+ songs
    private val dummyMusicList: List<Music> by lazy {
        MusicGenerator.generateLargeMusicLibrary()
    }
    
    // Featured/Original songs for quick display
    private val featuredSongs = listOf(
        // Hindi Songs
        Music(
            id = "1",
            title = "Tum Hi Ho",
            artist = "Arijit Singh",
            album = "",
            duration = 262,
            coverImageUrl = "https://picsum.photos/seed/hindi1/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            genre = "Bollywood",
            releaseYear = 2023,
            playCount = 5250,
            isRingtone = false
        ),
        Music(
            id = "2",
            title = "Kesariya",
            artist = "Arijit Singh",
            album = "",
            duration = 268,
            coverImageUrl = "https://picsum.photos/seed/hindi2/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            genre = "Bollywood",
            releaseYear = 2024,
            playCount = 4800,
            isRingtone = false
        ),
        Music(
            id = "3",
            title = "Apna Bana Le",
            artist = "Arijit Singh",
            album = "",
            duration = 245,
            coverImageUrl = "https://picsum.photos/seed/hindi3/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            genre = "Bollywood",
            releaseYear = 2023,
            playCount = 4200,
            isRingtone = false
        ),
        Music(
            id = "4",
            title = "Chaleya",
            artist = "Arijit Singh",
            album = "",
            duration = 251,
            coverImageUrl = "https://picsum.photos/seed/hindi4/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            genre = "Bollywood",
            releaseYear = 2024,
            playCount = 5500,
            isRingtone = false
        ),
        // Bhojpuri Songs
        Music(
            id = "5",
            title = "Lollipop Lagelu",
            artist = "Pawan Singh",
            album = "",
            duration = 235,
            coverImageUrl = "https://picsum.photos/seed/bhojpuri1/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3",
            genre = "Bhojpuri",
            releaseYear = 2023,
            playCount = 3800,
            isRingtone = false
        ),
        Music(
            id = "6",
            title = "Nirahua Satal Rahe",
            artist = "Dinesh Lal Yadav",
            album = "",
            duration = 228,
            coverImageUrl = "https://picsum.photos/seed/bhojpuri2/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3",
            genre = "Bhojpuri",
            releaseYear = 2024,
            playCount = 3200,
            isRingtone = false
        ),
        Music(
            id = "7",
            title = "Jab Se Dekhal",
            artist = "Khesari Lal Yadav",
            album = "",
            duration = 242,
            coverImageUrl = "https://picsum.photos/seed/bhojpuri3/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3",
            genre = "Bhojpuri",
            releaseYear = 2023,
            playCount = 2900,
            isRingtone = false
        ),
        // International Songs
        Music(
            id = "8",
            title = "Shape of You",
            artist = "Ed Sheeran",
            album = "",
            duration = 233,
            coverImageUrl = "https://picsum.photos/seed/intl1/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3",
            genre = "Pop",
            releaseYear = 2023,
            playCount = 6500,
            isRingtone = false
        ),
        Music(
            id = "9",
            title = "Blinding Lights",
            artist = "The Weeknd",
            album = "",
            duration = 200,
            coverImageUrl = "https://picsum.photos/seed/intl2/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3",
            genre = "Pop",
            releaseYear = 2024,
            playCount = 7200,
            isRingtone = false
        ),
        Music(
            id = "10",
            title = "Levitating",
            artist = "Dua Lipa",
            album = "",
            duration = 203,
            coverImageUrl = "https://picsum.photos/seed/intl3/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3",
            genre = "Pop",
            releaseYear = 2023,
            playCount = 5800,
            isRingtone = false
        ),
        Music(
            id = "11",
            title = "As It Was",
            artist = "Harry Styles",
            album = "",
            duration = 167,
            coverImageUrl = "https://picsum.photos/seed/intl4/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-11.mp3",
            genre = "Pop",
            releaseYear = 2024,
            playCount = 6100,
            isRingtone = false
        ),
        Music(
            id = "12",
            title = "Anti-Hero",
            artist = "Taylor Swift",
            album = "",
            duration = 200,
            coverImageUrl = "https://picsum.photos/seed/intl5/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-12.mp3",
            genre = "Pop",
            releaseYear = 2024,
            playCount = 5900,
            isRingtone = false
        ),
        // More Hindi Songs
        Music(
            id = "13",
            title = "Dil Diyan Gallan",
            artist = "Atif Aslam",
            album = "",
            duration = 238,
            coverImageUrl = "https://picsum.photos/seed/hindi5/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-13.mp3",
            genre = "Bollywood",
            releaseYear = 2023,
            playCount = 4500,
            isRingtone = false
        ),
        Music(
            id = "14",
            title = "Raataan Lambiyan",
            artist = "Jubin Nautiyal",
            album = "",
            duration = 243,
            coverImageUrl = "https://picsum.photos/seed/hindi6/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-14.mp3",
            genre = "Bollywood",
            releaseYear = 2023,
            playCount = 4100,
            isRingtone = false
        ),
        Music(
            id = "15",
            title = "Kahani Suno",
            artist = "Kaifi Khalil",
            album = "",
            duration = 255,
            coverImageUrl = "https://picsum.photos/seed/hindi7/400/400",
            audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-15.mp3",
            genre = "Indie",
            releaseYear = 2024,
            playCount = 3600,
            isRingtone = false
        )
    )
    
    /**
     * Get all available music tracks
     */
    fun getAllMusic(): List<Music> = dummyMusicList
    
    /**
     * Get a specific music track by ID
     */
    fun getMusicById(id: String): Music? = dummyMusicList.find { it.id == id }
    
    /**
     * Get music by genre
     */
    fun getMusicByGenre(genre: String): List<Music> = 
        dummyMusicList.filter { it.genre.equals(genre, ignoreCase = true) }
    
    /**
     * Get top trending music (by play count)
     */
    fun getTrendingMusic(limit: Int = 5): List<Music> = 
        dummyMusicList.sortedByDescending { it.playCount }.take(limit)
    
    /**
     * Search music by title or artist
     */
    fun searchMusic(query: String): List<Music> {
        val searchQuery = query.lowercase()
        return dummyMusicList.filter { 
            it.title.lowercase().contains(searchQuery) || 
            it.artist.lowercase().contains(searchQuery) ||
            it.album.lowercase().contains(searchQuery)
        }
    }
    
    /**
     * Get search suggestions based on partial query
     */
    fun getSearchSuggestions(query: String, limit: Int = 10): List<String> {
        if (query.isBlank()) {
            return MusicGenerator.getPopularSongSuggestions(limit)
        }
        
        val searchQuery = query.lowercase()
        val suggestions = mutableSetOf<String>()
        
        // Add matching song titles
        dummyMusicList.forEach { music ->
            if (music.title.lowercase().contains(searchQuery)) {
                suggestions.add(music.title)
            }
            if (music.artist.lowercase().contains(searchQuery)) {
                suggestions.add(music.artist)
            }
        }
        
        return suggestions.take(limit)
    }
    
    /**
     * Get popular song titles for suggestions
     */
    fun getPopularSuggestions(count: Int = 10): List<String> {
        return MusicGenerator.getPopularSongSuggestions(count)
    }
    
    /**
     * Get total song count
     */
    fun getTotalSongCount(): Int = dummyMusicList.size
    
    /**
     * Get only songs (excluding ringtones)
     */
    fun getSongsOnly(): List<Music> = dummyMusicList.filter { !it.isRingtone }
    
    /**
     * Get only ringtones
     */
    fun getRingtones(): List<Music> = dummyMusicList.filter { it.isRingtone }
    
    /**
     * Get dummy playlists
     */
    fun getPlaylists(): List<Playlist> = listOf(
        Playlist(
            id = "p1",
            name = "Bollywood Hits 🎵",
            description = "Top Hindi songs from Bollywood",
            coverImageUrl = "https://picsum.photos/seed/bollywood/400/400",
            songs = getMusicByGenre("Bollywood"),
            category = PlaylistCategory.TRENDING
        ),
        Playlist(
            id = "p2",
            name = "Bhojpuri Dhamaka 💥",
            description = "Best Bhojpuri songs",
            coverImageUrl = "https://picsum.photos/seed/bhojpuri/400/400",
            songs = getMusicByGenre("Bhojpuri"),
            category = PlaylistCategory.EDITORS_PICK
        ),
        Playlist(
            id = "p3",
            name = "International Pop 🌍",
            description = "Global pop music hits",
            coverImageUrl = "https://picsum.photos/seed/international/400/400",
            songs = dummyMusicList.filter { it.genre == "Pop" },
            category = PlaylistCategory.TRENDING
        ),
        Playlist(
            id = "p4",
            name = "Arijit Singh Special ❤️",
            description = "Best of Arijit Singh",
            coverImageUrl = "https://picsum.photos/seed/arijit/400/400",
            songs = dummyMusicList.filter { it.artist == "Arijit Singh" },
            category = PlaylistCategory.EDITORS_PICK
        ),
        Playlist(
            id = "p5",
            name = "Top Trending Now 🔥",
            description = "Most played songs",
            coverImageUrl = "https://picsum.photos/seed/trending/400/400",
            songs = getTrendingMusic(8),
            category = PlaylistCategory.TRENDING
        ),
        Playlist(
            id = "p6",
            name = "Desi Vibes 🇮🇳",
            description = "Best of Hindi & Bhojpuri",
            coverImageUrl = "https://picsum.photos/seed/desi/400/400",
            songs = dummyMusicList.filter { it.genre == "Bollywood" || it.genre == "Bhojpuri" },
            category = PlaylistCategory.GENERAL
        ),
        Playlist(
            id = "p7",
            name = "Ringtones 🔔",
            description = "Best ringtones collection",
            coverImageUrl = "https://picsum.photos/seed/ringtones/400/400",
            songs = getRingtones(),
            category = PlaylistCategory.GENERAL
        ),
        Playlist(
            id = "p8",
            name = "Punjabi Beats 🎧",
            description = "Top Punjabi hits from Diljit, AP Dhillon & more",
            coverImageUrl = "https://picsum.photos/seed/punjabi/400/400",
            songs = getMusicByGenre("Punjabi"),
            category = PlaylistCategory.TRENDING
        ),
        Playlist(
            id = "p9",
            name = "Tamil Melodies 🎶",
            description = "Best Tamil songs by Anirudh, A.R. Rahman",
            coverImageUrl = "https://picsum.photos/seed/tamil/400/400",
            songs = getMusicByGenre("Tamil"),
            category = PlaylistCategory.EDITORS_PICK
        ),
        Playlist(
            id = "p10",
            name = "Telugu Hits 🔥",
            description = "Trending Telugu music from DSP, Thaman",
            coverImageUrl = "https://picsum.photos/seed/telugu/400/400",
            songs = getMusicByGenre("Telugu"),
            category = PlaylistCategory.TRENDING
        ),
        Playlist(
            id = "p11",
            name = "Malayalam Magic ✨",
            description = "Beautiful Malayalam songs",
            coverImageUrl = "https://picsum.photos/seed/malayalam/400/400",
            songs = getMusicByGenre("Malayalam"),
            category = PlaylistCategory.GENERAL
        ),
        Playlist(
            id = "p12",
            name = "Kannada Vibes 🎵",
            description = "Popular Kannada music collection",
            coverImageUrl = "https://picsum.photos/seed/kannada/400/400",
            songs = getMusicByGenre("Kannada"),
            category = PlaylistCategory.GENERAL
        )
    )
}
