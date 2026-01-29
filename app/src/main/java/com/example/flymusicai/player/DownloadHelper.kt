package com.example.flymusicai.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadRequest
import androidx.media3.exoplayer.scheduler.Requirements
import com.example.flymusicai.data.Music
import com.example.flymusicai.service.MusicDownloadService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

/**
 * 📥 Professional Download Helper for FlyMusicAI
 * Adapted from Kreate/RiMusic to provide real Media3 downloading.
 */
@UnstableApi
object DownloadHelper {

    private const val DOWNLOAD_CHANNEL_ID = "download_channel"
    private const val NUM_PARALLEL_DOWNLOADS = 3

    private var downloadManager: DownloadManager? = null
    private var downloadNotificationHelper: DownloadNotificationHelper? = null
    
    val downloads = MutableStateFlow<Map<String, Download>>(emptyMap())
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val executor = Executors.newSingleThreadExecutor()

    fun init(context: Context) {
        if (downloadManager == null) {
            val databaseProvider = StandaloneDatabaseProvider(context)
            val downloadCache = CacheManager.getCache(context)
            val dataSourceFactory = DefaultHttpDataSource.Factory()

            downloadManager = DownloadManager(
                context,
                databaseProvider,
                downloadCache,
                dataSourceFactory,
                executor
            ).apply {
                maxParallelDownloads = NUM_PARALLEL_DOWNLOADS
                requirements = Requirements(Requirements.NETWORK)
                addListener(object : DownloadManager.Listener {
                    override fun onDownloadChanged(manager: DownloadManager, download: Download, finalException: Exception?) {
                        updateDownloadState(download)
                    }

                    override fun onDownloadRemoved(manager: DownloadManager, download: Download) {
                        removeDownloadFromState(download.request.id)
                    }
                })
            }

            // Initial load of downloads
            scope.launch {
                val results = mutableMapOf<String, Download>()
                val cursor = downloadManager!!.downloadIndex.getDownloads()
                while (cursor.moveToNext()) {
                    results[cursor.download.request.id] = cursor.download
                }
                downloads.value = results
            }
        }
    }

    private fun updateDownloadState(download: Download) {
        downloads.update { it.toMutableMap().apply { put(download.request.id, download) } }
    }

    private fun removeDownloadFromState(id: String) {
        downloads.update { it.toMutableMap().apply { remove(id) } }
    }

    fun getDownloadManager(): DownloadManager = downloadManager!!

    fun getNotificationHelper(context: Context): DownloadNotificationHelper {
        if (downloadNotificationHelper == null) {
            downloadNotificationHelper = DownloadNotificationHelper(context, DOWNLOAD_CHANNEL_ID)
        }
        return downloadNotificationHelper!!
    }

    fun startDownload(context: Context, music: Music) {
        // Validate that we have a proper stream URL
        if (music.audioUrl.isEmpty() || 
            music.audioUrl.contains("placeholder") ||
            music.audioUrl.contains("example.com")) {
            android.util.Log.e("DownloadHelper", "❌ Cannot download: Invalid or expired URL for ${music.title}")
            android.widget.Toast.makeText(
                context,
                "Download failed: URL expired. Please try again.",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            val mediaItem = music.toMediaItem()
            val downloadRequest = DownloadRequest.Builder(
                mediaItem.mediaId,
                mediaItem.requestMetadata.mediaUri ?: Uri.parse(music.audioUrl)
            )
                .setCustomCacheKey(mediaItem.mediaId)
                .setData("${music.artist} - ${music.title}".encodeToByteArray())
                .build()

            androidx.media3.exoplayer.offline.DownloadService.sendAddDownload(
                context,
                MusicDownloadService::class.java,
                downloadRequest,
                /* foreground = */ false
            )
            
            android.util.Log.d("DownloadHelper", "✅ Download request sent for: ${music.title}")
        } catch (e: Exception) {
            android.util.Log.e("DownloadHelper", "❌ Failed to start download for ${music.title}", e)
            android.widget.Toast.makeText(
                context,
                "Download failed: ${e.message}",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun removeDownload(context: Context, songId: String) {
        androidx.media3.exoplayer.offline.DownloadService.sendRemoveDownload(
            context,
            MusicDownloadService::class.java,
            songId,
            /* foreground = */ false
        )
    }

    private fun Music.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id)
            .setUri(audioUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setArtworkUri(Uri.parse(coverImageUrl))
                    .build()
            )
            .setRequestMetadata(
                MediaItem.RequestMetadata.Builder()
                    .setMediaUri(Uri.parse(audioUrl))
                    .build()
            )
            .build()
    }
}
