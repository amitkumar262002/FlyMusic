package com.example.flymusicai.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.flymusicai.MainActivity
import com.example.flymusicai.R
import com.example.flymusicai.data.Music

/**
 * 🎵 Music Player Service with Notification Controls
 * Shows play/pause/next/previous buttons in notification
 */
class MusicPlayerService : Service() {

    companion object {
        private const val TAG = "MusicPlayerService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "music_playback_channel"
        private const val CHANNEL_NAME = "Music Playback"
        
        const val ACTION_PLAY = "com.example.flymusicai.ACTION_PLAY"
        const val ACTION_PAUSE = "com.example.flymusicai.ACTION_PAUSE"
        const val ACTION_NEXT = "com.example.flymusicai.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.example.flymusicai.ACTION_PREVIOUS"
        const val ACTION_STOP = "com.example.flymusicai.ACTION_STOP"
    }

    private lateinit var notificationManager: NotificationManager
    private var currentSong: Music? = null
    private var isPlaying = false

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🎵 Service created")
        createNotificationChannel()
        notificationManager = getSystemService(NotificationManager::class.java)
        
        // Start with empty notification immediately
        startForeground(NOTIFICATION_ID, createEmptyNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "📢 onStartCommand - Action: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_PLAY -> handlePlay()
            ACTION_PAUSE -> handlePause()
            ACTION_NEXT -> handleNext()
            ACTION_PREVIOUS -> handlePrevious()
            ACTION_STOP -> handleStop()
            else -> {
                // Update notification with song data
                intent?.let {
                    val title = it.getStringExtra("song_title") ?: "Unknown"
                    val artist = it.getStringExtra("song_artist") ?: "Unknown"
                    val genre = it.getStringExtra("song_genre") ?: ""
                    val cover = it.getStringExtra("song_cover") ?: ""
                    isPlaying = it.getBooleanExtra("is_playing", false)
                    
                    Log.d(TAG, "🎵 Updating notification: $title by $artist - Playing: $isPlaying")
                    
                    currentSong = Music(
                        id = "", 
                        title = title, 
                        artist = artist, 
                        duration = 0, 
                        audioUrl = "", 
                        coverImageUrl = cover, 
                        genre = genre,
                        year = 2024,
                        playCount = 0
                    )
                    updateNotification()
                }
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "🔌 Service destroyed")
    }

    private fun handlePlay() {
        Log.d(TAG, "▶️ Play")
        isPlaying = true
        updateNotification()
        broadcastAction("PLAY")
    }

    private fun handlePause() {
        Log.d(TAG, "⏸️ Pause")
        isPlaying = false
        updateNotification()
        broadcastAction("PAUSE")
    }

    private fun handleNext() {
        Log.d(TAG, "⏭️ Next")
        broadcastAction("NEXT")
    }

    private fun handlePrevious() {
        Log.d(TAG, "⏮️ Previous")
        broadcastAction("PREVIOUS")
    }

    private fun handleStop() {
        Log.d(TAG, "⏹️ Stop")
        isPlaying = false
        broadcastAction("STOP")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun broadcastAction(action: String) {
        val intent = Intent("com.example.flymusicai.MEDIA_ACTION").apply {
            putExtra("action", action)
        }
        sendBroadcast(intent)
    }

    private fun updateNotification() {
        val notification = createNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val song = currentSong ?: return createEmptyNotification()

        // Intent to open app
        val contentIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(
                this, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Create actions
        val previousAction = NotificationCompat.Action.Builder(
            R.drawable.ic_previous,
            "Previous",
            createPendingIntent(ACTION_PREVIOUS)
        ).build()

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action.Builder(
                R.drawable.ic_pause,
                "Pause",
                createPendingIntent(ACTION_PAUSE)
            ).build()
        } else {
            NotificationCompat.Action.Builder(
                R.drawable.ic_play,
                "Play",
                createPendingIntent(ACTION_PLAY)
            ).build()
        }

        val nextAction = NotificationCompat.Action.Builder(
            R.drawable.ic_next,
            "Next",
            createPendingIntent(ACTION_NEXT)
        ).build()

        // Build notification with MediaStyle
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSubText(song.genre)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(contentIntent)
            .setDeleteIntent(createPendingIntent(ACTION_STOP))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setOngoing(isPlaying)
            // Add actions
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            // MediaStyle - THIS IS THE KEY!
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2) // Show all 3 buttons
            )
            .build()
    }

    private fun createEmptyNotification(): Notification {
        val contentIntent = Intent(this, MainActivity::class.java).let {
            PendingIntent.getActivity(
                this, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FlyMusic AI")
            .setContentText("Ready to play music")
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(false)
            .build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MusicPlayerService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setSound(null, null) // No sound for updates
            }
            
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
            Log.d(TAG, "✅ Notification channel created")
        }
    }
}
