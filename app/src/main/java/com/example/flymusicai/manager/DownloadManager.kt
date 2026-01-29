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
        
        // Save metadata so we can reconstruct the song object offline
        editor.putString("${music.id}_title", music.title)
        editor.putString("${music.id}_artist", music.artist)
        editor.putString("${music.id}_cover", music.coverImageUrl)
        editor.putString("${music.id}_album", music.album)
        editor.putLong("${music.id}_duration", music.duration.toLong())

        // Mocking a local file path (in real app, this would be a file in filesDir)
        val file = File(context.filesDir, "${music.id}.mp3")
        if (!file.exists()) {
            file.createNewFile() // Create dummy file to simulate presence
        }
        editor.putString("${music.id}_path", file.absolutePath)
        editor.apply()

        android.os.Handler(android.os.Looper.getMainLooper()).post {
            Toast.makeText(context, "Downloaded: ${music.title}", Toast.LENGTH_SHORT).show()
        }
    }

    fun isDownloaded(songId: String): Boolean {
        return sharedPreferences.getBoolean(songId, false)
    }

    fun deleteSong(songId: String) {
        val editor = sharedPreferences.edit()
        editor.remove(songId)
        editor.remove("${songId}_path")
        editor.remove("${songId}_title")
        editor.remove("${songId}_artist")
        editor.remove("${songId}_cover")
        editor.remove("${songId}_album")
        editor.remove("${songId}_duration")
        editor.apply()

        val file = File(context.filesDir, "${songId}.mp3")
        if (file.exists()) {
            file.delete()
        }
    }

    fun getLocalFilePath(songId: String): String? {
        return sharedPreferences.getString("${songId}_path", null)
    }

    /** Retrieve all downloaded songs with their metadata */
    fun getAllDownloadedSongs(): List<Music> {
        val allEntries = sharedPreferences.all
        val songs = mutableListOf<Music>()
        
        allEntries.forEach { (key, value) ->
            // If the key is a song ID (boolean true), reconstruct the song
            if (value is Boolean && value == true && !key.contains("_")) {
                val id = key
                val title = sharedPreferences.getString("${id}_title", "Unknown Title") ?: "Unknown Title"
                val artist = sharedPreferences.getString("${id}_artist", "Unknown Artist") ?: "Unknown Artist"
                val cover = sharedPreferences.getString("${id}_cover", "") ?: ""
                val album = sharedPreferences.getString("${id}_album", "") ?: ""
                val duration = sharedPreferences.getLong("${id}_duration", 0L)
                val path = sharedPreferences.getString("${id}_path", "") ?: ""
                
                songs.add(
                    Music(
                        id = id,
                        title = title,
                        artist = artist,
                        coverImageUrl = if (cover.isNotBlank()) cover else com.example.flymusicai.data.ArtistConstants.getArtistImage(artist),
                        duration = duration.toInt(),
                        audioUrl = path, // Use local path as audio URL
                        album = album,
                        isDownloaded = true
                    )
                )
            }
        }
        return songs
    }
}
