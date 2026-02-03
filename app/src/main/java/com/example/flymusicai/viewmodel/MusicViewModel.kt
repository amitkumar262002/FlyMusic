package com.example.flymusicai.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flymusicai.ai.AIRecommender
import com.example.flymusicai.data.AppUpdateConfig
import com.example.flymusicai.data.IndianMusicDatabase
import com.example.flymusicai.data.Music
import com.example.flymusicai.data.MusicRepository
import com.example.flymusicai.data.Playlist
import com.example.flymusicai.data.PlaylistCategory
import com.example.flymusicai.data.YouTubeMusicRepository
import com.example.flymusicai.api.OpenAIService
import com.example.flymusicai.manager.DownloadManager
import com.example.flymusicai.manager.UpdateManager
import com.example.flymusicai.player.AudioPlayerManager
import com.example.flymusicai.ui.theme.*
import java.util.UUID
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Data class to hold adaptive theme colors */
data class DynamicThemeColors(
        val primaryColor: Color = AmberGold,
        val backgroundGradient: List<Color> =
                listOf(Color(0xFF0A1612), Color(0xFF0D1F1A), Color(0xFF000000))
)

/** ViewModel for managing music playback, playlists, and AI recommendations */
class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val aiRecommender = AIRecommender()
    private val audioPlayer = AudioPlayerManager.getInstance(application.applicationContext)
    private val downloadManager = DownloadManager(application.applicationContext)
    private val updateManager = UpdateManager(application.applicationContext)
    private val youtubeService = com.example.flymusicai.api.YouTubeMusicService()
    private val preferencesManager =
            com.example.flymusicai.datastore.PreferencesManager(application.applicationContext)
    private val openAIService = com.example.flymusicai.api.OpenAIService()

    // Media notification receiver
    private val mediaActionReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    when (intent?.getStringExtra("action")) {
                        "PLAY" -> play()
                        "PAUSE" -> pause()
                        "NEXT" -> playNext()
                        "PREVIOUS" -> playPrevious()
                        "STOP" -> stop()
                    }
                }
            }

    // Music data
    private val _allMusic = MutableStateFlow<List<Music>>(emptyList())
    val allMusic: StateFlow<List<Music>> = _allMusic.asStateFlow()

    private val _songs = MutableStateFlow<List<Music>>(emptyList())
    val songs: StateFlow<List<Music>> = _songs.asStateFlow()

    private val _ringtones = MutableStateFlow<List<Music>>(emptyList())
    val ringtones: StateFlow<List<Music>> = _ringtones.asStateFlow()

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()

    private val _genreSongs = MutableStateFlow<List<Music>>(emptyList())
    val genreSongs: StateFlow<List<Music>> = _genreSongs.asStateFlow()

    // Currently playing music
    private val _currentSong = MutableStateFlow<Music?>(null)
    val currentSong: StateFlow<Music?> = _currentSong.asStateFlow()

    // 📊 Visualizer Data (Simulated frequency bands)
    private val _visualizerData = MutableStateFlow(List(32) { 0f })
    val visualizerData: StateFlow<List<Float>> = _visualizerData.asStateFlow()

    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    // 🎨 Dynamic Theming
    private val _dynamicThemeColors = MutableStateFlow(DynamicThemeColors())
    val dynamicThemeColors: StateFlow<DynamicThemeColors> = _dynamicThemeColors.asStateFlow()

    // 🤖 AI Assistant
    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _isAILoading = MutableStateFlow(false)
    val isAILoading: StateFlow<Boolean> = _isAILoading.asStateFlow()

    private val _currentPosition = MutableStateFlow(0f) // Progress 0-1
    val currentPosition: StateFlow<Float> = _currentPosition.asStateFlow()

    // Buffering state
    val isBuffering: StateFlow<Boolean> = audioPlayer.isBuffering

    // Actual track position/duration from player
    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs.asStateFlow()

    val trackDurationMs: StateFlow<Long> = audioPlayer.currentSongDurationMs

    private val _trackDuration = MutableStateFlow(0)
    val trackDuration: StateFlow<Int> = _trackDuration.asStateFlow()

    // Queue
    private val _currentQueue = MutableStateFlow<List<Music>>(emptyList())
    val currentQueue: StateFlow<List<Music>> = _currentQueue.asStateFlow()

    private val _currentQueueIndex = MutableStateFlow(0)
    val currentQueueIndex: StateFlow<Int> = _currentQueueIndex.asStateFlow()

    // Favorites
    private val _favoriteSongs = MutableStateFlow<List<Music>>(emptyList())
    val favoriteSongs: StateFlow<List<Music>> = _favoriteSongs.asStateFlow()

    // Search
    private val _searchResults = MutableStateFlow<List<Music>>(emptyList())
    val searchResults: StateFlow<List<Music>> = _searchResults.asStateFlow()

    private val _searchSuggestions = MutableStateFlow<List<String>>(emptyList())
    val searchSuggestions: StateFlow<List<String>> = _searchSuggestions.asStateFlow()

    // Recommendations
    private val _recommendedPlaylist = MutableStateFlow<Playlist?>(null)
    val recommendedPlaylist: StateFlow<Playlist?> = _recommendedPlaylist.asStateFlow()

    private val _forYouSongs = MutableStateFlow<List<Music>>(emptyList())
    val forYouSongs: StateFlow<List<Music>> = _forYouSongs.asStateFlow()

    private val _playerSuggestions = MutableStateFlow<List<Music>>(emptyList())
    val playerSuggestions: StateFlow<List<Music>> = _playerSuggestions.asStateFlow()

    // History
    private val _recentlyPlayed = MutableStateFlow<List<Music>>(emptyList())
    val recentlyPlayed: StateFlow<List<Music>> = _recentlyPlayed.asStateFlow()

    // Downloads
    private val _downloadedMusic = MutableStateFlow<List<Music>>(emptyList())
    val downloadedMusic: StateFlow<List<Music>> = _downloadedMusic.asStateFlow()

    // Home Section Categories
    private val _indiaRising = MutableStateFlow<List<Music>>(emptyList())
    val indiaRising: StateFlow<List<Music>> = _indiaRising.asStateFlow()

    private val _romanceNow = MutableStateFlow<List<Music>>(emptyList())
    val romanceNow: StateFlow<List<Music>> = _romanceNow.asStateFlow()

    private val _bestOf90s = MutableStateFlow<List<Music>>(emptyList())
    val bestOf90s: StateFlow<List<Music>> = _bestOf90s.asStateFlow()

    private val _hindiHits = MutableStateFlow<List<Music>>(emptyList())
    val hindiHits: StateFlow<List<Music>> = _hindiHits.asStateFlow()

    private val _albumsForYou = MutableStateFlow<List<Playlist>>(emptyList())
    val albumsForYou: StateFlow<List<Playlist>> = _albumsForYou.asStateFlow()

    // App Update
    private val _appUpdateConfig =
            MutableStateFlow<com.example.flymusicai.data.AppUpdateConfig?>(null)
    val appUpdateConfig: StateFlow<com.example.flymusicai.data.AppUpdateConfig?> =
            _appUpdateConfig.asStateFlow()

    private val _charts = MutableStateFlow<List<Music>>(emptyList())
    val charts: StateFlow<List<Music>> = _charts.asStateFlow()

    private val _moods =
            MutableStateFlow<List<Pair<String, String>>>(emptyMap<String, String>().toList())
    val moods: StateFlow<List<Pair<String, String>>> = _moods.asStateFlow()

    // Lyrics
    private val _currentLyrics =
            MutableStateFlow<List<com.example.flymusicai.ui.screens.LyricLine>>(emptyList())
    val currentLyrics: StateFlow<List<com.example.flymusicai.ui.screens.LyricLine>> =
            _currentLyrics.asStateFlow()

    // Per-Artist Songs
    private val _artistSongs = MutableStateFlow<Map<String, List<Music>>>(emptyMap())
    val dynamicArtistSongsMap: StateFlow<Map<String, List<Music>>> = _artistSongs.asStateFlow()

    // Popular/Top Songs
    private val _popularSongs = MutableStateFlow<List<Music>>(emptyList())
    val popularSongs: StateFlow<List<Music>> = _popularSongs.asStateFlow()

    private val _popularBhojpuri = MutableStateFlow<List<Music>>(emptyList())
    val popularBhojpuri: StateFlow<List<Music>> = _popularBhojpuri.asStateFlow()

    // Shuffle and Repeat
    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()

    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled.asStateFlow()

    // Network status
    private val _isOffline = mutableStateOf(false)
    val isOffline: State<Boolean> = _isOffline

    // Advanced: Prefetch Cache for Instant Play
    private val prefetchCache = mutableMapOf<String, String>()

    init {
        com.example.flymusicai.player.DownloadHelper.init(application)
        loadMusicData()
        setupAudioPlayer()
        checkNetworkStatus()
        observeAudioSettings()
        startVisualizer()
        ensureForYouContent()
        refreshDownloads() // Load downloaded songs
        checkForAppUpdate() // Check for updates

        // Observe Real Downloads from Media3
        viewModelScope.launch {
            com.example.flymusicai.player.DownloadHelper.downloads.collect { downloadMap ->
                downloadMap.values.forEach { download ->
                    if (download.state == androidx.media3.exoplayer.offline.Download.STATE_COMPLETED
                    ) {
                        // When a real download completes, save metadata to our manager for library
                        // display
                        val song = _allMusic.value.find { it.id == download.request.id }
                        song?.let {
                            if (!downloadManager.isDownloaded(it.id)) {
                                downloadManager.downloadSong(it)
                                refreshDownloads()
                            }
                        }
                    }
                }
            }
        }

        // Register media action receiver
        val filter = IntentFilter("com.example.flymusicai.MEDIA_ACTION")
        ContextCompat.registerReceiver(
                application.applicationContext,
                mediaActionReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
        )
        // Periodic check to ensure For You is never empty
        viewModelScope.launch {
            while (true) {
                delay(10000)
                if (_forYouSongs.value.isEmpty()) {
                    ensureForYouContent()
                }
            }
        }
    }

    private fun observeAudioSettings() {
        // Observe Equalizer Enabled and Bands together
        viewModelScope.launch {
            kotlinx.coroutines.flow
                    .combine(
                            preferencesManager.equalizerEnabledFlow,
                            preferencesManager.band60HzFlow,
                            preferencesManager.band230HzFlow,
                            preferencesManager.band910HzFlow,
                            preferencesManager.band3600HzFlow,
                            preferencesManager.band14000HzFlow
                    ) { array ->
                        val enabled = array[0] as Boolean
                        val bands = array.slice(1..5).map { it as Float }
                        enabled to bands
                    }
                    .collect { (enabled, bands) ->
                        audioPlayer.applyEqualizerSettings(enabled, bands)
                    }
        }

        // Observe Bass Boost
        viewModelScope.launch {
            preferencesManager.bassBoostFlow.collect { level -> audioPlayer.setBassBoost(level) }
        }

        // Observe Virtualizer
        viewModelScope.launch {
            preferencesManager.virtualizerFlow.collect { level ->
                audioPlayer.setVirtualizer(level)
            }
        }

        // Observe Loudness
        viewModelScope.launch {
            preferencesManager.loudnessFlow.collect { level -> audioPlayer.setLoudness(level) }
        }

        // Observe Reverb
        viewModelScope.launch {
            preferencesManager.reverbFlow.collect { preset -> audioPlayer.setReverb(preset) }
        }
    }

    private fun checkNetworkStatus() {
        _isOffline.value = !isNetworkAvailable()
    }

    /** Check for app updates */
    private fun checkForAppUpdate() {
        viewModelScope.launch {
            val updateInfo = updateManager.checkForUpdate()
            if (updateInfo != null) {
                _appUpdateConfig.value =
                        AppUpdateConfig(
                                version_name = updateInfo.versionName,
                                title = updateInfo.title,
                                message = updateInfo.message,
                                update_now_text = updateInfo.updateNowText,
                                later_text = updateInfo.laterText,
                                show_later_button = updateInfo.showLaterButton,
                                update_link = updateInfo.updateLink
                        )
            }
        }
    }

    fun isFavorite(songId: String): kotlinx.coroutines.flow.Flow<Boolean> = kotlinx.coroutines.flow.flow {
        _favoriteSongs.collect { favorites ->
            emit(favorites.any { it.id == songId })
        }
    }

    /** Setup audio player listeners and resume state */
    private fun setupAudioPlayer() {
        // Song completion listener
        audioPlayer.setOnSongCompleteListener { playNext() }

        // Error listener
        audioPlayer.setOnErrorListener { errorMessage ->
            Log.e("MusicViewModel", "Playback error: $errorMessage")

            // Auto-recovery for expired URLs (403 errors)
            if (errorMessage == "ERROR_EXPIRED_URL") {
                _currentSong.value?.let { song ->
                    Log.d(
                            "MusicViewModel",
                            "🔄 Detected expired URL for ${song.title}, clearing cache and retrying..."
                    )
                    prefetchCache.remove(song.id) // Clear broken cache
                    
                    // Seamless recovery: Preserve current position and current queue
                    val lastPosition = _currentPositionMs.value
                    playSong(song, _currentQueue.value, startPositionMs = lastPosition, isRetry = true)
                }
            }
        }

        // Position updates are now handled internally by AudioPlayerManager
        viewModelScope.launch {
            audioPlayer.currentPosition.collect { position -> _currentPosition.value = position }
        }
        viewModelScope.launch {
            audioPlayer.currentPositionMs.collect { ms -> _currentPositionMs.value = ms }
        }
        viewModelScope.launch {
            audioPlayer.currentSongDuration.collect { duration -> _trackDuration.value = duration }
        }

        // Sync Play/Pause state
        viewModelScope.launch {
            audioPlayer.isPlaying.collect { playing ->
                _isPlaying.value = playing
                // Update notification when state changes
                _currentSong.value?.let { updateNotificationService(it, playing) }
            }
        }

        // SMART SYNC & RESUME FEATURE
        viewModelScope.launch {
            delay(1500) // Ensure all music data handles are loaded
            
            val activeMediaId = audioPlayer.getCurrentMediaId()
            val lastMediaId = audioPlayer.getLastPlayedMediaId()
            val lastPosition = audioPlayer.getLastPlayedPosition()
            val wasPlaying = audioPlayer.wasPlayingLast()

            // State A: Already playing / paused in background (just sync UI)
            if (activeMediaId != null) {
                val activeSong = _allMusic.value.find { it.id == activeMediaId }
                if (activeSong != null) {
                    _currentSong.value = activeSong
                    updateLyrics(activeSong)
                    _isPlaying.value = audioPlayer.isPlaying.value
                    Log.d("MusicViewModel", "🔗 Linked to active session: ${activeSong.title}")
                    return@launch
                }
            }

            // State B: Fresh start (resume where we left off)
            if (lastMediaId != null && _currentSong.value == null) {
                Log.d("MusicViewModel", "🔄 Resuming last session: $lastMediaId at $lastPosition (WasPlaying: $wasPlaying)")
                val resumeSong = _allMusic.value.find { it.id == lastMediaId }
                if (resumeSong != null) {
                    // Resume with same play/pause state
                    playSong(resumeSong, startPositionMs = lastPosition, playImmediately = wasPlaying)
                }
            }
        }
    }

    /** Load all music data and generate recommendations */
    private fun loadMusicData() {
        viewModelScope.launch {
            // 0. FIREBASE PATH: Ultra-fast load from DB
            try {
                val firebaseSongs = com.example.flymusicai.data.FirebaseMusicManager.getAllSongs()
                if (firebaseSongs.isNotEmpty()) {
                    val normalized = firebaseSongs.map { it.copy(coverImageUrl = normalizeThumbnail(it)) }
                    _allMusic.value = normalized
                    _songs.value = normalized
                    Log.d("MusicViewModel", "🔥 Loaded ${firebaseSongs.size} songs from Firebase (Normalized)!")
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Firebase load failed", e)
            }

            // 1. FAST PATH: Load static content immediately (Offline/Cache first)
            try {
                val staticSongs =
                        IndianMusicDatabase.forYouSongs.map { dbSong ->
                            val durationParts = dbSong.duration.split(":")
                            val durationSecs =
                                    if (durationParts.size == 2) {
                                        (durationParts[0].toIntOrNull()
                                                ?: 0) * 60 + (durationParts[1].toIntOrNull() ?: 0)
                                    } else 300

                            val finalImageUrl = dbSong.imageUrl
                                    .replace("img.youtube.com", "i.ytimg.com") // Ensure YT high res fallback

                            Music(
                                    id =
                                            if (dbSong.id.startsWith("yt_")) dbSong.id
                                            else "yt_${dbSong.id}",
                                    title = dbSong.title,
                                    artist = dbSong.artist,
                                    duration = durationSecs,
                                    coverImageUrl = finalImageUrl,
                                    audioUrl = "",
                                    genre = dbSong.category.firstOrNull() ?: "Pop",
                                    album = dbSong.album,
                                    year = dbSong.year,
                                    lyrics = dbSong.lyrics
                            )
                        }.map { it.copy(coverImageUrl = normalizeThumbnail(it)) }

                _allMusic.value = staticSongs
                _songs.value = staticSongs
                _forYouSongs.value = staticSongs.shuffled() // Show all 100+ incrementally
                
                // Populate specific sections from static data as fallback
                if (_indiaRising.value.isEmpty()) {
                    _indiaRising.value = staticSongs.filter { it.genre == "Trending" || it.genre == "Bollywood" }
                }
                if (_popularSongs.value.isEmpty()) {
                    _popularSongs.value = staticSongs.take(20)
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Error loading static songs", e)
            }

            // 2. PARALLEL PATH: Launch Real Home Content Fetching (India Rising, Romance, etc.)
            launch { fetchHomeContent() }

            // 3. BACKGROUND PATH: Initial Diverse Jukebox populate
            launch { loadDiverseMusic() }

            // 4. SYNC PATH: Try to get existing library/YouTube data
            launch {
                var retries = 0
                while (retries < 3) {
                    try {
                        val musicWithYT = MusicRepository.getAllMusicWithYouTube()
                        if (musicWithYT.isNotEmpty()) {
                            val combined = (_allMusic.value + musicWithYT).distinctBy { it.id }
                            _allMusic.value = combined
                            _songs.value = combined.filter { !it.isRingtone }
                            _ringtones.value = combined.filter { it.isRingtone }
                            _playlists.value = MusicRepository.getPlaylistsWithYouTube()
                            ensureForYouContent()
                            refreshDownloads()
                            break
                        }
                    } catch (e: Exception) {
                        Log.w("MusicViewModel", "⚠️ Library sync attempt ${retries + 1} failed")
                    }
                    retries++
                    if (retries < 3) delay(1000)
                }
            }

            // 5. EXTENDED PATH: Fetch Top Indian Singles
            launch {
                try {
                    val topSingles = YouTubeMusicRepository.getTopIndianSingles()
                    _popularSongs.value = topSingles
                    com.example.flymusicai.data.FirebaseMusicManager.saveSongs(
                            topSingles
                    ) // Archive to DB
                    val updated = (_allMusic.value + topSingles).distinctBy { it.id }
                    _allMusic.value = updated
                    _songs.value = updated.filter { !it.isRingtone }
                } catch (e: Exception) {}
            }

            // 6. BHOJPURI PATH: Fetch Popular Bhojpuri
            launch {
                try {
                    val bhojpuri = YouTubeMusicRepository.getPopularBhojpuri()
                    _popularBhojpuri.value = bhojpuri
                    com.example.flymusicai.data.FirebaseMusicManager.saveSongs(
                            bhojpuri
                    ) // Archive to DB
                    val updated = (_allMusic.value + bhojpuri).distinctBy { it.id }
                    _allMusic.value = updated
                } catch (e: Exception) {}
            }
        }
    }

    /** Fetch 50+ real songs for a specific artist from YouTube */
    fun fetchArtistSongs(artistName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("MusicViewModel", "🔍 AI SEARCH: Fetching 50+ songs for artist: $artistName")
                // Search for "Artist Name all songs" for maximum results
                val songs = YouTubeMusicRepository.getMusicByCategory("$artistName all songs", 100)

                if (songs.isNotEmpty()) {
                    val currentMap = _artistSongs.value.toMutableMap()
                    currentMap[artistName] = songs
                    _artistSongs.value = currentMap

                    // Inject into global list for discovery
                    val updated = (_allMusic.value + songs).distinctBy { it.id }
                    _allMusic.value = updated
                    _songs.value = updated.filter { !it.isRingtone }

                    Log.d(
                            "MusicViewModel",
                            "✅ Populated ${songs.size} high-res tracks for $artistName"
                    )
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "❌ Failed to fetch artist songs for $artistName", e)
            }
        }
    }

    fun updateRegionAndLanguage(
            @Suppress("UNUSED_PARAMETER") languages: String,
            displayLanguage: String
    ) {
        val gl =
                when (displayLanguage) {
                    "Hindi" -> "IN"
                    "English" -> "US"
                    else -> "IN"
                }
        val hl =
                when (displayLanguage) {
                    "Hindi" -> "hi"
                    "English" -> "en"
                    else -> "hi"
                }
        YouTubeMusicRepository.updateRegionAndLanguage(gl, hl)
        // Refresh dynamic content when settings change
        fetchHomeContent()
    }

    private fun fetchHomeContent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("MusicViewModel", "🚀 Fetching home content sections in parallel...")
                val risingDeferred = async<List<Music>> { YouTubeMusicRepository.getIndiaRising() }
                val romanceDeferred = async<List<Music>> { YouTubeMusicRepository.getRomanceNow() }
                val hits90sDeferred = async<List<Music>> { YouTubeMusicRepository.getBestOf90s() }
                val hitsHindiDeferred = async<List<Music>> { YouTubeMusicRepository.getHindiHits() }
                val bhojpuriDeferred = async<List<Music>> { YouTubeMusicRepository.getPopularBhojpuri() }
                val albumsDeferred = async<List<Playlist>> { YouTubeMusicRepository.getAlbumsForYou() }
                val chartsDeferred = async<List<Music>> { YouTubeMusicRepository.getCharts() }
                val moodsDeferred = async<List<Pair<String, String>>> { YouTubeMusicRepository.getMoodsAndGenres() }
                val youtubePlaylistsDeferred = async<List<Playlist>> {
                    YouTubeMusicRepository.getYouTubePlaylists()
                }

                // Await all results
                val rising: List<Music> = risingDeferred.await()
                val romance: List<Music> = romanceDeferred.await()
                val hits90s: List<Music> = hits90sDeferred.await()
                val hitsHindi: List<Music> = hitsHindiDeferred.await()
                val bhojpuri: List<Music> = bhojpuriDeferred.await()
                val albums: List<Playlist> = albumsDeferred.await()
                val charts: List<Music> = chartsDeferred.await()
                val moods: List<Pair<String, String>> = moodsDeferred.await()
                val ytPlaylists: List<Playlist> = youtubePlaylistsDeferred.await()

                // Update state flows immediately
                withContext(Dispatchers.Main) {
                    _indiaRising.value = rising
                    _romanceNow.value = romance
                    _bestOf90s.value = hits90s
                    _hindiHits.value = hitsHindi
                    _popularBhojpuri.value = bhojpuri
                    
                    // Combine YouTube albums with our high-quality database albums
                    val dbAlbums = com.example.flymusicai.data.IndianMusicDatabase.popularAlbums.map { album ->
                        Playlist(
                            id = album.id,
                            name = album.name,
                            description = "${album.artist} • ${album.year}",
                            coverImageUrl = album.imageUrl,
                            songs = _allMusic.value.filter { it.album == album.name || it.artist.contains(album.artist) }.shuffled().take(album.songs).ifEmpty {
                                _allMusic.value.shuffled().take(20) // Fallback to interesting mix
                            }
                        )
                    }
                    val combinedAlbums: List<Playlist> = dbAlbums + albums
                    _albumsForYou.value = combinedAlbums.distinctBy { it.id }
                    
                    _charts.value = charts
                    _moods.value = moods
                }

                // SAVE TO FIREBASE for Fast Future Loading
                launch(Dispatchers.IO) {
                    val allFetched: List<Music> = (rising + romance + hits90s + hitsHindi + bhojpuri + charts)
                    val uniqueAll = allFetched.distinctBy { m: Music -> m.id }
                    com.example.flymusicai.data.FirebaseMusicManager.saveSongs(uniqueAll)
                }

                // Combine into a master playlist list for "View All"
                val specialPlaylists =
                        listOf(
                                Playlist(
                                        id = "90s",
                                        name = "Best of 90s",
                                        description = "Golden era hits",
                                        coverImageUrl = hits90s.firstOrNull()?.coverImageUrl ?: "",
                                        songs = hits90s,
                                        category = PlaylistCategory.FOR_YOU
                                ),
                                Playlist(
                                        id = "hindi_hits",
                                        name = "Hindi Hits",
                                        description = "Top Bollywood",
                                        coverImageUrl = hitsHindi.firstOrNull()?.coverImageUrl
                                                        ?: "",
                                        songs = hitsHindi,
                                        category = PlaylistCategory.BOLLYWOOD
                                ),
                                Playlist(
                                        id = "bhojpuri_hits",
                                        name = "Bhojpuri Hits",
                                        description = "Top Bhojpuri",
                                        coverImageUrl = bhojpuri.firstOrNull()?.coverImageUrl ?: "",
                                        songs = bhojpuri,
                                        category = PlaylistCategory.BOLLYWOOD
                                ),
                                Playlist(
                                        id = "india_rising",
                                        name = "India Rising",
                                        description = "New Voices",
                                        coverImageUrl = rising.firstOrNull()?.coverImageUrl ?: "",
                                        songs = rising,
                                        category = PlaylistCategory.TRENDING
                                ),
                                Playlist(
                                        id = "charts",
                                        name = "Global Charts",
                                        description = "Top 100",
                                        coverImageUrl = charts.firstOrNull()?.coverImageUrl ?: "",
                                        songs = charts,
                                        category = PlaylistCategory.TRENDING
                                )
                        )

                withContext(Dispatchers.Main) { _playlists.value = ytPlaylists + specialPlaylists }

                // Inject into global list for discovery
                val allNew: List<Music> = (rising + romance + hits90s + hitsHindi + charts)
                val distinctNew = allNew.distinctBy { m: Music -> m.id }
                val currentAll = _allMusic.value
                val updated = (currentAll + distinctNew).distinctBy { m: Music -> m.id }

                withContext(Dispatchers.Main) {
                    _allMusic.value = updated
                    _songs.value = updated.filter { m: Music -> !m.isRingtone }
                }
                Log.d("MusicViewModel", "✅ Home content populated in parallel")
            } catch (e: Exception) {
                Log.e("MusicViewModel", "Failed to fetch home content", e)
            }
        }
    }

    /** FlyMusic AI: Process natural language music requests and chat */
    fun askAI(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _isAILoading.value = true
            _aiResponse.value = "FlyMusic AI is thinking..."

            // Get chat response from OpenAI
            val chatResponse = openAIService.askFlyAI(query)
            _aiResponse.value = chatResponse

            // Parallel: Check if it's a music request to improve UX by starting playback if obvious
            val queryLower = query.lowercase()
            when {
                queryLower.contains("play") ||
                        queryLower.contains("listen") ||
                        queryLower.contains("song") -> {
                    val searchTerms =
                            queryLower
                                    .replace(Regex("\\bplay\\b"), "")
                                    .replace(Regex("\\blisten to\\b"), "")
                                    .replace(Regex("\\blisten\\b"), "")
                                    .replace(Regex("\\bsongs\\b"), "")
                                    .replace(Regex("\\bsong\\b"), "")
                                    .trim()

                    if (searchTerms.isNotEmpty()) {
                        searchMusic(searchTerms)
                        delay(1000)
                        if (_searchResults.value.isNotEmpty()) {
                            playSong(_searchResults.value.first(), _searchResults.value)
                        }
                    }
                }
                queryLower.contains("happy") ||
                        queryLower.contains("energetic") ||
                        queryLower.contains("dance") -> {
                    val songs = YouTubeMusicRepository.getMusicByCategory("party dance hits", 10)
                    playSong(songs.shuffled().first(), songs)
                }
                queryLower.contains("study") ||
                        queryLower.contains("focus") ||
                        queryLower.contains("relax") -> {
                    val songs = YouTubeMusicRepository.getMusicByCategory("lofi study focus", 10)
                    playSong(songs.shuffled().first(), songs)
                }
                queryLower.contains("bollywood") || queryLower.contains("hindi") -> {
                    val songs = _hindiHits.value.ifEmpty { YouTubeMusicRepository.getHindiHits() }
                    playSong(songs.shuffled().first(), songs)
                }
            }

            _isAILoading.value = false
        }
    }

    /** Update UI colors dynamically based on the current song */
    private fun updateDynamicTheme(song: Music) {
        val primary =
                when {
                    song.genre.contains("Romantic") -> Color(0xFFE91E63) // Pink
                    song.genre.contains("Party") -> Color(0xFFFF5722) // Orange
                    song.genre.contains("Bhakti") -> Color(0xFFFF9800) // Deep Orange
                    song.genre.contains("Pop") -> Color(0xFF2196F3) // Blue
                    song.genre.contains("Punjabi") -> Color(0xFFFFD600) // Vivid Yellow
                    else -> AmberGold
                }

        _dynamicThemeColors.value =
                DynamicThemeColors(
                        primaryColor = primary,
                        backgroundGradient =
                                listOf(
                                        primary.copy(alpha = 0.4f),
                                        Color(0xFF0A1612),
                                        Color(0xFF000000)
                                )
                )
    }

    /** Start/Stop a simulated visualizer for reactive UI elements */
    private fun startVisualizer() {
        viewModelScope.launch {
            while (true) {
                if (_isPlaying.value) {
                    _visualizerData.value = List(32) { Random.nextFloat() * 0.9f + 0.1f }
                } else {
                    _visualizerData.value = List(32) { 0.1f }
                }
                delay(100)
            }
        }
    }

    private fun updateLyrics(song: Music) {
        if (song.lyrics.isNotEmpty()) {
            _currentLyrics.value = parseLyrics(song.lyrics)
        } else {
            // If no lyrics, try to fetch or clear
            _currentLyrics.value = emptyList()
            // TODO: Fetch from API if needed
        }
    }

    private fun parseLyrics(rawLyrics: String): List<com.example.flymusicai.ui.screens.LyricLine> {
        val lines = rawLyrics.split("\n").filter { it.isNotBlank() }
        // Simple auto-sync simulation: 4 seconds per line
        return lines.mapIndexed { index, text ->
            com.example.flymusicai.ui.screens.LyricLine(
                timestamp = index * 4,
                text = text.trim()
            )
        }
    }

    private fun loadDiverseMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            val genres =
                    listOf(
                            "sadabahar",
                            "hindi",
                            "punjabi",
                            "bhojpuri",
                            "haryanvi",
                            "english",
                            "bollywood",
                            "romance",
                            "party",
                            "indie india",
                            "devotional",
                            "workout motivation",
                            "lo-fi hindi",
                            "ghazals",
                            "sufi",
                            "rajasthani",
                            "arijit singh hits",
                            "shreya ghoshal special",
                            "sidhu moose wala"
                    )
            val massiveList = mutableListOf<Music>()

            val deferredGenres =
                    genres.map { genre ->
                        async {
                            try {
                                YouTubeMusicRepository.getMusicByCategory(genre, 100)
                            } catch (e: Exception) {
                                emptyList<Music>()
                            }
                        }
                    }

            massiveList.addAll(deferredGenres.flatMap { it.await() })
            if (massiveList.isNotEmpty()) {
                val currentForYou = _forYouSongs.value.toMutableList()
                massiveList.shuffle()
                val merged = (currentForYou + massiveList).distinctBy { it.id }.take(1000)
                _forYouSongs.value = merged

                // Update global list too
                val all = (_allMusic.value + massiveList).distinctBy { it.id }
                _allMusic.value = all
                _songs.value = all.filter { !it.isRingtone }
            }
        }
    }

    private fun ensureForYouContent() {
        viewModelScope.launch {
            if (_forYouSongs.value.isEmpty()) {
                loadDiverseMusic()

                // Try recommendations first
                generateRecommendations()

                // If still empty after a brief wait, use allMusic as fallback
                delay(1500)
                if (_forYouSongs.value.isEmpty()) {
                    val fallback = _allMusic.value.shuffled().take(50)
                    if (fallback.isNotEmpty()) {
                        _forYouSongs.value = fallback
                    }
                }
            }
        }
    }

    /** Generate AI recommendations */
    fun generateRecommendations() {
        viewModelScope.launch(Dispatchers.IO) {
            val recommended =
                    aiRecommender.generateRecommendedPlaylist(
                            allMusic = _allMusic.value,
                            favoriteSongs = _favoriteSongs.value,
                            listeningHistory =
                                    _recentlyPlayed.value +
                                            _searchResults.value +
                                            _forYouSongs.value // Include for you songs to improve
                            // context
                            )

            // Limit to 100 songs as requested
            val enhancedSongs = recommended.songs.toMutableList()
            if (enhancedSongs.size < 100) {
                enhancedSongs.addAll(_allMusic.value.shuffled().take(100 - enhancedSongs.size))
            }

            _recommendedPlaylist.value =
                    recommended.copy(songs = enhancedSongs.distinctBy { it.id }.take(100))
        }
    }

    /** Play a song with optional start position for resume support */
    fun playSong(song: Music, queue: List<Music> = listOf(song), startPositionMs: Long = 0, playImmediately: Boolean = true, isRetry: Boolean = false) {
        viewModelScope.launch {
            Log.d("MusicViewModel", "🎵 [ACTION] playSong: ${song.title} at ${startPositionMs}ms (Play: $playImmediately)")

            // 1. Instantly update UI state to avoid lag
            _currentSong.value = song
            _currentQueue.value = queue
            _currentQueueIndex.value = queue.indexOf(song).coerceAtLeast(0)
            
            // --- Advanced Logic: Dynamic Theme & UI ---
            updateDynamicTheme(song)
            addToRecentlyPlayed(song)
            updateLyrics(song) 

            // 2. Resolve audio path (Cache -> Download -> Stream)
            var audioPath = prefetchCache[song.id]

            if (audioPath.isNullOrEmpty()) {
                audioPath =
                        when {
                            song.isDownloaded -> {
                                Log.d("MusicViewModel", "✅ Playing from local download")
                                downloadManager.getLocalFilePath(song.id)
                            }
                            !isRetry && song.audioUrl.isNotEmpty() &&
                                    !song.audioUrl.contains("placeholder") &&
                                    !song.audioUrl.contains("cdn.example.com") &&
                                    !song.audioUrl.contains("soundhelix.com") -> {
                                Log.d("MusicViewModel", "✅ Using existing audio URL")
                                song.audioUrl
                            }
                            else -> {
                                var finalId = ensureYoutubeId(song)

                                // Fetch stream URL with retry
                                var retryCount = 0
                                var streamUrl: String? = null

                                while (retryCount < 3 && streamUrl == null) {
                                    try {
                                        streamUrl = youtubeService.getSongStreamUrl(finalId)
                                        if (!streamUrl.isNullOrEmpty()) {
                                            prefetchCache[song.id] = streamUrl
                                            break
                                        } else if (retryCount == 0) {
                                            val forcedId = youtubeService.searchSong(song.title, song.artist)
                                            if (forcedId != null) finalId = forcedId
                                        }
                                    } catch (e: Exception) {
                                        Log.e("MusicViewModel", "Stream fetch failed (Attempt ${retryCount+1}): ${e.message}")
                                    }
                                    retryCount++
                                    if (retryCount < 3) delay(800)
                                }
                                streamUrl
                            }
                        }
            }

            if (audioPath.isNullOrEmpty()) {
                Log.e("MusicViewModel", "❌ Failed to resolve audio for: ${song.title}")
                _isPlaying.value = false
                return@launch
            }

            _isOffline.value = false
            Log.d("MusicViewModel", "✅ Final resolved path: ${audioPath.take(50)}...")

            // 3. Play using AudioPlayerManager
            try {
                audioPlayer.playSong(song.copy(audioUrl = audioPath), startPositionMs, playImmediately)
                
                if (playImmediately) {
                    _isPlaying.value = true
                    updateNotificationService(song, true)
                } else {
                    _isPlaying.value = false
                    updateNotificationService(song, false)
                }

                // --- Background Features ---
                updatePlayerSuggestions(song)
                fetchLyrics(song)
                updateActivityRecommendations(song)
                prefetchNextSongs(song, queue)
                generateAutoQueue(song)
                
            } catch (e: Exception) {
                Log.e("MusicViewModel", "❌ Playback execution failed", e)
                _isPlaying.value = false
            }
        }
    }

    /** Automatically populate the queue with related songs for a "never-ending" experience */
    private fun generateAutoQueue(song: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("MusicViewModel", "🔍 Generating auto-queue for: ${song.title}")
                val related =
                        YouTubeMusicRepository.getRelatedMusic(
                                song.id,
                                song.title,
                                song.artist,
                                limit = 50
                        )

                if (related.isNotEmpty()) {
                    val currentQueue = _currentQueue.value.toMutableList()

                    // Filter out duplicates
                    val newSongs =
                            related.filter { rel ->
                                currentQueue.none { it.id == rel.id } && rel.id != song.id
                            }

                    if (newSongs.isNotEmpty()) {
                        currentQueue.addAll(newSongs)
                        _currentQueue.value = currentQueue
                        Log.d(
                                "MusicViewModel",
                                "✅ Auto-generated queue: added ${newSongs.size} related songs (Total: ${currentQueue.size})"
                        )
                    }

                    // Also update player suggestions UI
                    _playerSuggestions.value = related
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "❌ Failed to generate auto-queue", e)
            }
        }
    }

    /** Update notification service with current song */
    private fun updateNotificationService(song: Music, isPlaying: Boolean) {
        try {
            Log.d(
                    "MusicViewModel",
                    "🔔 Starting notification service for: ${song.title} - Playing: $isPlaying"
            )
            val intent =
                    Intent(
                            getApplication(),
                            com.example.flymusicai.service.MusicPlayerService::class.java
                    )
            intent.putExtra("song_title", song.title)
            intent.putExtra("song_artist", song.artist)
            intent.putExtra("song_genre", song.genre)
            intent.putExtra("song_cover", song.coverImageUrl)
            intent.putExtra("is_playing", isPlaying)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getApplication<Application>().startForegroundService(intent)
            } else {
                getApplication<Application>().startService(intent)
            }
            Log.d("MusicViewModel", "✅ Notification service started successfully")
        } catch (e: Exception) {
            Log.e("MusicViewModel", "❌ Failed to update notification service", e)
        }
    }

    private fun updatePlayerSuggestions(song: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            val similar = aiRecommender.findSimilarSongs(song, _allMusic.value, limit = 20)
            _playerSuggestions.value = similar
        }
    }

    /** Help ensure we have a valid YouTube ID even for local database slugs */
    private suspend fun ensureYoutubeId(song: Music): String {
        val rawId = song.id.removePrefix("yt_")
        val isIdValid = rawId.length == 11 && !rawId.contains(" ") && rawId.matches(Regex("[a-zA-Z0-9_-]{11}"))

        if (isIdValid) return rawId

        Log.d("MusicViewModel", "🔍 Resolving slug ($rawId) to real YouTube ID...")
        return try {
            youtubeService.searchSong(song.title, song.artist) ?: rawId
        } catch (e: Exception) {
            rawId
        }
    }

    private fun prefetchNextSongs(currentSong: Music, queue: List<Music>) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentIndex = queue.indexOfFirst { it.id == currentSong.id }
            if (currentIndex == -1) return@launch

            // Prefetch next 3 songs for extra smoothness
            for (i in 1..3) {
                val nextIndex = (currentIndex + i) % queue.size
                val nextSong = queue[nextIndex]
                if (!prefetchCache.containsKey(nextSong.id) && !nextSong.isDownloaded) {
                    try {
                        // Ensure we use a real ID for prefetching too!
                        val finalId = ensureYoutubeId(nextSong)
                        val url = youtubeService.getSongStreamUrl(finalId)
                        if (url != null) {
                            prefetchCache[nextSong.id] = url
                            Log.d("MusicViewModel", "🚀 Prefetched URL: ${nextSong.title}")

                            // NEW: Start background caching of the actual audio data!
                            com.example.flymusicai.player.CacheManager.prefetchSongData(
                                    getApplication(),
                                    url
                            )
                        }
                    } catch (e: Exception) {
                        /* ignore */
                    }
                } else if (prefetchCache.containsKey(nextSong.id)) {
                    // Even if we already have the URL, ensure it's partially cached
                    prefetchCache[nextSong.id]?.let { url ->
                        com.example.flymusicai.player.CacheManager.prefetchSongData(
                                getApplication(),
                                url
                        )
                    }
                }
            }

            // Keep cache size optimized
            if (prefetchCache.size > 25) {
                val keysToRemove = prefetchCache.keys.take(prefetchCache.size - 25)
                keysToRemove.forEach { prefetchCache.remove(it) }
            }
        }
    }

    private fun addToRecentlyPlayed(song: Music) {
        val currentList = _recentlyPlayed.value.toMutableList()
        // Remove if already exists to move to top
        currentList.removeAll { it.id == song.id }
        // Add to front
        currentList.add(0, song)
        // Keep only top 20
        _recentlyPlayed.value = currentList.take(20)

        // Save to preferences (ThemeViewModel will handle it if we expose it)
        // For simplicity here, we'll assume the caller updates ThemeViewModel or we handle it here
    }

    fun setRecentlyPlayedList(songs: List<Music>) {
        _recentlyPlayed.value = songs
    }

    /** Update recommendations based on user activity (last played song) */
    private fun updateActivityRecommendations(song: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val related =
                        YouTubeMusicRepository.getRelatedMusic(song.id, song.title, song.artist)
                if (related.isNotEmpty()) {
                    // Update the sidebar recommendation
                    _recommendedPlaylist.value =
                            com.example.flymusicai.data.Playlist(
                                    id = "recommended_for_${song.id}",
                                    name = "Related to ${song.title}",
                                    description = "Based on your recent activity",
                                    coverImageUrl = song.coverImageUrl,
                                    songs = related,
                                    category =
                                            com.example.flymusicai.data.PlaylistCategory.RECOMMENDED
                            )

                    // Add new unique related songs to "For You" without clearing current ones
                    val currentForYou = _forYouSongs.value.toMutableList()
                    val existingIds = currentForYou.map { it.id }.toSet()
                    val newSongs = related.filter { it.id !in existingIds }

                    if (newSongs.isNotEmpty()) {
                        // Insert new songs a few positions ahead of current or at end
                        currentForYou.addAll(newSongs)
                        _forYouSongs.value = currentForYou.take(50) // Keep reasonable size
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Play song by ID (searches in all available lists) */
    fun playSongById(songId: String, currentList: List<Music> = emptyList()) {
        val song =
                currentList.find { it.id == songId }
                        ?: _searchResults.value.find { it.id == songId }
                                ?: _allMusic.value.find { it.id == songId }
                                ?: _recommendedPlaylist.value?.songs?.find { it.id == songId }

        song?.let { playSong(it, if (currentList.isNotEmpty()) currentList else listOf(it)) }
    }

    /** Download a song for offline playback */
    /** Start real background download */
    fun downloadSong(music: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            // First check if already downloaded
            try {
                Log.d("MusicViewModel", "📥 Starting download for: ${music.title}")

                // First check if already downloaded
                if (downloadManager.isDownloaded(music.id)) {
                    Log.d("MusicViewModel", "✅ Song already downloaded: ${music.title}")
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(
                                        getApplication(),
                                        "Already downloaded: ${music.title}",
                                        android.widget.Toast.LENGTH_SHORT
                                )
                                .show()
                    }
                    return@launch
                }

                // CRITICAL: Fetch FRESH stream URL right before download
                // YouTube URLs expire in 3-5 seconds, so we must get a new one
                val finalId = ensureYoutubeId(music)
                val freshStreamUrl =
                        try {
                            Log.d("MusicViewModel", "🔄 Fetching fresh stream URL for download...")
                            youtubeService.getSongStreamUrl(finalId)
                        } catch (e: Exception) {
                            Log.e("MusicViewModel", "❌ Failed to fetch stream URL", e)
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(
                                                getApplication(),
                                                "Download failed: Cannot fetch stream URL",
                                                android.widget.Toast.LENGTH_SHORT
                                        )
                                        .show()
                            }
                            return@launch
                        }

                if (freshStreamUrl.isNullOrEmpty()) {
                    Log.e("MusicViewModel", "❌ Empty stream URL received")
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(
                                        getApplication(),
                                        "Download failed: No stream available",
                                        android.widget.Toast.LENGTH_SHORT
                                )
                                .show()
                    }
                    return@launch
                }

                // Create music object with fresh URL
                val musicToDownload = music.copy(audioUrl = freshStreamUrl)

                // Start download with fresh URL
                com.example.flymusicai.player.DownloadHelper.startDownload(
                        getApplication<Application>().applicationContext,
                        musicToDownload
                )

                // Also save to local DownloadManager for tracking
                downloadManager.downloadSong(musicToDownload)
                refreshDownloads()

                Log.d("MusicViewModel", "✅ Download started successfully for: ${music.title}")

                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                                    getApplication(),
                                    "Downloading: ${music.title}",
                                    android.widget.Toast.LENGTH_SHORT
                            )
                            .show()
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "❌ Download error: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                                    getApplication(),
                                    "Download failed: ${e.message}",
                                    android.widget.Toast.LENGTH_SHORT
                            )
                            .show()
                }
            }
        }
    }

    /** Refresh the list of downloaded songs */
    fun refreshDownloads() {
        viewModelScope.launch(Dispatchers.IO) {
            val downloaded = downloadManager.getAllDownloadedSongs()
            _downloadedMusic.value = downloaded
            Log.d(
                    "MusicViewModel",
                    "📂 Downloads refreshed: ${downloaded.size} songs from persistent storage"
            )
        }
    }

    /** Toggle play/pause */
    fun togglePlayPause() {
        audioPlayer.togglePlayPause()
        viewModelScope.launch {
            delay(100)
            _isPlaying.value = audioPlayer.isPlaying.value
            _currentSong.value?.let { updateNotificationService(it, _isPlaying.value) }
        }
    }

    /** Play (for broadcast receiver) */
    fun play() {
        audioPlayer.play()
        _isPlaying.value = true
        _currentSong.value?.let { updateNotificationService(it, true) }
    }

    /** Pause (for broadcast receiver) */
    fun pause() {
        audioPlayer.pause()
        _isPlaying.value = false
        _currentSong.value?.let { updateNotificationService(it, false) }
    }

    /** Stop (for broadcast receiver) */
    fun stop() {
        audioPlayer.stop()
        _isPlaying.value = false
    }

    /** Play next song in queue with smart auto-play */
    fun playNext(forceNext: Music? = null) {
        val queue = _currentQueue.value
        if (queue.isEmpty()) return

        if (forceNext != null) {
            playSong(forceNext, queue)
            return
        }

        var nextIndex = _currentQueueIndex.value + 1

        if (_isShuffleEnabled.value) {
            // Shuffle mode: pick random song from current queue
            val availableIndices = queue.indices.filter { it != _currentQueueIndex.value }
            if (availableIndices.isNotEmpty()) {
                nextIndex = availableIndices.random()
            }
        } else if (nextIndex >= queue.size) {
            // Queue ended
            if (_isRepeatEnabled.value) {
                // Repeat mode: go back to start
                nextIndex = 0
            } else {
                // Smart Auto-Play: Continue with similar songs
                Log.d(
                        "MusicViewModel",
                        "🎵 Queue ended, starting smart auto-play with similar songs"
                )
                val currentSong = _currentSong.value
                if (currentSong != null && _playerSuggestions.value.isNotEmpty()) {
                    // Create extended queue with suggested similar songs
                    val extendedQueue = queue + _playerSuggestions.value.take(20)
                    _currentQueue.value = extendedQueue
                    nextIndex = queue.size // Play first suggestion
                    Log.d(
                            "MusicViewModel",
                            "✅ Extended queue with ${_playerSuggestions.value.size} similar songs"
                    )
                } else {
                    // Fallback: If no suggestions available, stop playback
                    audioPlayer.stop()
                    _isPlaying.value = false
                    Log.d("MusicViewModel", "⏸️ No similar songs available, stopping playback")
                    return
                }
            }
        }

        _currentQueueIndex.value = nextIndex
        val updatedQueue = _currentQueue.value // Use updated queue
        val nextSong = updatedQueue[nextIndex]
        _currentSong.value = nextSong
        _currentPosition.value = 0f

        // Play next song with real audio - USE VIEWMODEL.playSong to ensure YouTube IDs are
        // resolved!
        playSong(nextSong, updatedQueue)

        Log.d(
                "MusicViewModel",
                "▶️ Playing next: ${nextSong.title} (${nextIndex + 1}/${_currentQueue.value.size})"
        )
    }

    /** Play previous song in queue */
    fun playPrevious() {
        val queue = _currentQueue.value
        if (queue.isEmpty()) return

        var prevIndex = _currentQueueIndex.value - 1

        if (prevIndex < 0) {
            prevIndex = if (_isRepeatEnabled.value) queue.size - 1 else 0
        }

        _currentQueueIndex.value = prevIndex
        val prevSong = queue[prevIndex]
        _currentSong.value = prevSong
        _currentPosition.value = 0f

        // Play previous song with real audio
        playSong(prevSong, queue)
    }

    /** Seek to position (0-1) */
    fun seekTo(position: Float) {
        _currentPosition.value = position.coerceIn(0f, 1f)
        audioPlayer.seekTo(position)
    }

    // ========== Advanced Audio Effects ==========

    fun applyEqualizerSettings(enabled: Boolean, bands: List<Float>) {
        audioPlayer.applyEqualizerSettings(enabled, bands)
    }

    fun setBassBoost(level: Int) {
        audioPlayer.setBassBoost(level)
    }

    fun setVirtualizer(level: Int) {
        audioPlayer.setVirtualizer(level)
    }

    fun setLoudness(level: Int) {
        audioPlayer.setLoudness(level)
    }

    fun setReverb(preset: String) {
        audioPlayer.setReverb(preset)
    }

    fun setPlaybackSpeed(speed: Float) {
        audioPlayer.setPlaybackSpeed(speed)
    }

    /** Toggle shuffle mode */
    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }

    /** Toggle repeat mode */
    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
    }

    /** Toggle favorite status of a song */
    fun toggleFavorite(song: Music) {
        val currentFavorites = _favoriteSongs.value.toMutableList()
        val existingIndex = currentFavorites.indexOfFirst { it.id == song.id }

        if (existingIndex >= 0) {
            currentFavorites.removeAt(existingIndex)
        } else {
            currentFavorites.add(song.copy(isFavorite = true))
        }

        _favoriteSongs.value = currentFavorites

        // Update the song in all music list
        _allMusic.value =
                _allMusic.value.map {
                    if (it.id == song.id) it.copy(isFavorite = existingIndex < 0) else it
                }

        // Regenerate recommendations
        generateRecommendations()
    }

    /** Create a new playlist */
    fun createPlaylist(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val newPlaylist =
                    Playlist(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            description = "My custom playlist",
                            coverImageUrl =
                                    "https://c.saavncdn.com/editorial/charts_TopWeeklyHindi_139364_20231201123456_500x500.jpg",
                            songs = emptyList()
                    )
            _playlists.value = _playlists.value + newPlaylist
        }
    }

    /** Add song to play next in queue (after current song finishes) */
    fun addToPlayNext(song: Music) {
        val current = _currentQueue.value.toMutableList()
        val currentIndex = _currentQueueIndex.value

        // Remove if already in queue to avoid duplicates
        current.removeAll { it.id == song.id }

        // Insert right after current song
        val insertIndex = (currentIndex + 1).coerceAtMost(current.size)
        current.add(insertIndex, song)

        _currentQueue.value = current
        Log.d("MusicViewModel", "⏭️ Added to Play Next: ${song.title}")
    }

    fun addToQueue(song: Music) {
        val current = _currentQueue.value.toMutableList()
        if (!current.any { it.id == song.id }) {
            current.add(song)
            _currentQueue.value = current
            Log.d("MusicViewModel", "➕ Added to Queue: ${song.title}")
        }
    }

    /** Start Radio based on a song - Now enhanced with AI + YouTube recommendations */
    fun startRadio(song: Music) {
        viewModelScope.launch {
            Log.d("MusicViewModel", "📻 Starting Smart Radio for: ${song.title}")

            // 1. Get local recommendations (Fast)
            val localRadio = aiRecommender.findSimilarSongs(song, _allMusic.value, limit = 20)

            // 2. Get YouTube recommendations (Premium feel, fresh content)
            val ytRadio =
                    try {
                        YouTubeMusicRepository.getRelatedMusic(song.id, song.title, song.artist)
                    } catch (e: Exception) {
                        emptyList()
                    }

            // Combine and Shuffle for variety
            val fullRadioList =
                    (listOf(song) + localRadio + ytRadio)
                            .distinctBy { it.id }
                            .shuffled()
                            .toMutableList()

            // Ensure the clicked song is always first
            fullRadioList.remove(song)
            fullRadioList.add(0, song)

            playSong(song, fullRadioList)
        }
    }

    /** Share song with FlyMusic AI branding */
    fun shareSong(context: Context, song: Music) {
        val shareText =
                """
            🎵 Check out this amazing song on FlyMusic AI!
            
            ${song.title} - ${song.artist}
            
            Download FlyMusic AI for unlimited music streaming with AI-powered recommendations!
            
            🔗 https://music.youtube.com/watch?v=${song.id.removePrefix("yt_")}
        """.trimIndent()

        val intent =
                android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
        context.startActivity(android.content.Intent.createChooser(intent, "Share via"))
    }

    /** Share FlyMusic AI App */
    fun shareApp(context: Context) {
        val shareText =
                """
            🎵 FlyMusic AI - Smart Music Streaming App
            
            ✨ Features:
            • AI-Powered Music Recommendations
            • Smart Auto-Play & Queue Management
            • Download Songs for Offline Listening
            • Create & Manage Playlists
            • Intelligent Mood Detection
            • 50+ Million Songs from YouTube Music
            • Advanced Equalizer & Audio Effects
            • Lyrics Support
            
            🚀 Download Now and experience the future of music!
            
            Made with ❤️ in India 🇮🇳
        """.trimIndent()

        val intent =
                android.content.Intent().apply {
                    action = android.content.Intent.ACTION_SEND
                    putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                    putExtra(android.content.Intent.EXTRA_SUBJECT, "FlyMusic AI - Smart Music App")
                    type = "text/plain"
                }
        context.startActivity(android.content.Intent.createChooser(intent, "Share FlyMusic AI via"))
    }

    /** Delete a playlist */
    fun deletePlaylist(playlistId: String) {
        _playlists.value = _playlists.value.filter { it.id != playlistId }
    }

    /** Add a song to a playlist */
    fun addSongToPlaylist(song: Music, playlistId: String) {
        _playlists.value =
                _playlists.value.map { playlist ->
                    if (playlist.id == playlistId) {
                        if (!playlist.songs.any { it.id == song.id }) {
                            playlist.copy(songs = playlist.songs + song)
                        } else {
                            playlist
                        }
                    } else {
                        playlist
                    }
                }
    }

    /** Remove a song from a playlist */
    fun removeSongFromPlaylist(songId: String, playlistId: String) {
        _playlists.value =
                _playlists.value.map { playlist ->
                    if (playlist.id == playlistId) {
                        playlist.copy(songs = playlist.songs.filter { it.id != songId })
                    } else {
                        playlist
                    }
                }
    }



    /** Search music */
    fun searchMusic(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchResults.value =
                    if (query.isBlank()) {
                        emptyList()
                    } else {
                        MusicRepository.searchMusicWithYouTube(query)
                    }

            // Update suggestions as user types
            updateSearchSuggestions(query)

            // Refresh recommendations to include search context
            generateRecommendations()
        }
    }

    /** Update search suggestions based on query */
    fun updateSearchSuggestions(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchSuggestions.value = MusicRepository.getSearchSuggestions(query, 10)
        }
    }

    /** Load popular suggestions */
    fun loadPopularSuggestions() {
        viewModelScope.launch(Dispatchers.IO) {
            _searchSuggestions.value = MusicRepository.getSearchSuggestions("", 10)
        }
    }

    /** Clear search suggestions */
    fun clearSearchSuggestions() {
        _searchSuggestions.value = emptyList()
    }

    /** Fetch music by genre/category with local first fallback */
    fun fetchByGenre(genre: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Local Database Lookup (Instant)
                val localSongs = _allMusic.value.filter { 
                    it.genre.contains(genre, ignoreCase = true) || 
                    it.id.contains(genre, ignoreCase = true) 
                }
                
                if (localSongs.isNotEmpty()) {
                    _genreSongs.value = localSongs
                }

                // 2. Fetch more from Repository (Background)
                val songs = YouTubeMusicRepository.getMusicByCategory(genre, 100)
                
                // Combine and distinct
                val combined = (localSongs + songs).distinctBy { it.id }
                _genreSongs.value = combined

                // Also add to global list if not present
                val currentMusic = _allMusic.value.toMutableList()
                var modified = false
                songs.forEach { song ->
                    if (currentMusic.none { it.id == song.id }) {
                        currentMusic.add(song)
                        modified = true
                    }
                }
                if (modified) {
                    _allMusic.value = currentMusic
                    _songs.value = currentMusic.filter { !it.isRingtone }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Get trending music */
    fun getTrendingMusic(): List<Music> {
        return _allMusic.value.take(10)
    }

    /** Play a playlist */
    fun playPlaylist(playlist: Playlist) {
        if (playlist.songs.isNotEmpty()) {
            playSong(playlist.songs.first(), playlist.songs)
        } else {
             fetchPlaylistSongs(playlist.id)
        }
    }

    /** Fetch songs for a playlist/album dynamically */
    fun fetchPlaylistSongs(playlistId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allPlaylists = _playlists.value + _albumsForYou.value
            val playlist = allPlaylists.find { it.id == playlistId } ?: return@launch

            if (playlist.songs.isNotEmpty()) return@launch

            // Use the name and artist description to find the album
            val artistName = playlist.description.split("•").firstOrNull()?.trim() ?: ""
            val query = if (artistName.isNotEmpty()) "${playlist.name} $artistName album songs" else "${playlist.name} songs"
            
            try {
                // Fetch high quantity to filter better matches
                val songs = YouTubeMusicRepository.searchMusic(query)

                if (songs.isNotEmpty()) {
                    val updatedPlaylist = playlist.copy(songs = songs)

                    // Update _albumsForYou
                    val currentAlbums = _albumsForYou.value.toMutableList()
                    val index = currentAlbums.indexOfFirst { it.id == playlistId }
                    if (index != -1) {
                         currentAlbums[index] = updatedPlaylist
                         _albumsForYou.value = currentAlbums
                    }
                    
                    // Update _playlists
                    val currentPlaylists = _playlists.value.toMutableList()
                    val pIndex = currentPlaylists.indexOfFirst { it.id == playlistId }
                    if (pIndex != -1) {
                        currentPlaylists[pIndex] = updatedPlaylist
                        _playlists.value = currentPlaylists
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Clean up resources */
    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }

    /** Fetch lyrics for a song */
    /** Fetch lyrics for a song */
    private fun fetchLyrics(song: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            if (song.lyrics.isNotEmpty()) {
                val parsed = parseLyrics(song.lyrics)
                _currentLyrics.value = parsed
            } else {
                // Return empty or fetch from API. 
                // For now, consistent empty state or specific message is better than fake lyrics for real songs.
                // But to keep the "Mock" feel for demo if requested:
                 val lyrics =
                    listOf(
                            com.example.flymusicai.ui.screens.LyricLine(0, "🎵"),
                            com.example.flymusicai.ui.screens.LyricLine(5, "Lyrics not available"),
                            com.example.flymusicai.ui.screens.LyricLine(10, "Playing: ${song.title}"),
                            com.example.flymusicai.ui.screens.LyricLine(15, "By: ${song.artist}")
                    )
                _currentLyrics.value = lyrics
            }
        }
    }

    /** Check if the device is connected to the internet */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
                getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as
                        ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    private fun normalizeThumbnail(song: Music): String {
        if (song.coverImageUrl.isNotEmpty() && 
            !song.coverImageUrl.contains("picsum") && 
            !song.coverImageUrl.contains("placeholder")) {
            return song.coverImageUrl
        }
        
        val ytId = song.id.removePrefix("yt_")
        // If it's a 11-char YouTube ID, use it for thumbnail
        if (ytId.length == 11 && !ytId.contains("_") && !ytId.contains(" ")) {
            return "https://i.ytimg.com/vi/$ytId/hqdefault.jpg"
        }
        
        // Fallback placeholder if all fails
        return "https://c.saavncdn.com/734/Champagne-Talk-Hindi-2022-20221008011951-500x500.jpg" // Maan Meri Jaan as default
    }
}
