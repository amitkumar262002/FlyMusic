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
 */
class AudioPlayerManager(private val context: Context) {

    companion object {
        private const val TAG = "AudioPlayerManager"
        private const val POSITION_UPDATE_INTERVAL = 100L // Update every 100ms
        private const val FADE_DURATION = 300L // Crossfade duration in ms
    }

    // ExoPlayer instance
    @androidx.media3.common.util.UnstableApi
    private val player: ExoPlayer =
            ExoPlayer.Builder(context)
                    .setLoadControl(
                        androidx.media3.exoplayer.DefaultLoadControl.Builder()
                            .setBufferDurationsMs(
                                1000, // MIN_BUFFER_MS: starts playback after 1s buffered (Faster!)
                                20000, // MAX_BUFFER_MS
                                500,  // BUFFER_FOR_PLAYBACK_MS
                                1000  // BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
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

    init {
        setupPlayer()
        setupAudioEffects()
        startPositionUpdater()
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

                        val errorMessage =
                                when (error.errorCode) {
                                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ->
                                            "Network connection failed"
                                    PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
                                            "Audio file not found"
                                    PlaybackException.ERROR_CODE_DECODER_INIT_FAILED ->
                                            "Audio decoder initialization failed"
                                    PlaybackException.ERROR_CODE_TIMEOUT -> "Connection timeout"
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

        Log.d(TAG, "Setting up audio effects for session: $sessionId")

        // Release old effects if any
        releaseEffectsOnly()

        // Initialize Equalizer
        try {
            equalizer = Equalizer(0, sessionId).apply {
                enabled = isEqualizerEnabled
            }
            Log.d(TAG, "Equalizer initialized with ${equalizer?.numberOfBands} bands")
        } catch (e: Exception) {
            Log.w(TAG, "Equalizer not supported on this device")
            equalizer = null
        }

        // Initialize Bass Boost
        try {
            bassBoost = BassBoost(0, sessionId).apply { enabled = false }
            Log.d(TAG, "Bass Boost initialized")
        } catch (e: Exception) {
            Log.w(TAG, "Bass Boost not supported on this device")
            bassBoost = null
        }

        // Initialize Loudness Enhancer
        try {
            loudnessEnhancer =
                    android.media.audiofx.LoudnessEnhancer(sessionId).apply { enabled = false }
            Log.d(TAG, "Loudness Enhancer initialized")
        } catch (e: Exception) {
            Log.w(TAG, "Loudness Enhancer not supported on this device")
            loudnessEnhancer = null
        }

        // Initialize Virtualizer
        try {
            virtualizer = Virtualizer(0, sessionId).apply { enabled = false }
            Log.d(TAG, "Virtualizer initialized")
        } catch (e: Exception) {
            Log.w(TAG, "Virtualizer not supported on this device")
            virtualizer = null
        }

        // Initialize Preset Reverb
        try {
            presetReverb = PresetReverb(0, sessionId).apply { enabled = false }
            Log.d(TAG, "Preset Reverb initialized")
        } catch (e: Exception) {
            Log.w(TAG, "Preset Reverb not supported on this device")
            presetReverb = null
        }

        // Re-apply stored settings to new effects
        applyCurrentSettings()
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

    /** Play a song from URL */
    fun playSong(song: Music) {
        try {
            Log.d(TAG, "▶️ Playing: ${song.title} by ${song.artist}")
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
                // Force DASH if it looks like a YouTube stream but doesn't have extension
                // Actually ExoPlayer handles DASH better if we don't force it unless it's a .mpd
                // But we can set a custom metadata
                mediaItemBuilder.setMimeType(androidx.media3.common.MimeTypes.AUDIO_UNKNOWN)
            }

            val mediaItem = mediaItemBuilder.build()

            player.setMediaItem(mediaItem)

            // Prepare and play
            player.prepare()
            player.playWhenReady = true

            // Update state immediately
            _isPlaying.value = true

            // Re-sync audio session ID and effects for the new track
            val newSessionId = player.audioSessionId
            if (newSessionId != _audioSessionId.value) {
                _audioSessionId.value = newSessionId
                setupAudioEffects()
            }

            Log.d(TAG, "✅ Song prepared and starting playback")
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

    /** Pause playback */
    fun pause() {
        try {
            player.pause()
            _isPlaying.value = false
            Log.d(TAG, "⏸️ Paused")
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing", e)
        }
    }

    /** Resume playback */
    fun play() {
        try {
            player.play()
            _isPlaying.value = true
            Log.d(TAG, "▶️ Playing")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing", e)
        }
    }

    /** Stop playback */
    fun stop() {
        try {
            player.stop()
            _isPlaying.value = false
            _currentPosition.value = 0f
            Log.d(TAG, "⏹️ Stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping", e)
        }
    }

    /** Seek to position (0-1) */
    fun seekTo(position: Float) {
        try {
            val seekPosition = (position * player.duration).toLong().coerceAtLeast(0)
            player.seekTo(seekPosition)
            _currentPosition.value = position.coerceIn(0f, 1f)
            Log.d(TAG, "⏩ Seeked to: ${(position * 100).toInt()}%")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking", e)
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
                    while (isActive) {
                        if (_isPlaying.value && player.duration > 0) {
                            val duration = player.duration
                            val position = player.currentPosition
                            _currentPositionMs.value = position
                            _currentPosition.value = (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                        }
                        delay(POSITION_UPDATE_INTERVAL)
                    }
                }
    }

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
