package com.example.flymusicai.player

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.flymusicai.data.Music
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Manager class for audio playback using ExoPlayer
 */
class AudioPlayerManager(context: Context) {
    
    companion object {
        private const val TAG = "AudioPlayerManager"
    }
    
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    
    private val _currentPosition = MutableStateFlow(0f)
    val currentPosition: StateFlow<Float> = _currentPosition
    
    private val _currentSongDuration = MutableStateFlow(0)
    val currentSongDuration: StateFlow<Int> = _currentSongDuration
    
    private var onSongComplete: (() -> Unit)? = null
    
    init {
        // Setup player listener
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_IDLE -> {
                        Log.d(TAG, "Playback state: IDLE")
                    }
                    Player.STATE_BUFFERING -> {
                        Log.d(TAG, "Playback state: BUFFERING")
                    }
                    Player.STATE_READY -> {
                        Log.d(TAG, "Playback state: READY")
                        _currentSongDuration.value = (player.duration / 1000).toInt()
                    }
                    Player.STATE_ENDED -> {
                        Log.d(TAG, "Playback state: ENDED")
                        _isPlaying.value = false
                        onSongComplete?.invoke()
                    }
                }
            }
            
            override fun onIsPlayingChanged(playing: Boolean) {
                Log.d(TAG, "Playing changed: $playing")
                _isPlaying.value = playing
            }
            
            override fun onPlayerError(error: PlaybackException) {
                Log.e(TAG, "Playback error: ${error.message}", error)
                _isPlaying.value = false
            }
        })
        
        // Update position periodically
        setupPositionUpdater()
    }
    
    /**
     * Play a song from URL
     */
    fun playSong(song: Music) {
        try {
            Log.d(TAG, "Playing song: ${song.title} by ${song.artist}")
            Log.d(TAG, "Audio URL: ${song.audioUrl}")
            
            if (song.audioUrl.isEmpty()) {
                Log.e(TAG, "Audio URL is empty for song: ${song.title}")
                return
            }
            
            // Stop current playback
            player.stop()
            
            // Set new media item
            val mediaItem = MediaItem.fromUri(song.audioUrl)
            player.setMediaItem(mediaItem)
            
            // Prepare and play
            player.prepare()
            player.playWhenReady = true
            
            _currentSongDuration.value = song.duration
            
            Log.d(TAG, "Song prepared and starting playback")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing song: ${song.title}", e)
            e.printStackTrace()
            _isPlaying.value = false
        }
    }
    
    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
        } else {
            player.play()
            _isPlaying.value = true
        }
    }
    
    /**
     * Pause playback
     */
    fun pause() {
        player.pause()
        _isPlaying.value = false
    }
    
    /**
     * Resume playback
     */
    fun play() {
        player.play()
        _isPlaying.value = true
    }
    
    /**
     * Stop playback
     */
    fun stop() {
        player.stop()
        _isPlaying.value = false
    }
    
    /**
     * Seek to position (0-1)
     */
    fun seekTo(position: Float) {
        val seekPosition = (position * player.duration).toLong()
        player.seekTo(seekPosition.coerceAtLeast(0))
    }
    
    /**
     * Set callback for song completion
     */
    fun setOnSongCompleteListener(callback: () -> Unit) {
        onSongComplete = callback
    }
    
    /**
     * Release resources
     */
    fun release() {
        player.release()
    }
    
    /**
     * Setup position updater
     */
    private fun setupPositionUpdater() {
        // This will be called periodically by the view model
    }
    
    /**
     * Update current position
     */
    fun updatePosition() {
        if (player.duration > 0) {
            val position = player.currentPosition.toFloat() / player.duration.toFloat()
            _currentPosition.value = position.coerceIn(0f, 1f)
        }
    }
    
    /**
     * Get current playback position in milliseconds
     */
    fun getCurrentPosition(): Long = player.currentPosition
    
    /**
     * Get duration in milliseconds
     */
    fun getDuration(): Long = player.duration
}
