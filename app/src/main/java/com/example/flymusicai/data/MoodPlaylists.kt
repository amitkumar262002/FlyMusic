package com.example.flymusicai.data

/** Mood-Based Playlists - Better than others More categories, smarter organization */
object MoodPlaylists {

    /** Get all mood-based playlists */
    fun getAllMoodPlaylists(allSongs: List<Music>): List<Playlist> {
        val playlists = mutableListOf<Playlist>()

        // Emotional Moods
        playlists.add(createHappyPlaylist(allSongs))
        playlists.add(createSadPlaylist(allSongs))
        playlists.add(createRomanticPlaylist(allSongs))
        playlists.add(createBrokenHeartPlaylist(allSongs))

        // Lifestyle Moods
        playlists.add(createSigmaPlaylist(allSongs))
        playlists.add(createAlonePlaylist(allSongs))
        playlists.add(createPartyPlaylist(allSongs))
        playlists.add(createChillPlaylist(allSongs))
        playlists.add(createWorkoutPlaylist(allSongs))

        // Time-based
        playlists.add(createMorningPlaylist(allSongs))
        playlists.add(createNightPlaylist(allSongs))
        playlists.add(createRainyPlaylist(allSongs))

        // Special Categories
        playlists.add(createTrendingPlaylist(allSongs))
        playlists.add(createViralPlaylist(allSongs))
        playlists.add(createNostalgicPlaylist(allSongs))

        // Activity-based
        playlists.add(createDrivingPlaylist(allSongs))
        playlists.add(createStudyPlaylist(allSongs))

        return playlists
    }

    // Emotional Playlists

    private fun createHappyPlaylist(songs: List<Music>): Playlist {
        val happySongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("happy") ||
                                    title.contains("khush") ||
                                    title.contains("nachdi") ||
                                    title.contains("celebrate") ||
                                    title.contains("smile") ||
                                    title.contains("joy")
                        }
                        .take(100)

        return Playlist(
                id = "mood_happy",
                name = "Happy Vibes",
                description = "Feel-good songs to brighten your day",
                coverImageUrl = ArtistConstants.CATEGORY_IMAGES["Happy"] ?: "",
                songs = happySongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createSadPlaylist(songs: List<Music>): Playlist {
        val sadSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("dil") ||
                                    title.contains("yaad") ||
                                    title.contains("tere") ||
                                    title.contains("khuda") ||
                                    title.contains("sad") ||
                                    title.contains("tears") ||
                                    title.contains("alvida") ||
                                    title.contains("pain")
                        }
                        .take(100)

        return Playlist(
                id = "mood_sad",
                name = "Sad Songs",
                description = "For when you're feeling emotional",
                coverImageUrl = ArtistConstants.CATEGORY_IMAGES["Sad"] ?: "",
                songs = sadSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createRomanticPlaylist(songs: List<Music>): Playlist {
        val romanticSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("pyar") ||
                                    title.contains("love") ||
                                    title.contains("ishq") ||
                                    title.contains("mohabbat") ||
                                    title.contains("romance") ||
                                    title.contains("heart")
                        }
                        .take(100)

        return Playlist(
                id = "mood_romantic",
                name = "Romantic",
                description = "Love is in the air",
                coverImageUrl = ArtistConstants.CATEGORY_IMAGES["Romantic"] ?: "",
                songs = romanticSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createBrokenHeartPlaylist(songs: List<Music>): Playlist {
        val brokenHeartSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("broken") ||
                                    title.contains("heartbreak") ||
                                    title.contains("alvida") ||
                                    title.contains("tujhe") ||
                                    title.contains("miss") ||
                                    title.contains("hurt")
                        }
                        .take(100)

        return Playlist(
                id = "mood_broken_heart",
                name = "Broken Heart",
                description = "Healing from heartbreak",
                coverImageUrl = "https://images.unsplash.com/photo-1549213821-4708d624e1d1?w=500&q=80",
                songs = brokenHeartSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    // Lifestyle Playlists

    private fun createSigmaPlaylist(songs: List<Music>): Playlist {
        val sigmaSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            val artist = song.artist.lowercase()
                            title.contains("attitude") ||
                                    title.contains("boss") ||
                                    title.contains("king") ||
                                    title.contains("badshah") ||
                                    title.contains("gangster") ||
                                    title.contains("sigma") ||
                                    title.contains("legend") ||
                                    artist.contains("sidhu moose")
                        }
                        .take(100)

        return Playlist(
                id = "mood_sigma",
                name = "Sigma Male",
                description = "Boss vibes, attitude songs",
                coverImageUrl = "https://images.unsplash.com/photo-1518020382113-a71843b51b3c?w=500&q=80",
                songs = sigmaSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createAlonePlaylist(songs: List<Music>): Playlist {
        val aloneSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("alone") ||
                                    title.contains("lonely") ||
                                    title.contains("akela") ||
                                    title.contains("tanhai") ||
                                    title.contains("solitude") ||
                                    title.contains("solo")
                        }
                        .take(100)

        return Playlist(
                id = "mood_alone",
                name = "Alone Time",
                description = "For your solo moments",
                coverImageUrl = "https://images.unsplash.com/photo-1509248961158-e54f6934749c?w=500&q=80",
                songs = aloneSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createPartyPlaylist(songs: List<Music>): Playlist {
        val partySongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("party") ||
                                    title.contains("dance") ||
                                    title.contains("dj") ||
                                    title.contains("club") ||
                                    title.contains("nachle") ||
                                    title.contains("beat") ||
                                    song.genre.contains("Electronic") ||
                                    song.genre.contains("Punjabi")
                        }
                        .take(100)

        return Playlist(
                id = "mood_party",
                name = "Party Anthems",
                description = "Get the party started!",
                coverImageUrl = ArtistConstants.CATEGORY_IMAGES["Party"] ?: "",
                songs = partySongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createChillPlaylist(songs: List<Music>): Playlist {
        val chillSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("chill") ||
                                    title.contains("relax") ||
                                    title.contains("calm") ||
                                    title.contains("peace") ||
                                    title.contains("slow") ||
                                    title.contains("soft")
                        }
                        .take(100)

        return Playlist(
                id = "mood_chill",
                name = "Chill Vibes",
                description = "Relax and unwind",
                coverImageUrl = ArtistConstants.CATEGORY_IMAGES["Chill"] ?: "",
                songs = chillSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createWorkoutPlaylist(songs: List<Music>): Playlist {
        val workoutSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("power") ||
                                    title.contains("strong") ||
                                    title.contains("fighter") ||
                                    title.contains("energy") ||
                                    title.contains("beast") ||
                                    title.contains("warrior") ||
                                    title.contains("gym") ||
                                    title.contains("motivation")
                        }
                        .take(100)

        return Playlist(
                id = "mood_workout",
                name = "Workout",
                description = "Beast mode activated",
                coverImageUrl = ArtistConstants.CATEGORY_IMAGES["Workout"] ?: "",
                songs = workoutSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    // Time-based Playlists

    private fun createMorningPlaylist(songs: List<Music>): Playlist {
        val morningSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("morning") ||
                                    title.contains("savera") ||
                                    title.contains("subah") ||
                                    title.contains("sunrise") ||
                                    title.contains("fresh") ||
                                    title.contains("new day")
                        }
                        .take(100)

        return Playlist(
                id = "mood_morning",
                name = "Morning Fresh",
                description = "Start your day right",
                coverImageUrl = "https://images.unsplash.com/photo-1470240731273-7821a6eeb6bd?w=500&q=80",
                songs = morningSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createNightPlaylist(songs: List<Music>): Playlist {
        val nightSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("night") ||
                                    title.contains("raat") ||
                                    title.contains("midnight") ||
                                    title.contains("moon") ||
                                    title.contains("chand") ||
                                    title.contains("dreams")
                        }
                        .take(100)

        return Playlist(
                id = "mood_night",
                name = "Night Vibes",
                description = "Late night feelings",
                coverImageUrl = "https://images.unsplash.com/photo-1532767153582-b1a0e5145009?w=500&q=80",
                songs = nightSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createRainyPlaylist(songs: List<Music>): Playlist {
        val rainySongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("rain") ||
                                    title.contains("baarish") ||
                                    title.contains("barish") ||
                                    title.contains("monsoon") ||
                                    title.contains("sawan") ||
                                    title.contains("cloud")
                        }
                        .take(100)

        return Playlist(
                id = "mood_rainy",
                name = "Rainy Day",
                description = "Perfect for monsoon mood",
                coverImageUrl = "https://images.unsplash.com/photo-1519692933481-e162a57d6721?w=500&q=80",
                songs = rainySongs,
                category = PlaylistCategory.GENERAL
        )
    }

    // Special Category Playlists

    private fun createTrendingPlaylist(songs: List<Music>): Playlist {
        val trendingSongs = songs.sortedByDescending { it.playCount }.take(100)

        return Playlist(
                id = "mood_trending",
                name = "Trending Now",
                description = "What's hot right now",
                coverImageUrl = "https://images.unsplash.com/photo-1459749411177-042180ceea72?w=500&q=80",
                songs = trendingSongs,
                category = PlaylistCategory.TRENDING
        )
    }

    private fun createViralPlaylist(songs: List<Music>): Playlist {
        val viralSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            // Viral song patterns (TikTok, Instagram Reels)
                            title.contains("viral") || song.playCount > 5000
                        }
                        .sortedByDescending { it.playCount }
                        .take(100)

        return Playlist(
                id = "mood_viral",
                name = "Viral Hits",
                description = "TikTok & Instagram trending",
                coverImageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&q=80",
                songs = viralSongs,
                category = PlaylistCategory.TRENDING
        )
    }

    private fun createNostalgicPlaylist(songs: List<Music>): Playlist {
        val nostalgicSongs =
                songs
                        .filter { song ->
                            song.year < 2020 // Older songs
                        }
                        .take(100)

        return Playlist(
                id = "mood_nostalgic",
                name = "Nostalgic",
                description = "Old is gold",
                coverImageUrl = "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=500&q=80",
                songs = nostalgicSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    // Activity-based Playlists

    private fun createDrivingPlaylist(songs: List<Music>): Playlist {
        val drivingSongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("road") ||
                                    title.contains("drive") ||
                                    title.contains("highway") ||
                                    title.contains("journey") ||
                                    song.genre.contains("Punjabi") ||
                                    song.genre.contains("Rock")
                        }
                        .take(100)

        return Playlist(
                id = "mood_driving",
                name = "Driving Songs",
                description = "Perfect for road trips",
                coverImageUrl = "https://images.unsplash.com/photo-1493238507154-20389a9f242c?w=500&q=80",
                songs = drivingSongs,
                category = PlaylistCategory.GENERAL
        )
    }

    private fun createStudyPlaylist(songs: List<Music>): Playlist {
        val studySongs =
                songs
                        .filter { song ->
                            val title = song.title.lowercase()
                            title.contains("study") ||
                                    title.contains("focus") ||
                                    title.contains("concentration") ||
                                    title.contains("lo-fi") ||
                                    title.contains("instrumental") ||
                                    title.contains("calm")
                        }
                        .take(100)

        return Playlist(
                id = "mood_study",
                name = "Study Focus",
                description = "Concentrate better",
                coverImageUrl = ArtistConstants.CATEGORY_IMAGES["Study"] ?: "",
                songs = studySongs,
                category = PlaylistCategory.GENERAL
        )
    }
}
