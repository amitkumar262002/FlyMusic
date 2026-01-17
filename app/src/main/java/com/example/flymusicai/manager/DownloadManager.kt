package com.example.flymusicai.manager

import android.content.Context
import com.example.flymusicai.data.Music

class DownloadManager(private val context: Context) {

    fun downloadSong(music: Music) {
        // TODO: Implement download logic
    }

    fun isDownloaded(songId: String): Boolean {
        // TODO: Implement isDownloaded check
        return false
    }

    fun deleteSong(songId: String) {
        // TODO: Implement delete logic
    }

    fun getLocalFilePath(songId: String): String? {
        // TODO: Implement getLocalFilePath
        return null
    }
}