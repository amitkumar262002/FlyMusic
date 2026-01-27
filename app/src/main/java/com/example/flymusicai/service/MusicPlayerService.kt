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
import com.example.flymusicai.R
import com.example.flymusicai.data.Music

/**
 * 🎵 Simplified Music Player Service
 * Provides notification controls for music playback
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
        Log.d(TAG, "Service created")
        createNotificationChannel()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    private fun handlePlay() {
        isPlaying = true
        updateNotification()
        broadcastAction("PLAY")
    }

    private fun handlePause() {
        isPlaying = false
        updateNotification()
        broadcastAction("PAUSE")
    }

    private fun handleNext() {
        broadcastAction("NEXT")
    }

    private fun handlePrevious() {
        broadcastAction("PREVIOUS")
    }

    private fun handleStop() {
        isPlaying = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        broadcastAction("STOP")
    }

    private fun broadcastAction(action: String) {
        val intent = Intent("com.example.flymusicai.MEDIA_ACTION").apply {
            putExtra("action", action)
        }
        sendBroadcast(intent)
    }

    private fun updateNotification() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val song = currentSong ?: return createEmptyNotification()

        // Intent for opening the app
        val contentIntent = packageManager.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(
                this, 0, it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        // Action intents
        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_pause,
                "Pause",
                createPendingIntent(ACTION_PAUSE)
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play,
                "Play",
                createPendingIntent(ACTION_PLAY)
            )
        }

        val previousAction = NotificationCompat.Action(
            R.drawable.ic_previous,
            "Previous",
            createPendingIntent(ACTION_PREVIOUS)
        )

        val nextAction = NotificationCompat.Action(
            R.drawable.ic_next,
            "Next",
            createPendingIntent(ACTION_NEXT)
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSubText(song.genre)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(contentIntent)
            .setDeleteIntent(createPendingIntent(ACTION_STOP))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .build()
    }

    private fun createEmptyNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("FlyMusic AI")
            .setContentText("Ready to play")
            .setSmallIcon(R.drawable.ic_music_note)
            .setPriority(NotificationCompat.PRIORITY_LOW)
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
            }
            
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
