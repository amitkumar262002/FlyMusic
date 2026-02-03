package com.example.flymusicai.player

import android.content.Context
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import android.media.audiofx.PresetReverb
import android.media.audiofx.Virtualizer
import android.util.Log
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.flymusicai.data.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 🎵 Advanced Audio Player Manager Professional audio playback with ExoPlayer and audio effects
 * Features: Gapless playback, audio effects, crossfade, error recovery
 * Refactored to SINGLETON to ensure Service and ViewModel share the same player.
 */
class AudioPlayerManager private constructor(private val context: Context) {

    companion object {
        private const val TAG = "AudioPlayerManager"
        private const val POSITION_UPDATE_INTERVAL = 100L // Update every 100ms
        private const val SAVE_STATE_INTERVAL = 1000L // Save state every 1s for better accuracy
        private const val FADE_DURATION = 300L // Crossfade duration in ms
        private const val PREFS_NAME = "playback_state"
        private const val KEY_MEDIA_ID = "last_media_id"
        private const val KEY_POSITION = "last_position_ms"
        private const val KEY_IS_PLAYING = "last_is_playing"
        
        @Volatile
        private var instance: AudioPlayerManager? = null

        fun getInstance(context: Context): AudioPlayerManager {
            return instance ?: synchronized(this) {
                instance ?: AudioPlayerManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // ExoPlayer instance
    @androidx.media3.common.util.UnstableApi
    private val player: ExoPlayer =
            ExoPlayer.Builder(context)
                    .setLoadControl(
                        androidx.media3.exoplayer.DefaultLoadControl.Builder()
                            .setBufferDurationsMs(
                                2500,   // MIN_BUFFER_MS: (Fixed) Must be >= AfterRebufferMs
                                60000,  // MAX_BUFFER_MS: buffer up to 60s
                                1000,   // BUFFER_FOR_PLAYBACK_MS: starts playing after 1s
                                2000    // BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS: Must be <= minBufferMs
                            )
                            .setPrioritizeTimeOverSizeThresholds(true)
                            .build()
                    )
                    .setMediaSourceFactory(
                            androidx.media3.exoplayer.source.DefaultMediaSourceFactory(context)
                                    .setDataSourceFactory(
                                            CacheManager.getCacheDataSourceFactory(context)
                                    )
                    )
                    .build()

    // Audio Effects
    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null
    private var presetReverb: PresetReverb? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null
    
    // Persistent settings to survive session changes
    private var isEqualizerEnabled = false
    private var eqBandLevels = mutableListOf(0f, 0f, 0f, 0f, 0f)
    private var bassBoostLevel = 0
    private var virtualizerLevel = 0
    private var loudnessLevel = 0
    private var reverbPresetName = "None"

    // State Flows
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0f)
    val currentPosition: StateFlow<Float> = _currentPosition

    private val _currentPositionMs = MutableStateFlow(0L)
    val currentPositionMs: StateFlow<Long> = _currentPositionMs

    private val _currentSongDurationMs = MutableStateFlow(0L)
    val currentSongDurationMs: StateFlow<Long> = _currentSongDurationMs

    private val _currentSongDuration = MutableStateFlow(0)
    val currentSongDuration: StateFlow<Int> = _currentSongDuration

    private val _isBuffering = MutableStateFlow(false)
    val isBuffering: StateFlow<Boolean> = _isBuffering

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed

    private val _audioSessionId = MutableStateFlow(0)
    val audioSessionId: StateFlow<Int> = _audioSessionId

    // Callbacks
    private var onSongComplete: (() -> Unit)? = null
    private var onError: ((String) -> Unit)? = null

    // Coroutine for position updates
    private val scope = CoroutineScope(Dispatchers.Main)
    private var positionUpdateJob: Job? = null
    private var fadeJob: Job? = null

    init {
        setupPlayer()
        setupAudioEffects()
        startPositionUpdater()
        // Initial volume is 0 for fade-in on first play
        player.volume = 0f
    }

    /** Setup ExoPlayer with listeners */
    private fun setupPlayer() {
        player.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_IDLE -> {
                                Log.d(TAG, "Player state: IDLE")
                                _isBuffering.value = false
                            }
                            Player.STATE_BUFFERING -> {
                                Log.d(TAG, "Player state: BUFFERING")
                                _isBuffering.value = true
                            }
                            Player.STATE_READY -> {
                                Log.d(TAG, "Player state: READY")
                                _isBuffering.value = false
                                val durationMs = player.duration
                                if (durationMs > 0) {
                                    _currentSongDurationMs.value = durationMs
                                    _currentSongDuration.value = (durationMs / 1000).toInt()
                                }
                                Log.d(TAG, "Duration: ${_currentSongDuration.value} seconds")
                            }
                            Player.STATE_ENDED -> {
                                Log.d(TAG, "Player state: ENDED")
                                _isPlaying.value = false
                                _isBuffering.value = false
                                onSongComplete?.invoke()
                            }
                        }
                    }

                    override fun onIsPlayingChanged(playing: Boolean) {
                        Log.d(TAG, "Playing changed: $playing")
                        _isPlaying.value = playing
                    }

                    override fun onAudioSessionIdChanged(audioSessionId: Int) {
                        Log.d(TAG, "Audio session ID changed: $audioSessionId")
                        if (audioSessionId != 0 && audioSessionId != _audioSessionId.value) {
                            _audioSessionId.value = audioSessionId
                            setupAudioEffects()
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        Log.e(TAG, "Playback error: ${error.message}", error)
                        _isPlaying.value = false
                        _isBuffering.value = false

                        // Detect 403 Specialized error for auto-retry
                        val cause = error.cause
                        val is403 = cause is androidx.media3.datasource.HttpDataSource.InvalidResponseCodeException 
                                    && cause.responseCode == 403

                        val errorMessage = when {
                            is403 -> "ERROR_EXPIRED_URL"
                            error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ->
                                "Network connection failed"
                            error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
                                "Audio file not found"
                            error.errorCode == PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ->
                                "Audio decoder initialization failed"
                            error.errorCode == PlaybackException.ERROR_CODE_TIMEOUT -> "Connection timeout"
                            else -> "Playback error: ${error.message}"
                        }

                        onError?.invoke(errorMessage)
                    }
                }
        )

        // Enable gapless playback
        player.setAudioAttributes(
                androidx.media3.common.AudioAttributes.Builder()
                        .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC)
                        .setUsage(androidx.media3.common.C.USAGE_MEDIA)
                        .build(),
                true
        )
    }

    private fun setupAudioEffects() {
        val sessionId = player.audioSessionId
        _audioSessionId.value = sessionId

        if (sessionId == 0) {
            Log.w(TAG, "Invalid audio session ID, effects not initialized")
            return
        }

        Log.d(TAG, "🚀 [PRO] Setting up premium audio engine for session: $sessionId")

        scope.launch(Dispatchers.Default) {
            // Release old effects if any to avoid session leaks
            releaseEffectsOnly()

            // Initialize Equalizer with non-blocking retry logic and priority fallback
            var retryCount = 0
            while (retryCount < 3 && equalizer == null) {
                try {
                    // Try with high priority first, fallback to 0 if it fails
                    val priority = if (retryCount == 0) 100 else 0
                    equalizer = Equalizer(priority, sessionId).apply {
                        enabled = isEqualizerEnabled
                    }
                    Log.d(TAG, "✅ Equalizer initialized with ${equalizer?.numberOfBands} bands (priority: $priority)")
                    break
                } catch (e: Exception) {
                    Log.w(TAG, "⚠️ Equalizer init attempt ${retryCount + 1} failed: ${e.message}")
                    retryCount++
                    delay(500) // Give system a bit more time
                }
            }

            // Initialize Bass Boost (High Priority)
            try {
                bassBoost = BassBoost(100, sessionId).apply {
                    enabled = bassBoostLevel > 0
                }
                Log.d(TAG, "✅ Professional Bass Boost engine initialized")
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Bass Boost not supported on this device session")
                bassBoost = null
            }

            // Initialize Virtualizer (3D Surround)
            try {
                virtualizer = Virtualizer(100, sessionId).apply {
                    enabled = virtualizerLevel > 0
                }
                Log.d(TAG, "✅ 3D Virtualizer engine initialized")
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Virtualizer not supported")
                virtualizer = null
            }

            // Initialize Loudness Enhancer (Extreme Gain)
            try {
                loudnessEnhancer = LoudnessEnhancer(sessionId).apply {
                    enabled = loudnessLevel > 0
                }
                Log.d(TAG, "✅ Extreme Gain engine initialized")
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Loudness Enhancer not supported")
                loudnessEnhancer = null
            }

            // Initialize Preset Reverb
            try {
                presetReverb = PresetReverb(100, sessionId).apply {
                    enabled = reverbPresetName != "None"
                }
                Log.d(TAG, "✅ Reverb engine initialized")
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Reverb not supported")
                presetReverb = null
            }

            // Apply stored settings to new engine instances
            applyCurrentSettings()
        }
    }

    /** Release effects without releasing the player */
    private fun releaseEffectsOnly() {
        equalizer?.release()
        bassBoost?.release()
        virtualizer?.release()
        presetReverb?.release()
        loudnessEnhancer?.release()
        
        equalizer = null
        bassBoost = null
        virtualizer = null
        presetReverb = null
        loudnessEnhancer = null
    }

    /** Apply all stored settings to current effect instances */
    private fun applyCurrentSettings() {
        applyEqualizerSettings(isEqualizerEnabled, eqBandLevels)
        setBassBoost(bassBoostLevel)
        setVirtualizer(virtualizerLevel)
        setLoudness(loudnessLevel)
        setReverb(reverbPresetName)
    }

    /** Play a song from URL at specific position */
    fun playSong(song: Music, startPositionMs: Long = 0, playImmediately: Boolean = true) {
        try {
            Log.d(TAG, "▶️ [ACTION] playSong: ${song.title} (ID: ${song.id}) at ${startPositionMs}ms (Play: $playImmediately)")
            Log.d(TAG, "URL: ${song.audioUrl}")

            if (song.audioUrl.isEmpty()) {
                Log.e(TAG, "❌ Empty audio URL for: ${song.title}")
                _isPlaying.value = false
                onError?.invoke("Invalid audio URL")
                return
            }

            // Stop and clear current playback
            player.stop()
            player.clearMediaItems()

            // Reset position
            _currentPosition.value = 0f
            _currentSongDuration.value = song.duration

            // Create and set media item
            val mediaItemBuilder =
                    androidx.media3.common.MediaItem.Builder()
                            .setUri(song.audioUrl)
                            .setMediaId(song.id)

            // Check if it's likely a YouTube DASH stream
            if (song.id.startsWith("yt_") || song.audioUrl.contains("googlevideo.com")) {
                mediaItemBuilder.setMimeType(androidx.media3.common.MimeTypes.AUDIO_UNKNOWN)
            }

            val mediaItem = mediaItemBuilder.build()

            player.setMediaItem(mediaItem)

            // Seek BEFORE preparing for efficient buffering
            if (startPositionMs > 0) {
                player.seekTo(startPositionMs)
                _currentPositionMs.value = startPositionMs
                
                // Also update the float position if duration is known (compatibility)
                if (song.duration > 0) {
                    _currentPosition.value = (startPositionMs.toFloat() / (song.duration * 1000).toFloat()).coerceIn(0f, 1f)
                }
            }

            // Prepare
            player.prepare()
            
            if (playImmediately) {
                player.volume = 0f
                player.playWhenReady = true
                fadeIn()
                _isPlaying.value = true
                Log.d(TAG, "✅ Song starting with fade-in")
            } else {
                player.playWhenReady = false
                player.volume = 1f
                _isPlaying.value = false
                Log.d(TAG, "✅ Song loaded and PAUSED for resume")
            }

            // Re-sync audio session ID and effects for the new track
            val newSessionId = player.audioSessionId
            if (newSessionId != _audioSessionId.value) {
                _audioSessionId.value = newSessionId
                setupAudioEffects()
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error playing song: ${song.title}", e)
            e.printStackTrace()
            _isPlaying.value = false
            onError?.invoke("Failed to play song: ${e.message}")
        }
    }

    /** Toggle play/pause */
    fun togglePlayPause() {
        try {
            if (player.isPlaying) {
                pause()
            } else {
                play()
            }
            Log.d(TAG, "⏯️ Toggle play/pause: ${_isPlaying.value}")
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling play/pause", e)
        }
    }

    /** Pause playback with Fade-Out */
    fun pause() {
        try {
            scope.launch {
                fadeOut()
                savePlaybackState() // Save state on pause
                player.pause()
                _isPlaying.value = false
                Log.d(TAG, "⏸️ Paused with fade-out")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing", e)
        }
    }

    /** Resume playback with Fade-In */
    fun play() {
        try {
            player.play()
            fadeIn()
            _isPlaying.value = true
            Log.d(TAG, "▶️ Playing with fade-in")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing", e)
        }
    }

    /** Stop playback with Fade-Out */
    fun stop() {
        try {
            scope.launch {
                fadeOut()
                player.stop()
                _isPlaying.value = false
                _currentPosition.value = 0f
                Log.d(TAG, "⏹️ Stopped with fade-out")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping", e)
        }
    }

    private fun fadeIn() {
        fadeJob?.cancel()
        val duration = FADE_DURATION
        val steps = 15
        val stepMs = duration / steps
        val volumeStep = 1.0f / steps
        
        fadeJob = scope.launch {
            var currentVol = player.volume
            for (i in 1..steps) {
                if (!isActive) break
                delay(stepMs)
                currentVol += volumeStep
                player.volume = currentVol.coerceIn(0f, 1f)
                if (currentVol >= 1.0f) break
            }
            player.volume = 1f
        }
    }

    private suspend fun fadeOut() {
        fadeJob?.cancel()
        val duration = FADE_DURATION
        val steps = 15
        val stepMs = duration / steps
        val volumeStep = player.volume / steps
        
        val job = scope.launch {
            var currentVol = player.volume
            for (i in 1..steps) {
                if (!isActive) break
                delay(stepMs)
                currentVol -= volumeStep
                player.volume = currentVol.coerceIn(0f, 1f)
                if (currentVol <= 0f) break
            }
            player.volume = 0f
        }
        fadeJob = job
        job.join() // Wait for fade out to complete before pausing/stopping
    }

    /** Seek to position (0-1) */
    fun seekTo(position: Float) {
        try {
            val duration = player.duration
            if (duration > 0) {
                val seekPosition = (position * duration).toLong().coerceAtLeast(0)
                player.seekTo(seekPosition)
                _currentPosition.value = position.coerceIn(0f, 1f)
                Log.d(TAG, "⏩ Seeked to property ratio: ${(position * 100).toInt()}%")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking with ratio", e)
        }
    }

    /** Seek to specific millisecond position */
    fun seekTo(positionMs: Long) {
        try {
            player.seekTo(positionMs.coerceAtLeast(0))
            val duration = player.duration
            if (duration > 0) {
                _currentPosition.value = (positionMs.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
            }
            _currentPositionMs.value = positionMs
            Log.d(TAG, "⏩ Seeked to: $positionMs ms")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking to ms", e)
        }
    }

    /** Set playback speed */
    fun setPlaybackSpeed(speed: Float) {
        try {
            val validSpeed = speed.coerceIn(0.25f, 2.0f)
            player.setPlaybackSpeed(validSpeed)
            _playbackSpeed.value = validSpeed
            Log.d(TAG, "🎚️ Playback speed: ${validSpeed}x")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting playback speed", e)
        }
    }

    /** Apply equalizer settings */
    fun applyEqualizerSettings(enabled: Boolean, bands: List<Float> = listOf(0f, 0f, 0f, 0f, 0f)) {
        isEqualizerEnabled = enabled
        if (bands.isNotEmpty()) {
            eqBandLevels = bands.toMutableList()
        }
        
        try {
            equalizer?.apply {
                this.enabled = enabled
                if (enabled && bands.isNotEmpty()) {
                    val range = bandLevelRange // [min, max] in millibels
                    val maxLevel = range[1] // e.g., 1500 (15dB)
                    
                    val numBands = numberOfBands.toInt()
                    for (i in 0 until numBands) {
                        if (i < bands.size) {
                            // Map -15..15 to min..max millibels properly
                            // If bands[i] is between -15 and 15, we divide by 15.0 and multiply by maxLevel
                            val normalized = (bands[i] / 15.0f).coerceIn(-1.0f, 1.0f)
                            val bandLevel = (normalized * maxLevel).toInt().toShort()
                            setBandLevel(i.toShort(), bandLevel)
                        }
                    }
                    Log.d(TAG, "🎛️ Equalizer applied (mapped to hardware): $bands")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error applying equalizer", e)
        }
    }

    /** Set bass boost level (0-100) and Loudness (10x boost) */
    /** Set bass boost level (0-100) */
    fun setBassBoost(level: Int) {
        bassBoostLevel = level
        try {
            bassBoost?.apply {
                enabled = level > 0
                if (enabled) {
                    // Maximum 1000 strength
                    val strength = (level * 10).toShort().coerceIn(0, 1000)
                    setStrength(strength)
                }
            }
            Log.d(TAG, "🔊 Bass Boost: $level%")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting bass boost", e)
        }
    }

    /** Set loudness enhancement level (0-100) */
    fun setLoudness(level: Int) {
        loudnessLevel = level
        try {
            loudnessEnhancer?.apply {
                enabled = level > 0
                if (enabled) {
                    // Mapping 0-100 to 0-4000mB (40dB boost)
                    val gainmB = (level * 40)
                    setTargetGain(gainmB)
                }
            }
            Log.d(TAG, "🔊 Loudness: $level%")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting loudness boost", e)
        }
    }

    /** Set virtualizer level (0-100) */
    fun setVirtualizer(level: Int) {
        virtualizerLevel = level
        try {
            virtualizer?.apply {
                enabled = level > 0
                if (enabled) {
                    val strength = (level * 10).toShort().coerceIn(0, 1000)
                    setStrength(strength)
                    Log.d(TAG, "🎧 Virtualizer: $level%")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting virtualizer", e)
        }
    }

    /** Set reverb preset */
    fun setReverb(preset: String) {
        reverbPresetName = preset
        try {
            presetReverb?.apply {
                val reverbPreset =
                        when (preset) {
                            "Small Room" -> PresetReverb.PRESET_SMALLROOM
                            "Medium Room" -> PresetReverb.PRESET_MEDIUMROOM
                            "Large Room" -> PresetReverb.PRESET_LARGEROOM
                            "Hall" -> PresetReverb.PRESET_LARGEHALL
                            else -> PresetReverb.PRESET_NONE
                        }

                enabled = preset != "None"
                if (enabled) {
                    this.preset = reverbPreset.toShort()
                    Log.d(TAG, "🎼 Reverb: $preset")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting reverb", e)
        }
    }

    /** Set callback for song completion */
    fun setOnSongCompleteListener(callback: () -> Unit) {
        onSongComplete = callback
    }

    /** Set callback for errors */
    fun setOnErrorListener(callback: (String) -> Unit) {
        onError = callback
    }

    /** Start position updater coroutine */
    private fun startPositionUpdater() {
        positionUpdateJob?.cancel()
        positionUpdateJob =
                scope.launch {
                    var lastSaveTime = 0L
                    while (isActive) {
                        if (_isPlaying.value && player.duration > 0) {
                            val duration = player.duration
                            val position = player.currentPosition
                            _currentPositionMs.value = position
                            _currentPosition.value = (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                            
                            // Save state periodically
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastSaveTime > SAVE_STATE_INTERVAL) {
                                savePlaybackState()
                                lastSaveTime = currentTime
                            }
                        }
                        delay(POSITION_UPDATE_INTERVAL)
                    }
                }
    }

    private fun savePlaybackState() {
        if (player.currentMediaItem != null) {
            val mediaId = player.currentMediaItem?.mediaId
            val position = player.currentPosition
            val isPlaying = player.isPlaying
            if (mediaId != null) {
                prefs.edit()
                    .putString(KEY_MEDIA_ID, mediaId)
                    .putLong(KEY_POSITION, position)
                    .putBoolean(KEY_IS_PLAYING, isPlaying)
                    .apply()
            }
        }
    }
    
    fun getLastPlayedMediaId(): String? = prefs.getString(KEY_MEDIA_ID, null)
    
    fun getLastPlayedPosition(): Long = prefs.getLong(KEY_POSITION, 0L)
    
    fun wasPlayingLast(): Boolean = prefs.getBoolean(KEY_IS_PLAYING, false)

    /** Get the media ID of the song currently loaded in the player */
    fun getCurrentMediaId(): String? = player.currentMediaItem?.mediaId

    /** Update current position (manual call if needed) */
    fun updatePosition() {
        try {
            if (player.duration > 0) {
                val position = player.currentPosition.toFloat() / player.duration.toFloat()
                _currentPosition.value = position.coerceIn(0f, 1f)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating position", e)
        }
    }

    /** Get current authentication */
    fun getCurrentPosition(): Long =
            try {
                player.currentPosition
            } catch (e: Exception) {
                0L
            }

    /** Get duration in milliseconds */
    fun getDuration(): Long =
            try {
                player.duration
            } catch (e: Exception) {
                0L
            }

    /** Check if player is ready */
    fun isReady(): Boolean = player.playbackState == Player.STATE_READY

    /** Get audio session ID for effects */
    fun getAudioSessionId(): Int = player.audioSessionId

    /** Release all resources */
    fun release() {
        try {
            Log.d(TAG, "🔌 Releasing audio player resources")

            // Cancel position updater
            positionUpdateJob?.cancel()

            // Release audio effects
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
            presetReverb?.release()
            loudnessEnhancer?.release()

            // Release player
            player.release()

            Log.d(TAG, "✅ Resources released successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing resources", e)
        }
    }

    /** Get equalizer band frequencies */
    fun getEqualizerBandFrequencies(): List<Int> {
        return try {
            equalizer?.let { eq ->
                (0 until eq.numberOfBands.toInt()).map { i ->
                    eq.getCenterFreq(i.toShort()) / 1000 // Convert to Hz
                }
            }
                    ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting equalizer frequencies", e)
            emptyList()
        }
    }

    /** Get equalizer band level range */
    fun getEqualizerBandLevelRange(): Pair<Short, Short> {
        return try {
            equalizer?.let { Pair(it.bandLevelRange[0], it.bandLevelRange[1]) } ?: Pair(0, 0)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting equalizer range", e)
            Pair(0, 0)
        }
    }
}
