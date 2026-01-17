package com.example.flymusicai.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flymusicai.ai.AIRecommender
import com.example.flymusicai.data.Music
import com.example.flymusicai.data.MusicRepository
import com.example.flymusicai.data.Playlist
import com.example.flymusicai.manager.DownloadManager
import com.example.flymusicai.player.AudioPlayerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

/**
 * ViewModel for managing music playback, playlists, and AI recommendations
 */
class MusicViewModel(application: Application) : AndroidViewModel(application) {
    
    private val aiRecommender = AIRecommender()
    private val audioPlayer = AudioPlayerManager(application.applicationContext)
    private val downloadManager = DownloadManager(application.applicationContext)
    
    // Music data
    private val _allMusic = MutableStateFlow<List<Music>>(emptyList())
    val allMusic: StateFlow<List<Music>> = _allMusic.asStateFlow()
    
    private val _songs = MutableStateFlow<List<Music>>(emptyList())
    val songs: StateFlow<List<Music>> = _songs.asStateFlow()
    
    private val _ringtones = MutableStateFlow<List<Music>>(emptyList())
    val ringtones: StateFlow<List<Music>> = _ringtones.asStateFlow()
    
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists.asStateFlow()
    
    // Currently playing music
    private val _currentSong = MutableStateFlow<Music?>(null)
    val currentSong: StateFlow<Music?> = _currentSong.asStateFlow()
    
    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()
    
    private val _currentPosition = MutableStateFlow(0f) // Progress 0-1
    val currentPosition: StateFlow<Float> = _currentPosition.asStateFlow()
    
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
    
    // Shuffle and Repeat
    private val _isShuffleEnabled = MutableStateFlow(false)
    val isShuffleEnabled: StateFlow<Boolean> = _isShuffleEnabled.asStateFlow()
    
    private val _isRepeatEnabled = MutableStateFlow(false)
    val isRepeatEnabled: StateFlow<Boolean> = _isRepeatEnabled.asStateFlow()

    // Network status
    private val _isOffline = mutableStateOf(false)
    val isOffline: State<Boolean> = _isOffline

    init {
        loadMusicData()
        setupAudioPlayer()
        checkNetworkStatus()
    }

    private fun checkNetworkStatus() {
        _isOffline.value = !isNetworkAvailable()
    }
    
    /**
     * Setup audio player listeners
     */
    private fun setupAudioPlayer() {
        audioPlayer.setOnSongCompleteListener {
            playNext()
        }
        
        // Start position updater
        viewModelScope.launch {
            while (true) {
                delay(500) // Update every 500ms
                if (audioPlayer.isPlaying.value) {
                    audioPlayer.updatePosition()
                    _currentPosition.value = audioPlayer.currentPosition.value
                }
            }
        }
    }
    
    /**
     * Load all music data and generate recommendations
     */
    private fun loadMusicData() {
        viewModelScope.launch {
            _allMusic.value = MusicRepository.getAllMusic()
            _songs.value = MusicRepository.getSongsOnly()
            _ringtones.value = MusicRepository.getRingtones()
            _playlists.value = MusicRepository.getPlaylists()
            generateRecommendations()
        }
    }
    
    /**
     * Generate AI recommendations
     */
    fun generateRecommendations() {
        viewModelScope.launch(Dispatchers.IO) {
            val recommended = aiRecommender.generateRecommendedPlaylist(
                allMusic = _allMusic.value,
                favoriteSongs = _favoriteSongs.value,
                listeningHistory = emptyList() // Can be tracked in a real app
            )
            _recommendedPlaylist.value = recommended
        }
    }
    
    /**
     * Play a song
     */
    fun playSong(song: Music, queue: List<Music> = listOf(song)) {
        val audioPath = if (song.isDownloaded) {
            downloadManager.getLocalFilePath(song.id) // ✅ Play offline
        } else {
            song.audioUrl // Stream online
        }

        if (audioPath.isNullOrEmpty()) {
            if (!isNetworkAvailable()) {
                _isOffline.value = true
                return
            }
        }
        _isOffline.value = false

        _currentSong.value = song
        _currentQueue.value = queue
        _currentQueueIndex.value = queue.indexOf(song).coerceAtLeast(0)
        _currentPosition.value = 0f
        
        // Play using real audio player
        audioPlayer.playSong(song.copy(audioUrl = audioPath!!))
        
        // Sync state
        viewModelScope.launch {
            delay(100)
            _isPlaying.value = audioPlayer.isPlaying.value
        }
    }

    /**
     * Download a song for offline playback
     */
    fun downloadSong(music: Music) {
        viewModelScope.launch(Dispatchers.IO) {
            downloadManager.downloadSong(music)
        }
    }
    
    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        audioPlayer.togglePlayPause()
        viewModelScope.launch {
            delay(100)
            _isPlaying.value = audioPlayer.isPlaying.value
        }
    }
    
    /**
     * Play next song in queue
     */
    fun playNext() {
        val queue = _currentQueue.value
        if (queue.isEmpty()) return
        
        var nextIndex = _currentQueueIndex.value + 1
        
        if (_isShuffleEnabled.value) {
            // Shuffle mode: pick random song
            val availableIndices = queue.indices.filter { it != _currentQueueIndex.value }
            if (availableIndices.isNotEmpty()) {
                nextIndex = availableIndices.random()
            }
        } else if (nextIndex >= queue.size) {
            if (_isRepeatEnabled.value) {
                nextIndex = 0
            } else {
                audioPlayer.stop()
                _isPlaying.value = false
                return
            }
        }
        
        _currentQueueIndex.value = nextIndex
        val nextSong = queue[nextIndex]
        _currentSong.value = nextSong
        _currentPosition.value = 0f
        
        // Play next song with real audio
        audioPlayer.playSong(nextSong)
        viewModelScope.launch {
            delay(100)
            _isPlaying.value = audioPlayer.isPlaying.value
        }
    }
    
    /**
     * Play previous song in queue
     */
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
        }
    }
    
    /**
     * Seek to position (0-1)
     */
    fun seekTo(position: Float) {
        _currentPosition.value = position.coerceIn(0f, 1f)
        audioPlayer.seekTo(position)
    }
    
    /**
     * Toggle shuffle mode
     */
    fun toggleShuffle() {
        _isShuffleEnabled.value = !_isShuffleEnabled.value
    }
    
    /**
     * Toggle repeat mode
     */
    fun toggleRepeat() {
        _isRepeatEnabled.value = !_isRepeatEnabled.value
    }
    
    /**
     * Toggle favorite status of a song
     */
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
        _allMusic.value = _allMusic.value.map {
            if (it.id == song.id) it.copy(isFavorite = existingIndex < 0) else it
        }
        
        // Regenerate recommendations
        generateRecommendations()
    }
    
    /**
     * Check if a song is favorite
     */
    fun isFavorite(songId: String): Boolean {
        return _favoriteSongs.value.any { it.id == songId }
    }
    
    /**
     * Search music
     */
    fun searchMusic(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchResults.value = if (query.isBlank()) {
                emptyList()
            } else {
                MusicRepository.searchMusic(query)
            }
            
            // Update suggestions as user types
            updateSearchSuggestions(query)
        }
    }
    
    /**
     * Update search suggestions based on query
     */
    fun updateSearchSuggestions(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _searchSuggestions.value = MusicRepository.getSearchSuggestions(query, 10)
        }
    }
    
    /**
     * Load popular suggestions
     */
    fun loadPopularSuggestions() {
        viewModelScope.launch(Dispatchers.IO) {
            _searchSuggestions.value = MusicRepository.getPopularSuggestions(10)
        }
    }
    
    /**
     * Clear search suggestions
     */
    fun clearSearchSuggestions() {
        _searchSuggestions.value = emptyList()
    }
    
    /**
     * Get trending music
     */
    fun getTrendingMusic(): List<Music> {
        return MusicRepository.getTrendingMusic(10)
    }
    
    /**
     * Play a playlist
     */
    fun playPlaylist(playlist: Playlist) {
        if (playlist.songs.isNotEmpty()) {
            playSong(playlist.songs.first(), playlist.songs)
        }
    }
    
    /**
     * Clean up resources
     */
    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }

    /**
     * Check if the device is connected to the internet
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
