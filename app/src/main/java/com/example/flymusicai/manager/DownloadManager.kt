package com.example.flymusicai.manager

import android.content.Context
import android.widget.Toast
import com.example.flymusicai.data.Music
import java.io.File

class DownloadManager(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("downloads", Context.MODE_PRIVATE)

    fun downloadSong(music: Music) {
        // Simulation: Mark as downloaded and save "path"
        // In a real app, uses DownloadManager system service
        val editor = sharedPreferences.edit()
        editor.putBoolean(music.id, true)

        // Mocking a local file path (in real app, this would be a file in filesDir)
        val file = File(context.filesDir, "${music.id}.mp3")
        if (!file.exists()) {
            file.createNewFile() // Create dummy file to simulate presence
        }
        editor.putString("${music.id}_path", file.absolutePath)
        editor.apply()

        Toast.makeText(context, "Downloaded: ${music.title}", Toast.LENGTH_SHORT).show()
    }

    fun isDownloaded(songId: String): Boolean {
        return sharedPreferences.getBoolean(songId, false)
    }

    fun deleteSong(songId: String) {
        val editor = sharedPreferences.edit()
        editor.remove(songId)
        editor.remove("${songId}_path")
        editor.apply()

        val file = File(context.filesDir, "${songId}.mp3")
        if (file.exists()) {
            file.delete()
        }
    }

    fun getLocalFilePath(songId: String): String? {
        return sharedPreferences.getString("${songId}_path", null)
    }
}
