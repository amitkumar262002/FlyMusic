package com.example.flymusicai.data

import com.example.flymusicai.api.YouTubeMusicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * YouTube Music Repository Provides trending songs, playlists, and categories from InnerTube
 * (YouTube Music)
 */
object YouTubeMusicRepository {

    private val musicService = com.example.flymusicai.api.YouTubeMusicService()
    private val innerTubeService = com.example.flymusicai.api.innertube.YouTubeInnerTubeService()

    fun updateRegionAndLanguage(gl: String, hl: String) {
        innerTubeService.setRegionAndLanguage(gl, hl)
    }

    suspend fun getTrendingMusic(limit: Int = 20): List<Music> =
            withContext(Dispatchers.IO) {
                try {
                    val songs = innerTubeService.getTrendingMusic().take(limit)
                    songs.map { it.toMusic("Trending") }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Fallback to search if browse fails
                    val searchSongs = musicService.searchVideos("trending music india 2024").take(limit)
                    searchSongs.map { it.toMusic("Trending") }
                }
            }

    /** Get music by category from YouTube */
    suspend fun getMusicByCategory(category: String, limit: Int = 100): List<Music> =
            withContext(Dispatchers.IO) {
                try {
                    val query =
                            when (category.lowercase()) {
                                "bollywood", "hindi" -> "bollywood hindi hit songs 2024"
                                "punjabi" -> "punjabi latest hits 2024"
                                "english", "pop" -> "top pop songs 2024"
                                "romantic" -> "new romantic hindi songs"
                                "party" -> "latest party music 2024"
                                "sadabahar", "old" -> "old hindi sadabahar songs 90's hits"
                                "bhojpuri" -> "latest bhojpuri hit songs 2024"
                                "haryanvi" -> "latest haryanvi songs 2024 hits"
                                "workout" -> "gym workout motivation music"
                                else -> "$category music 2024"
                            }

                    val songs = musicService.searchVideos(query).take(limit)
                    songs.map { it.toMusic(category) }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }

    /** Search music on YouTube */
    suspend fun searchMusic(query: String, limit: Int = 100): List<Music> =
            withContext(Dispatchers.IO) {
                try {
                    val songs = musicService.searchVideos(query).take(limit)
                    songs.map { it.toMusic("Trending") }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }

    /** Get search suggestions from YouTube */
    suspend fun getSearchSuggestions(query: String): List<String> =
            withContext(Dispatchers.IO) { musicService.getSearchSuggestions(query) }

    /** Get related music (recommendations) */
    suspend fun getRelatedMusic(musicId: String): List<Music> =
            withContext(Dispatchers.IO) {
                try {
                    // If related songs API is not fully implemented, search for the artist
                    // For now, let's use a smart search to simulate "For You"
                    val suggestions =
                            musicService.searchVideos("related to $musicId music").take(10)
                    suggestions.map { it.toMusic("Recommended") }
                } catch (e: Exception) {
                    emptyList()
                }
            }

    /** Get YouTube playlists - Diversified like Kreate/RiMusic */
    suspend fun getYouTubePlaylists(): List<Playlist> =
            withContext(Dispatchers.IO) {
                try {
                    val categories =
                            listOf(
                                    "Bollywood Hits" to "Bollywood",
                                    "Punjabi Beats" to "Punjabi",
                                    "English Pop" to "Pop",
                                    "Romantic Melodies" to "Romantic",
                                    "Haryanvi Top" to "Haryanvi",
                                    "Bhojpuri Hits" to "Bhojpuri",
                                    "Indie India" to "Indian indie",
                                    "Devotional" to "Bhakti"
                            )

                    categories.map { (name, category) ->
                        val songs = getMusicByCategory(category, 60)
                        Playlist(
                                id = "yt_playlist_${category.lowercase().replace(" ", "_")}",
                                name = name,
                                description = "Top $name for you",
                                coverImageUrl = songs.firstOrNull()?.coverImageUrl
                                                ?: ArtistConstants.CATEGORY_IMAGES[category] 
                                                ?: "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&q=80",
                                songs = songs,
                                category = PlaylistCategory.TRENDING
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            }

    /** India Rising - Support multi-language hits (Hindi, Punjabi, Regional, Indie) */
    suspend fun getIndiaRising(): List<Music> =
            getMusicByCategory("hindi punjabi tamil telugu kannada malayalam latest 2024 2025 trending india rising indie rap", 100)

    /** Romance Right Now */
    suspend fun getRomanceNow(): List<Music> =
            getMusicByCategory("latest romantic hindi songs 2024 2025 jukebox", 100)

    /** Best of 90s - Nostalgic Hits */
    suspend fun getBestOf90s(): List<Music> =
            getMusicByCategory("90s hindi hit songs gold collection superhits", 100)

    /** Hindi Hits - Trending in Bollywood */
    suspend fun getHindiHits(): List<Music> =
            getMusicByCategory("latest bollywood hindi hits 2024 2025 chartbusters", 100)

    /** Albums for you - Massive curated collection (70+ real albums from 2020-2025) */
    suspend fun getAlbumsForYou(): List<Playlist> =
        withContext(Dispatchers.IO) {
            try {
                val albumQueries = listOf(
                    // === 2024-2025 LATEST BLOCKBUSTERS ===
                    "Dhurandhar Shashwat Sachdev album", 
                    "Border 2 songs Sunny Deol jukebox", 
                    "Pushpa 2 The Rule Allu Arjun Devi Sri Prasad",
                    "Devara Jr NTR Anirudh Ravichander", 
                    "Singham Again Ajay Devgn jukebox", 
                    "Chhaava Vicky Kaushal AR Rahman",
                    "Fighter Hrithik Roshan Vishal Shekhar", 
                    "Stree 2 Shraddha Kapoor Sachin Jigar", 
                    "Kalki 2898 AD Prabhas Santhosh Narayanan",
                    "Bhool Bhulaiyaa 3 Kartik Aaryan Pritam",
                    "Khiladi 1080 Akshay Kumar songs",
                    "The Greatest Of All Time Vijay songs",
                    
                    // === SHAH RUKH KHAN COLLECTION ===
                    "Jawan Shah Rukh Khan jukebox", 
                    "Pathaan Shah Rukh Khan songs",
                    "Dunki Shah Rukh Khan songs", 
                    "Chennai Express songs",
                    "Dilwale songs Shah Rukh",
                    
                    // === SALMAN KHAN HITS ===
                    "Tiger 3 Salman Khan songs", 
                    "Gadar 2 Sunny Deol songs",
                    "Kick songs Salman Khan",
                    "Bajrangi Bhaijaan songs",
                    "Sultan songs",
                    
                    // === RANBIR KAPOOR & NEW GEN ===
                    "Animal Ranbir Kapoor Sandeep Vanga", 
                    "Brahmastra Ranbir Alia Bhatt songs",
                    "Rocky Aur Rani Kii Prem Kahaani jukebox", 
                    "Tu Jhoothi Main Makkaar Ranbir Kapoor",
                    "Shree Ramayana songs",
                    
                    // === ROMANTIC ALBUMS ===
                    "Kabir Singh Shahid Kapoor Sandeep Vanga", 
                    "Aashiqui 2 Aditya Roy Kapur songs",
                    "Ae Dil Hai Mushkil Ranbir Anushka jukebox", 
                    "Tamasha songs Ranbir Deepika",
                    "Dil Se Shah Rukh Manisha songs", 
                    "Tere Naam Salman Khan songs",
                    "Satyaprem Ki Katha Kartik Aaryan songs",
                    "Half Girlfriend songs Arjun Shraddha",
                    "Baaghi 2 songs Tiger Disha",
                    
                    // === ICONIC AR RAHMAN MASTERPIECES ===
                    "Rockstar AR Rahman album", 
                    "Dil Se AR Rahman songs",
                    "Rang De Basanti AR Rahman album", 
                    "Guru AR Rahman jukebox",
                    "Raanjhanaa AR Rahman songs",
                    "Highway AR Rahman album",
                    "Tamasha AR Rahman songs",
                    "OK Jaanu AR Rahman",
                    
                    // === GULLY BOY & HIP HOP ===
                    "Gully Boy Ranveer Singh Divine", 
                    "Apna Time Aayega Gully Boy",
                    "Mere Gully Mein Divine Naezy",
                    
                    // === ACTION BLOCKBUSTERS ===
                    "KGF Chapter 2 Yash Ravi Basrur", 
                    "KGF Chapter 1 Yash songs",
                    "Salaar Prabhas Ravi Basrur", 
                    "Leo Vijay Thalapathy Anirudh",
                    "Jailer Rajinikanth Anirudh", 
                    "Master Vijay Anirudh songs",
                    "Vikram Kamal Haasan Anirudh",
                    "Kantara Rishab Shetty songs",
                    "Kaithi Karthi Sam CS",
                    
                    // === SOUTH INDIAN MEGA HITS ===
                    "RRR Jr NTR Ram Charan MM Keeravani", 
                    "Baahubali Prabhas MM Keeravani",
                    "Baahubali 2 The Conclusion songs",
                    "2.0 Rajinikanth Akshay AR Rahman",
                    "Ponniyin Selvan AR Rahman",
                    "PS 2 songs AR Rahman",
                    "Valimai Ajith Kumar Yuvan",
                    "Varisu Vijay Thaman S",
                    
                    // === PUNJABI CHARTBUSTERS ===
                    "Kisi Ka Bhai Kisi Ki Jaan Salman songs",
                    "Zara Hatke Zara Bachke Vicky Sara", 
                    "Satyaprem Ki Katha songs Kartik",
                    "Brown Munde AP Dhillon album",
                    "Jugjugg Jeeyo songs Varun Kiara",
                    "Good Newwz songs Akshay Kareena",
                    
                    // === DEVOTIONAL & SPIRITUAL ===
                    "Shree Hanuman Chalisa Hariharan", 
                    "Hanuman Teja Sajja songs",
                    "Adipurush Prabhas Om Raut Ajay Atul",
                    "Bajrangi Bhaijaan songs Salman",
                    "OMG 2 Akshay Kumar songs",
                    
                    // === FAMILY ENTERTAINERS ===
                    "Ludo Abhishek Bachchan Pritam",
                    "Dream Girl 2 Ayushmann Khurrana songs",
                    "Bawaal Varun Dhawan Janhvi songs",
         "Merry Christmas Katrina Vijay Sethupathi",
                    "Main Atal Hoon Pankaj Tripathi songs",
                    "Article 370 Yami Gautam songs",
                    
                    // === CLASSIC HITS & RETRO ===
                    "Shershaah Sidharth Malhotra songs", 
                    "Raazi Alia Bhatt Shankar Ehsaan Loy",
                    "Uri The Surgical Strike songs",
                    "Tanhaji Ajay Devgn songs",
                    "Chhichhore Sushant Rajput Pritam",
                    "War Hrithik Tiger Vishal Shekhar",
                    "Mission Majnu Sidharth songs",
                    
                    //=== ITEM NUMBERS & DANCE HITS ===
                    "Pathaan Besharam Rang songs",
                    "Oo Antava Pushpa songs",
                    "Naatu Naatu RRR songs",
                    "Param Sundari Mimi songs",
                    "Burj Khalifa Laxmii songs"
                )

                albumQueries.mapIndexed { index, query ->
                    val songs = musicService.searchVideos(query).take(50).map { it.toMusic("Album") }
                    val albumName = query
                        .replace("songs", "")
                        .replace("jukebox", "")
                        .replace("album", "")
                        .replace("movie", "")
                        .split(" ").take(3).joinToString(" ")
                        .trim()
                    
                    Playlist(
                        id = "yt_album_$index",
                        name = albumName,
                        description = "Original Album • ${songs.size} songs",
                        coverImageUrl = songs.firstOrNull()?.coverImageUrl ?: "",
                        songs = songs,
                        category = PlaylistCategory.ALBUM
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    /** Get Top Indian Singles - 50+ Popular standalone hits (2023-2025) */
    suspend fun getTopIndianSingles(): List<Music> =
        withContext(Dispatchers.IO) {
            try {
                val singleQueries = listOf(
                    // === 2024-2025 VIRAL HITS ===
                    "Tauba Tauba Vicky Kaushal Bad Newz Karan Aujla",
                    "Aayi Nai Stree 2 Pawan Singh Simba Kharel",
                    "Aaj Ki Raat Stree 2 Madhubanti Divya Sachin Jigar",
                    "Angaaron Pushpa 2 Allu Arjun",
                    "Peelings Pushpa 2 Shreya Ghoshal",
                    
                    // === ARIJIT SINGH CHARTBUSTERS ===
                    "Satranga Arijit Singh Animal Ranbir Kapoor",
                    "Mere Mehboob Arijit Singh Vicky Vidya",
                    "Tum Se Arijit Singh Teri Baaton Mein",
                    "Chaleya Arijit Singh Jawan Shah Rukh",
                    "O Maahi Arijit Singh Dunki Shah Rukh",
                    "Apna Bana Le Arijit Singh Bhediya Varun",
                    "Kesariya Brahmastra Arijit Singh Ranbir Alia",
                    "Phir Aur Kya Chahiye Arijit Singh",
                    "Sajni Arijit Singh Laapataa Ladies",
                    "Heeriye Arijit Singh Jasleen Royal",
                    "Pehle Bhi Main Arijit Animal",
                    "Tere Hawale Arijit Singh",
                    
                    // === PUNJABI VIRAL SENSATIONS ===
                    "Arjan Vailly Bhupinder Babbal Animal",
                    "Tauba Tauba Karan Aujla Bad Newz",
                    "Kahani Suno 2.0 Kaifi Khalil viral",
                    "Excuses AP Dhillon",
                    "Brown Munde AP Dhillon Sidhu Moose Wala",
                    "Dil Nu Manere AP Dhillon",
                    "Insane AP Dhillon",
                    
                    // === ROMANTIC HITS ===
                    "Zihaal e Miskin Vishal Mishra Shreya Ghoshal",
                    "Tere Vaaste Varun Jain Sachin Jigar",
                    "Rang Sharbaton Ka Jubin Nautiyal Prateek Kuhad",
                    "Kho Gaye Hum Kahan Prateek Kuhad Jasleen Royal",
                    "Raataan Lambiyan Jubin Nautiyal Asees Kaur",
                    "Ve Kamleya Arijit Shreya Rocky Rani",
                    "O Bedardeya Tu Jhoothi Main Makkaar",
                    "Humdard Arijit Singh Ek Villain",
                    
                    // === TRENDING INDIE & RAP ===
                    "Maan Meri Jaan King viral",
                    "Tu Aake Dekhle King",
                    "Oops Yashika Sikka",
                    "Hass Hass Diljit Sia",
                    "Kya Baat Hai Hardy Sandhu",
                    "Bijlee Bijlee HardyPERFormulaic Sandhu",
                    
                    // === ITEM NUMBERS & DANCE ===
                    "Besharam Rang Pathaan Shah Rukh Deepika",
                    "Jhoome Jo Pathaan Shah Rukh",
                    "Oo Antava Pushpa Samantha Indravathi",
                    "Naatu Naatu RRR Jr NTR Ram Charan",
                    "Param Sundari Mimi Kriti Pankaj",
                    "Burj Khalifa Laxmii Akshay Nikhita",
                    "Kudmayi Cirkus Ranveer Singh",
                    
                    // === HEARTBREAK & SAD ===
                    "Tere Pyaar Mein Tu Jhoothi",
                    "Ranjha Shershaah B Praak Jasleen",
                    "Bekhayali Kabir Singh Sachet Tandon",
                    "Shayad Arijit Singh Love Aaj Kal 2",
                    "Pachtaoge Arijit Singh Vicky Nora",
                    
                    // === WORSHIP & DEVOTIONAL ===
                    "Kesariya Tera Ishq Hai Prabhas Adipurush",
                    "Shiva Tandav Stotram",
                    "Har Har Shambhu Bhole",
                    
                    // === CLASSIC REMAKES ===
                    "Pasoori Ali Sethi Shae Gill Coke Studio",
                    "Vaste Dhvani Bhanushali viral",
                    "Dilbar Neha Kakkar Nora Fatehi",
                    "Baarish Bilal Saeed Neha Kakkar"
                )
                
                singleQueries.flatMap { query ->
                    musicService.searchVideos(query).take(1).map { it.toMusic("Singles") }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    private fun com.example.flymusicai.api.SongDetails.toMusic(genre: String): Music {
        return Music(
                id = "yt_$id",
                title = title,
                artist = artist,
                duration = 300, // Default if not found
                coverImageUrl = coverImageUrl,
                audioUrl = "", // Fetch when playing
                genre = genre,
                year = 2024,
                playCount = 0
        )
    }
}
