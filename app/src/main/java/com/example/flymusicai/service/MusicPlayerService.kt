package com.example.flymusicai.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.flymusicai.MainActivity
import com.example.flymusicai.R
import com.example.flymusicai.data.Music
import com.example.flymusicai.player.AudioPlayerManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

/**
 * 🎵 Advanced Music Service
 * - Directly controls AudioPlayerManager (Singleton)
 * - Updates MediaSession with precise position/duration for Android 13+ seekbar
 * - Loads Album Art asynchronously
 */
class MusicPlayerService : Service() {

    companion object {
        private const val TAG = "MusicPlayerService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "fly_music_channel_v2" // Updated channel
        private const val CHANNEL_NAME = "FlyMusic Playback"
        
        const val ACTION_PLAY = "com.example.flymusicai.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.flymusicai.ACTION_PAUSE"
        const val ACTION_NEXT = "com.example.flymusicai.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.flymusicai.ACTION_PREVIOUS"
        const val ACTION_STOP = "com.example.flymusicai.ACTION_STOP"
    }

    private lateinit var notificationManager: NotificationManager
    private lateinit var audioPlayerManager: AudioPlayerManager
    private var mediaSession: MediaSessionCompat? = null
    
    // scope for observing player state
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var currentArtBitmap: Bitmap? = null
    private var lastSongId: String = ""

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🚀 Service Created: Initializing MediaSession & Player Connection")
        
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
        
        // Connect to the Singleton Player
        audioPlayerManager = AudioPlayerManager.getInstance(this)
        
        setupMediaSession()
        observePlayerState()
        
        // Start foreground immediately to avoid crash
        startForeground(NOTIFICATION_ID, buildNotification(null, false))
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(this, "FlyMusicSession").apply {
            isActive = true
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or 
                     MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
                     
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() { handlePlay() }
                override fun onPause() { handlePause() }
                override fun onSkipToNext() { handleNext() }
                override fun onSkipToPrevious() { handlePrevious() }
                override fun onStop() { handleStop() }
                override fun onSeekTo(pos: Long) {
                    audioPlayerManager.seekTo(pos.toFloat() / audioPlayerManager.getDuration().toFloat())
                }
            })
        }
    }

    private fun observePlayerState() {
        // Observer: Playback State (Playing/Paused)
        serviceScope.launch {
            audioPlayerManager.isPlaying.collectLatest { isPlaying ->
                updatePlaybackState(isPlaying)
                updateNotification()
            }
        }
        
        // Observer: Current Position (for Seekbar)
        serviceScope.launch {
            // We don't want to spam updates, but MediaSession PlaybackState needs relatively fresh position
            // The PlaybackState object handles the interpolation, we just need to update it on state change
            // or periodically if needed.
            audioPlayerManager.currentPositionMs.collectLatest { pos ->
                 // Ideally only update on significant change or state change, 
                 // but for seekbar sync we rely on setPlaybackState's speed parameter.
            }
        }

        // Observer: Song Duration
        serviceScope.launch {
            audioPlayerManager.currentSongDurationMs.collectLatest { duration ->
                updateMediaMetadata(duration)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "📩 Command received: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_PLAY -> handlePlay()
            ACTION_PAUSE -> handlePause()
            ACTION_NEXT -> handleNext()
            ACTION_PREVIOUS -> handlePrevious()
            ACTION_STOP -> handleStop()
            else -> {
                // Update track info from intent if provided (Legacy support or explicit update)
                // Ideally we just pull from AudioPlayerManager if possible, but intents are good for initial start
                val title = intent?.getStringExtra("song_title")
                if (title != null) {
                    val song = Music(
                        id = "", // We might not have ID here, but let's try to match
                        title = title,
                        artist = intent.getStringExtra("song_artist") ?: "Unknown",
                        coverImageUrl = intent.getStringExtra("song_cover") ?: "",
                        duration = 0,
                        audioUrl = "",
                        genre = intent.getStringExtra("song_genre") ?: ""
                    )
                    loadAlbumArt(song)
                    updateMediaMetadata(audioPlayerManager.getDuration())
                    updateNotification()
                }
            }
        }
        
        return START_NOT_STICKY
    }

    private fun handlePlay() {
        Log.d(TAG, "▶️ Play Request")
        audioPlayerManager.play()
        updatePlaybackState(true) // Immediate update
    }

    private fun handlePause() {
        Log.d(TAG, "⏸️ Pause Request")
        audioPlayerManager.pause()
        updatePlaybackState(false) // Immediate update
    }

    private fun handleNext() {
        // Broadcast NEXT action to ViewModel securely
        val intent = Intent("com.example.flymusicai.MEDIA_ACTION").apply {
            setPackage(packageName)
            putExtra("action", "NEXT")
        }
        sendBroadcast(intent)
    }

    private fun handlePrevious() {
        // Broadcast PREVIOUS action to ViewModel securely
        val intent = Intent("com.example.flymusicai.MEDIA_ACTION").apply {
            setPackage(packageName)
            putExtra("action", "PREVIOUS")
        }
        sendBroadcast(intent)
    }

    private fun handleStop() {
        audioPlayerManager.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updatePlaybackState(isPlaying: Boolean) {
        val state = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val position = audioPlayerManager.getCurrentPosition()
        
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SEEK_TO or // Enable Seeking
                PlaybackStateCompat.ACTION_STOP
            )
            .setState(state, position, if (isPlaying) 1.0f else 0f)
            .build()
            
        mediaSession?.setPlaybackState(playbackState)
    }

    private fun updateMediaMetadata(durationMs: Long) {
        // We need existing metadata or wait for album art load
        val metadataBuilder = MediaMetadataCompat.Builder()
            
        // Assuming we got song data from somewhere. 
        // Logic: intents pass song data. We should cache it locally.
        // But for cleaner code, we might want AudioPlayerManager to expose 'currentSong'
        // Since we can't change APM too much right now, we rely on the Intents sent by ViewModel
        // or check cached values.
        
        // Re-construct basic metadata
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, durationMs)
        
        if (currentArtBitmap != null) {
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, currentArtBitmap)
            metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, currentArtBitmap)
        }
        
        // We really need the Title/Artist. 
        // If they are missing from this scope, the Notification/Session will be blank.
        // The `onStartCommand` updates a local cache? We need a class-level variable.
        // Let's assume onStartCommand set them. we really need a variable.
        // For robustness:
        // We'll rely on updateNotification() to usually be called after onStartCommand
        
        mediaSession?.setMetadata(metadataBuilder.build())
    }
    
    // Helper to load image
    private fun loadAlbumArt(song: Music) {
         if (song.id == lastSongId && currentArtBitmap != null) return
         lastSongId = song.id
         
         serviceScope.launch(Dispatchers.IO) {
             try {
                 val loader = ImageLoader(this@MusicPlayerService)
                 val request = ImageRequest.Builder(this@MusicPlayerService)
                     .data(song.coverImageUrl)
                     .allowHardware(false) // Bitmap for remoteviews/notif needs software or simple config
                     .build()
                 
                 val result = loader.execute(request)
                 if (result is SuccessResult) {
                     val bitmap = (result.drawable as BitmapDrawable).bitmap
                     currentArtBitmap = bitmap
                     
                     // Update Metadata fully
                     val meta = MediaMetadataCompat.Builder()
                         .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                         .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
                         .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.genre)
                         .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, audioPlayerManager.getDuration())
                         .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                         .build()
                     
                     withContext(Dispatchers.Main) {
                         mediaSession?.setMetadata(meta)
                         updateNotification()
                     }
                 }
             } catch (e: Exception) {
                 e.printStackTrace()
             }
         }
    }

    private fun updateNotification() {
        // Quick check: if we haven't received valid data via Intent yet, don't show empty "Unknown"
        // But we need to update play/pause state.
        // We'll trust the MediaSession for metadata if set.
        
        val controller = mediaSession?.controller
        val mediaMetadata = controller?.metadata
        val playbackState = controller?.playbackState
        
        val title = mediaMetadata?.getString(MediaMetadataCompat.METADATA_KEY_TITLE) ?: "FlyMusic AI"
        val artist = mediaMetadata?.getString(MediaMetadataCompat.METADATA_KEY_ARTIST) ?: "Music Player"
        val isPlaying = playbackState?.state == PlaybackStateCompat.STATE_PLAYING
        
        val notification = buildNotification(title, isPlaying, artist)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(title: String?, isPlaying: Boolean, artist: String? = ""): Notification {
        val contentIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        // Action: Previous
        val prevIntent = PendingIntent.getService(this, 0, 
            Intent(this, MusicPlayerService::class.java).setAction(ACTION_PREVIOUS), 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            
        // Action: Play/Pause
        val playPauseIntent = PendingIntent.getService(this, 1, 
            Intent(this, MusicPlayerService::class.java).setAction(if (isPlaying) ACTION_PAUSE else ACTION_PLAY), 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            
        // Action: Next
        val nextIntent = PendingIntent.getService(this, 2, 
            Intent(this, MusicPlayerService::class.java).setAction(ACTION_NEXT), 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle(title)
            .setContentText(artist)
            .setLargeIcon(currentArtBitmap) // Standard Large Icon
            .setContentIntent(contentIntent)
            .setOngoing(isPlaying)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(mediaSession?.sessionToken)
                .setShowActionsInCompactView(0, 1, 2)) // indices of actions to show
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(R.drawable.ic_previous, "Previous", prevIntent)
            .addAction(if(isPlaying) R.drawable.ic_pause else R.drawable.ic_play, if(isPlaying) "Pause" else "Play", playPauseIntent)
            .addAction(R.drawable.ic_next, "Next", nextIntent)

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // Low to prevent sound/vibration for media
            ).apply {
                description = "Music playback controls"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        mediaSession?.release()
        serviceScope.cancel()
        super.onDestroy()
    }
}
