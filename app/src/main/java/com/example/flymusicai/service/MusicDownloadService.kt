package com.example.flymusicai.service

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import com.example.flymusicai.R
import com.example.flymusicai.player.DownloadHelper

private const val JOB_ID = 1001
private const val FOREGROUND_NOTIFICATION_ID = 1002

/**
 * 🛠️ Real Music Download Service
 * Handles background downloading of music using Media3 DownloadService.
 */
@UnstableApi
class MusicDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    "download_channel",
    R.string.app_name, // Fallback title
    0
) {

    override fun getDownloadManager(): DownloadManager {
        DownloadHelper.init(this)
        val manager = DownloadHelper.getDownloadManager()
        val notificationHelper = DownloadHelper.getNotificationHelper(this)
        
        manager.addListener(
            TerminalStateNotificationHelper(
                this,
                notificationHelper,
                FOREGROUND_NOTIFICATION_ID + 1
            )
        )
        return manager
    }

    override fun getScheduler(): PlatformScheduler? {
        return PlatformScheduler(this, JOB_ID)
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        return DownloadHelper.getNotificationHelper(this)
            .buildProgressNotification(
                this,
                R.drawable.music_placeholder, // Small icon
                null,
                "${downloads.size} downloads in progress",
                downloads,
                notMetRequirements
            )
    }

    /** Helper class for showing terminal state notifications */
    private class TerminalStateNotificationHelper(
        private val context: Context,
        private val notificationHelper: DownloadNotificationHelper,
        firstNotificationId: Int
    ) : DownloadManager.Listener {
        private var nextNotificationId = firstNotificationId

        override fun onDownloadChanged(manager: DownloadManager, download: Download, finalException: Exception?) {
            val notification: Notification = when (download.state) {
                Download.STATE_COMPLETED -> {
                    notificationHelper.buildDownloadCompletedNotification(
                        context,
                        R.drawable.music_placeholder,
                        null,
                        Util.fromUtf8Bytes(download.request.data)
                    )
                }
                Download.STATE_FAILED -> {
                    notificationHelper.buildDownloadFailedNotification(
                        context,
                        R.drawable.music_placeholder,
                        null,
                        Util.fromUtf8Bytes(download.request.data)
                    )
                }
                else -> return
            }
            NotificationUtil.setNotification(context, nextNotificationId++, notification)
        }
    }
}
