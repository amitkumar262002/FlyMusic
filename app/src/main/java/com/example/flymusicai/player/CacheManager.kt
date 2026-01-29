package com.example.flymusicai.player

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

/**
 * 💾 Cache Manager for Media3/ExoPlayer
 * Handles chunked downloading and persistence of audio streams
 */
@UnstableApi
object CacheManager {
    private var simpleCache: SimpleCache? = null
    private const val CACHE_SIZE = 500 * 1024 * 1024L // 500MB

    @Synchronized
    fun getCache(context: Context): SimpleCache {
        if (simpleCache == null) {
            val cacheDir = File(context.cacheDir, "media_cache")
            val evictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE)
            val databaseProvider = StandaloneDatabaseProvider(context)
            simpleCache = SimpleCache(cacheDir, evictor, databaseProvider)
        }
        return simpleCache!!
    }

    fun getCacheDataSourceFactory(context: Context): DataSource.Factory {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")

        val upstreamFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        return CacheDataSource.Factory()
            .setCache(getCache(context))
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    /**
     * 🚀 Prefetch initial data of a song for instant playback
     * Downloads the first [bytes] of the stream into the persistent cache.
     */
    fun prefetchSongData(context: Context, url: String, bytes: Long = 512 * 1024) {
        if (url.isEmpty() || url.contains("placeholder")) return
        
        val cache = getCache(context)
        val dataSource = getCacheDataSourceFactory(context).createDataSource()
        val dataSpec = androidx.media3.datasource.DataSpec.Builder()
            .setUri(url)
            .setLength(bytes)
            .build()

        // Run in background thread
        java.util.concurrent.Executors.newSingleThreadExecutor().execute {
            try {
                android.util.Log.d("CacheManager", "📥 Prefetching starting for: $url")
                androidx.media3.datasource.cache.CacheWriter(
                    dataSource as CacheDataSource,
                    dataSpec,
                    null,
                    null
                ).cache()
                android.util.Log.d("CacheManager", "✅ Prefetching complete (${bytes/1024} KB)")
            } catch (e: Exception) {
                android.util.Log.w("CacheManager", "⚠️ Prefetching failed: ${e.message}")
            }
        }
    }
}
