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
import androidx.core.content.ContextCompat
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flymusicai.ai.AIRecommender
import com.example.flymusicai.data.Music
import com.example.flymusicai.data.MusicRepository
import com.example.flymusicai.data.Playlist
import com.example.flymusicai.data.PlaylistCategory
import com.example.flymusicai.data.YouTubeMusicRepository
import com.example.flymusicai.manager.DownloadManager
import com.example.flymusicai.player.AudioPlayerManager
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random
import androidx.compose.ui.graphics.Color
import com.example.flymusicai.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Data class to hold adaptive theme colors */
data class DynamicThemeColors(
    val primaryColor: Color = AmberGold,
    val backgroundGradient: List<Color> = listOf(Color(0xFF0A1612), Color(0xFF0D1F1A), Color(0xFF000000))
)

/** ViewModel for managing music playback, playlists, and AI recommendations */
class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val aiRecommender = AIRecommender()
    private val audioPlayer = AudioPlayerManager(application.applicationContext)
    private val downloadManager = DownloadManager(application.applicationContext)
    private val youtubeService = com.example.flymusicai.api.YouTubeMusicService()
    private val preferencesManager = com.example.flymusicai.datastore.PreferencesManager(application.applicationContext)
    private val geminiService = com.example.flymusicai.api.GeminiAIService()
    
    // Media notification receiver
    private val mediaActionReceiver = object : BroadcastReceiver() {
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
    val currentPositionMs: StateFlow<Long> = audioPlayer.currentPositionMs
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

    // Lyrics
    private val _currentLyrics = MutableStateFlow<List<com.example.flymusicai.ui.screens.LyricLine>>(emptyList())
    val currentLyrics: StateFlow<List<com.example.flymusicai.ui.screens.LyricLine>> = _currentLyrics.asStateFlow()

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
        loadMusicData()
        setupAudioPlayer()
        checkNetworkStatus()
        observeAudioSettings()
        startVisualizer()
        ensureForYouContent()
        
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
            kotlinx.coroutines.flow.combine(
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
            }.collect { (enabled, bands) ->
                audioPlayer.applyEqualizerSettings(enabled, bands)
            }
        }

        // Observe Bass Boost
        viewModelScope.launch {
            preferencesManager.bassBoostFlow.collect { level ->
                audioPlayer.setBassBoost(level)
            }
        }

        // Observe Virtualizer
        viewModelScope.launch {
            preferencesManager.virtualizerFlow.collect { level ->
                audioPlayer.setVirtualizer(level)
            }
        }

        // Observe Loudness
        viewModelScope.launch {
            preferencesManager.loudnessFlow.collect { level ->
                audioPlayer.setLoudness(level)
            }
        }

        // Observe Reverb
        viewModelScope.launch {
            preferencesManager.reverbFlow.collect { preset ->
                audioPlayer.setReverb(preset)
            }
        }
    }

    private fun checkNetworkStatus() {
        _isOffline.value = !isNetworkAvailable()
    }

    /** Setup audio player listeners */
    private fun setupAudioPlayer() {
        // Song completion listener
        audioPlayer.setOnSongCompleteListener { playNext() }

        // Error listener
        audioPlayer.setOnErrorListener { errorMessage ->
            Log.e("MusicViewModel", "Playback error: $errorMessage")
            // Could show error to user via UI state
        }

        // Position updates are now handled internally by AudioPlayerManager
        viewModelScope.launch {
            audioPlayer.currentPosition.collect { position -> _currentPosition.value = position }
        }
        viewModelScope.launch {
            audioPlayer.currentSongDuration.collect { duration -> _trackDuration.value = duration }
        }
    }

    /** Load all music data and generate recommendations */
    private fun loadMusicData() {
        viewModelScope.launch {
            var retries = 0
            while (retries < 3) {
                try {
                    // Fetch everything including YouTube content
                    val musicWithYT = MusicRepository.getAllMusicWithYouTube()
                    if (musicWithYT.isNotEmpty()) {
                        _allMusic.value = musicWithYT
                        _songs.value = musicWithYT.filter { !it.isRingtone }
                        _ringtones.value = musicWithYT.filter { it.isRingtone }
                        
                        // Fetch playlists
                        _playlists.value = MusicRepository.getPlaylistsWithYouTube()
                        
                        ensureForYouContent()
                        break // Success!
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                retries++
                delay(2000) // Wait before retry
            }
            
            // Background load diverse content (100+ songs)
            loadDiverseMusic()
            
            // Fetch specialized sections for Home like Kreate
            fetchHomeContent()
        }
    }

    fun updateRegionAndLanguage(languages: String, displayLanguage: String) {
        val gl = when (displayLanguage) {
            "Hindi" -> "IN"
            "English" -> "US"
            else -> "IN"
        }
        val hl = when (displayLanguage) {
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
            val rising = YouTubeMusicRepository.getIndiaRising()
            val romance = YouTubeMusicRepository.getRomanceNow()
            val hits90s = YouTubeMusicRepository.getBestOf90s()
            val hitsHindi = YouTubeMusicRepository.getHindiHits()
            val albums = YouTubeMusicRepository.getAlbumsForYou()

            _indiaRising.value = rising
            _romanceNow.value = romance
            _bestOf90s.value = hits90s
            _hindiHits.value = hitsHindi
            _albumsForYou.value = albums

            // Update main playlists with these specialized ones for "View All" functionality
            val specialPlaylists = listOf(
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
                    coverImageUrl = hitsHindi.firstOrNull()?.coverImageUrl ?: "",
                    songs = hitsHindi,
                    category = PlaylistCategory.BOLLYWOOD
                )
            )
            
            _playlists.value = YouTubeMusicRepository.getYouTubePlaylists() + specialPlaylists
        }
    }

    /** FlyAI: Process natural language music requests and chat */
    fun askAI(query: String) {
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _isAILoading.value = true
            _aiResponse.value = "FlyAI is thinking..."
            
            // Get chat response from Gemini
            val chatResponse = geminiService.askFlyAI(query)
            _aiResponse.value = chatResponse
            
            // Parallel: Check if it's a music request to improve UX by starting playback if obvious
            val queryLower = query.lowercase()
            when {
                queryLower.contains("play") || queryLower.contains("listen") || queryLower.contains("song") -> {
                    val searchTerms = queryLower
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
                queryLower.contains("happy") || queryLower.contains("energetic") || queryLower.contains("dance") -> {
                    val songs = YouTubeMusicRepository.getMusicByCategory("party dance hits", 10)
                    playSong(songs.shuffled().first(), songs)
                }
                queryLower.contains("study") || queryLower.contains("focus") || queryLower.contains("relax") -> {
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
        val primary = when {
            song.genre.contains("Romantic") -> Color(0xFFE91E63) // Pink
            song.genre.contains("Party") -> Color(0xFFFF5722) // Orange
            song.genre.contains("Bhakti") -> Color(0xFFFF9800) // Deep Orange
            song.genre.contains("Pop") -> Color(0xFF2196F3) // Blue
            song.genre.contains("Punjabi") -> Color(0xFFFFD600) // Vivid Yellow
            else -> AmberGold
        }
        
        _dynamicThemeColors.value = DynamicThemeColors(
            primaryColor = primary,
            backgroundGradient = listOf(
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

    private fun loadDiverseMusic() {
        viewModelScope.launch(Dispatchers.IO) {
            val genres = listOf(
                "sadabahar", "hindi", "punjabi", "bhojpuri", "haryanvi", "english", 
                "bollywood", "romance", "party", "indie india", "devotional", 
                "workout motivation", "lo-fi hindi", "ghazals", "sufi", "rajasthani",
                "arijit singh hits", "shreya ghoshal special", "sidhu moose wala"
            )
            val massiveList = mutableListOf<Music>()
            
            genres.forEach { genre ->
                try {
                    val songs = YouTubeMusicRepository.getMusicByCategory(genre, 100)
                    massiveList.addAll(songs)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            
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
                            listeningHistory = _recentlyPlayed.value + _searchResults.value + _forYouSongs.value // Include for you songs to improve context
                    )
            
            // Limit to 100 songs as requested
            val enhancedSongs = recommended.songs.toMutableList()
            if (enhancedSongs.size < 100) {
                enhancedSongs.addAll(_allMusic.value.shuffled().take(100 - enhancedSongs.size))
            }
            
            _recommendedPlaylist.value = recommended.copy(songs = enhancedSongs.distinctBy { it.id }.take(100))
        }
    }

    /** Play a song */
    fun playSong(song: Music, queue: List<Music> = listOf(song)) {
        viewModelScope.launch {
            Log.d("MusicViewModel", "🎵 Attempting to play: ${song.title} by ${song.artist}")
            
            // Check prefetch cache first for "Instant Play"
            var audioPath = prefetchCache[song.id]
            
            if (audioPath == null) {
                audioPath = when {
                    song.isDownloaded -> {
                        Log.d("MusicViewModel", "✅ Playing from local download")
                        downloadManager.getLocalFilePath(song.id) // ✅ Play offline
                    }
                    song.audioUrl.isNotEmpty() &&
                            !song.audioUrl.contains("placeholder") &&
                            !song.audioUrl.contains("cdn.example.com") -> {
                        Log.d("MusicViewModel", "✅ Using existing audio URL")
                        song.audioUrl // Valid stream URL
                    }
                    else -> {
                        // Fetch real YouTube stream URL with retry logic
                        Log.d("MusicViewModel", "🔄 Fetching YouTube stream URL for: ${song.id}")
                        var retryCount = 0
                        var streamUrl: String? = null
                        
                        while (retryCount < 3 && streamUrl == null) {
                            try {
                                streamUrl = youtubeService.getSongStreamUrl(song.id.removePrefix("yt_"))
                                if (!streamUrl.isNullOrEmpty()) {
                                    Log.d("MusicViewModel", "✅ Successfully fetched stream URL (Attempt ${retryCount + 1})")
                                    break
                                } else {
                                    Log.w("MusicViewModel", "⚠️ Empty stream URL returned (Attempt ${retryCount + 1})")
                                }
                            } catch (e: Exception) {
                                Log.e("MusicViewModel", "❌ Error fetching stream (Attempt ${retryCount + 1}): ${e.message}", e)
                                e.printStackTrace()
                            }
                            retryCount++
                            if (retryCount < 3) {
                                delay(1000) // Wait 1 second before retry
                            }
                        }
                        
                        if (streamUrl == null) {
                            Log.e("MusicViewModel", "❌ Failed to fetch stream URL after $retryCount attempts")
                        }
                        streamUrl
                    }
                }
            } else {
                Log.d("MusicViewModel", "✅ Using cached audio URL")
            }

            if (audioPath.isNullOrEmpty()) {
                if (!isNetworkAvailable()) {
                    Log.e("MusicViewModel", "❌ No internet connection")
                    _isOffline.value = true
                    return@launch
                } else {
                    Log.e("MusicViewModel", "❌ Failed to get valid audio path for: ${song.title}")
                    _isPlaying.value = false
                    // Show error to user
                    return@launch
                }
            }
            _isOffline.value = false

            Log.d("MusicViewModel", "✅ Final audio path: ${audioPath.take(100)}...")

            _currentSong.value = song
            _currentQueue.value = queue
            _currentQueueIndex.value = queue.indexOf(song).coerceAtLeast(0)
            _currentPosition.value = 0f
            
            // --- Advanced Logic: Dynamic Theme & AI Stats ---
            updateDynamicTheme(song)

            // Add to recently played
            addToRecentlyPlayed(song)

            // Play using real audio player
            try {
                Log.d("MusicViewModel", "🎧 Starting audio playback...")
                audioPlayer.playSong(song.copy(audioUrl = audioPath))
                // Sync state immediately for "Instant Play" feeling
                _isPlaying.value = true
                Log.d("MusicViewModel", "✅ Playback started successfully!")
                
                // Update notification service
                updateNotificationService(song, true)
                
                // Update suggestions for the player
                updatePlayerSuggestions(song)
                
                // Fetch lyrics
                fetchLyrics(song)

                // Update "For You" recommendations based on this song
                updateActivityRecommendations(song)
                
                // PREFETCH next 2 songs for instant switching
                prefetchNextSongs(song, queue)
                
                // Auto-play next song when current finishes
                audioPlayer.setOnSongCompleteListener {
                    playNext()
                }
            } catch (e: Exception) {
                Log.e("MusicViewModel", "❌ Playback error: ${e.message}", e)
                _isPlaying.value = false
            }
        }
    }
    
    /** Update notification service with current song */
    private fun updateNotificationService(song: Music, isPlaying: Boolean) {
        try {
            Log.d("MusicViewModel", "🔔 Starting notification service for: ${song.title} - Playing: $isPlaying")
            val intent = Intent(getApplication(), com.example.flymusicai.service.MusicPlayerService::class.java)
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

    private fun prefetchNextSongs(currentSong: Music, queue: List<Music>) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentIndex = queue.indexOfFirst { it.id == currentSong.id }
            if (currentIndex == -1) return@launch
            
            // Prefetch next 2 songs
            for (i in 1..2) {
                val nextIndex = (currentIndex + i) % queue.size
                val nextSong = queue[nextIndex]
                if (!prefetchCache.containsKey(nextSong.id) && !nextSong.isDownloaded) {
                    try {
                        val url = youtubeService.getSongStreamUrl(nextSong.id.removePrefix("yt_"))
                        if (url != null) {
                            prefetchCache[nextSong.id] = url
                            Log.d("MusicViewModel", "🚀 Prefetched: ${nextSong.title}")
                        }
                    } catch (e: Exception) { /* ignore */ }
                }
            }
            
            // Keep cache size small
            if (prefetchCache.size > 20) {
                val keysToRemove = prefetchCache.keys.take(prefetchCache.size - 20)
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
                val related = YouTubeMusicRepository.getRelatedMusic(song.id)
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
    fun downloadSong(music: Music) {
        viewModelScope.launch(Dispatchers.IO) { downloadManager.downloadSong(music) }
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
    fun playNext() {
        val queue = _currentQueue.value
        if (queue.isEmpty()) return

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
                Log.d("MusicViewModel", "🎵 Queue ended, starting smart auto-play with similar songs")
                val currentSong = _currentSong.value
                if (currentSong != null && _playerSuggestions.value.isNotEmpty()) {
                    // Create extended queue with suggested similar songs
                    val extendedQueue = queue + _playerSuggestions.value.take(20)
                    _currentQueue.value = extendedQueue
                    nextIndex = queue.size // Play first suggestion
                    Log.d("MusicViewModel", "✅ Extended queue with ${_playerSuggestions.value.size} similar songs")
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

        // Play next song with real audio
        audioPlayer.playSong(nextSong)
        viewModelScope.launch {
            delay(100)
            _isPlaying.value = audioPlayer.isPlaying.value
            
            // Update notification
            updateNotificationService(nextSong, _isPlaying.value)
            
            // Update suggestions for continuous playback
            updatePlayerSuggestions(nextSong)
        }
        
        Log.d("MusicViewModel", "▶️ Playing next: ${nextSong.title} (${nextIndex + 1}/${_currentQueue.value.size})")
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
        audioPlayer.playSong(prevSong)
        viewModelScope.launch {
            delay(100)
            _isPlaying.value = audioPlayer.isPlaying.value
            
            // Update notification
            updateNotificationService(prevSong, _isPlaying.value)
        }
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
                            coverImageUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&q=80",
                            songs = emptyList()
                    )
            _playlists.value = _playlists.value + newPlaylist
        }
    }

    /** Add song to play next in queue */
    fun playNext(song: Music) {
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

    /** Add song to end of queue */
    fun addToQueue(song: Music) {
        val current = _currentQueue.value.toMutableList()
        if (!current.any { it.id == song.id }) {
            current.add(song)
            _currentQueue.value = current
            Log.d("MusicViewModel", "➕ Added to Queue: ${song.title}")
        }
    }

    /** Start Radio based on a song */
    fun startRadio(song: Music) {
        viewModelScope.launch {
            val radioSongs = aiRecommender.findSimilarSongs(song, _allMusic.value, limit = 50)
            val fullRadioList = (listOf(song) + radioSongs).distinctBy { it.id }
            playSong(song, fullRadioList)
            Log.d("MusicViewModel", "📻 Started Radio for: ${song.title}")
        }
    }

    /** Share song with FlyMusic AI branding */
    fun shareSong(context: Context, song: Music) {
        val shareText = """
            🎵 Check out this amazing song on FlyMusic AI!
            
            ${song.title} - ${song.artist}
            
            Download FlyMusic AI for unlimited music streaming with AI-powered recommendations!
            
            🔗 https://music.youtube.com/watch?v=${song.id.removePrefix("yt_")}
        """.trimIndent()
        
        val intent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        context.startActivity(android.content.Intent.createChooser(intent, "Share via"))
    }
    
    /** Share FlyMusic AI App */
    fun shareApp(context: Context) {
        val shareText = """
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
        
        val intent = android.content.Intent().apply {
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

    /** Check if a song is favorite */
    fun isFavorite(songId: String): Boolean {
        return _favoriteSongs.value.any { it.id == songId }
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

    /** Fetch music by genre/category */
    fun fetchByGenre(genre: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val songs = YouTubeMusicRepository.getMusicByCategory(genre, 100)
                _genreSongs.value = songs
                
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
        }
    }

    /** Clean up resources */
    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }

    /** Fetch lyrics for a song */
    private fun fetchLyrics(song: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            // Mocking lyrics for now - in production use an API like Musixmatch or YouTube captions
            val lyrics = listOf(
                com.example.flymusicai.ui.screens.LyricLine(0, "🎵"),
                com.example.flymusicai.ui.screens.LyricLine(5, "Welcome to Fly Music"),
                com.example.flymusicai.ui.screens.LyricLine(10, "Playing: ${song.title}"),
                com.example.flymusicai.ui.screens.LyricLine(15, "By: ${song.artist}"),
                com.example.flymusicai.ui.screens.LyricLine(20, "Enjoy the premium experience"),
                com.example.flymusicai.ui.screens.LyricLine(25, "Dil ki baat sunlo zara"),
                com.example.flymusicai.ui.screens.LyricLine(30, "Meri jaan ho tum yara"),
                com.example.flymusicai.ui.screens.LyricLine(35, "Har pal har ghadi"),
                com.example.flymusicai.ui.screens.LyricLine(40, "Tere sath hoon main khadi"),
                com.example.flymusicai.ui.screens.LyricLine(45, "FLY MUSIC AI..."),
                com.example.flymusicai.ui.screens.LyricLine(50, "Experience the magic"),
                com.example.flymusicai.ui.screens.LyricLine(100, "🎵")
            )
            _currentLyrics.value = lyrics
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
}
