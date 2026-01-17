package com.example.flymusicai.service

import android.content.Context
import com.example.flymusicai.data.Music
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Download Manager for offline music playback
 * Handles downloading and caching songs
 */
class DownloadManager(private val context: Context) {
    
    private val _downloadedSongs = MutableStateFlow<Set<String>>(emptySet())
    val downloadedSongs: StateFlow<Set<String>> = _downloadedSongs.asStateFlow()
    
    private val _downloadProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Int>> = _downloadProgress.asStateFlow()
    
    private val downloadDir: File by lazy {
        File(context.filesDir, "downloaded_music").apply {
            if (!exists()) mkdirs()
        }
    }
    
    /**
     * Start downloading a song
     */
    suspend fun downloadSong(music: Music): Boolean {
        try {
            // Update progress
            updateProgress(music.id, 0)
            
            // Simulate download progress (in real app, download from audioUrl)
            for (progress in 0..100 step 10) {
                updateProgress(music.id, progress)
                kotlinx.coroutines.delay(200) // Simulate download time
            }
            
            // Mark as downloaded
            val downloadedSet = _downloadedSongs.value.toMutableSet()
            downloadedSet.add(music.id)
            _downloadedSongs.value = downloadedSet
            
            // Save to local storage info
            saveDownloadInfo(music.id)
            
            updateProgress(music.id, 100)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
    
    /**
     * Delete downloaded song
     */
    fun deleteSong(songId: String) {
        val downloadedSet = _downloadedSongs.value.toMutableSet()
        downloadedSet.remove(songId)
        _downloadedSongs.value = downloadedSet
        
        // Delete file
        val file = File(downloadDir, "$songId.mp3")
        if (file.exists()) {
            file.delete()
        }
        
        // Remove from preferences
        removeDownloadInfo(songId)
    }
    
    /**
     * Check if song is downloaded
     */
    fun isDownloaded(songId: String): Boolean {
        return _downloadedSongs.value.contains(songId)
    }
    
    /**
     * Get local file path for downloaded song
     */
    fun getLocalFilePath(songId: String): String? {
        if (!isDownloaded(songId)) return null
        val file = File(downloadDir, "$songId.mp3")
        return if (file.exists()) file.absolutePath else null
    }
    
    /**
     * Get all downloaded songs
     */
    fun getAllDownloadedSongs(): Set<String> {
        return _downloadedSongs.value
    }
    
    /**
     * Clear all downloads
     */
    fun clearAllDownloads() {
        downloadDir.listFiles()?.forEach { it.delete() }
        _downloadedSongs.value = emptySet()
        clearDownloadInfo()
    }
    
    private fun updateProgress(songId: String, progress: Int) {
        val progressMap = _downloadProgress.value.toMutableMap()
        progressMap[songId] = progress
        _downloadProgress.value = progressMap
    }
    
    private fun saveDownloadInfo(songId: String) {
        val prefs = context.getSharedPreferences("downloads", Context.MODE_PRIVATE)
        val downloaded = prefs.getStringSet("downloaded_songs", mutableSetOf()) ?: mutableSetOf()
        downloaded.add(songId)
        prefs.edit().putStringSet("downloaded_songs", downloaded).apply()
    }
    
    private fun removeDownloadInfo(songId: String) {
        val prefs = context.getSharedPreferences("downloads", Context.MODE_PRIVATE)
        val downloaded = prefs.getStringSet("downloaded_songs", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        downloaded.remove(songId)
        prefs.edit().putStringSet("downloaded_songs", downloaded).apply()
    }
    
    private fun clearDownloadInfo() {
        val prefs = context.getSharedPreferences("downloads", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
    
    /**
     * Load downloaded songs from storage on init
     */
    init {
        val prefs = context.getSharedPreferences("downloads", Context.MODE_PRIVATE)
        val downloaded = prefs.getStringSet("downloaded_songs", emptySet()) ?: emptySet()
        _downloadedSongs.value = downloaded
    }
}
