package com.example.flymusicai.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import android.util.Log

/**
 * Manages Firebase Realtime Database interactions for FlyMusicAI.
 * Saves/Retrieves song metadata for fast loading.
 */
object FirebaseMusicManager {
    private const val TAG = "FirebaseMusicManager"
    private val database = FirebaseDatabase.getInstance()
    private val songsRef = database.getReference("songs")

    /**
     * Save a song to Firebase if it doesn't exist or update it.
     */
    fun saveSongFunction(music: Music) {
        try {
            // Sanitize ID for Firebase key (remove special chars)
            val key = music.id.replace(".", "_").replace("#", "_").replace("$", "_").replace("[", "(").replace("]", ")")
            
            // We only save metadata, not the actual audio URL if it's dynamic/expiring
            // But if we have a static URL or we want to cache the thumbnail, we save it.
            // User requested to save thumbnail URL.
            
            val songData = mapOf(
                "id" to music.id,
                "title" to music.title,
                "artist" to music.artist,
                "coverImageUrl" to music.coverImageUrl,
                "duration" to music.duration,
                "album" to music.album,
                "genre" to music.genre,
                "year" to music.year
            )

            songsRef.child(key).setValue(songData)
                .addOnSuccessListener { Log.d(TAG, "Song saved to Firebase: ${music.title}") }
                .addOnFailureListener { Log.w(TAG, "Failed to save song to Firebase", it) }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving to Firebase", e)
        }
    }

    /**
     * Save multiple songs to Firebase
     */
    fun saveSongs(songs: List<Music>) {
        songs.forEach { saveSongFunction(it) }
    }

    /**
     * Fetch all songs from Firebase (Fast Loading)
     */
    suspend fun getAllSongs(): List<Music> {
        return try {
            val snapshot = songsRef.get().await()
            val musicList = mutableListOf<Music>()
            
            snapshot.children.forEach { child ->
                try {
                    val id = child.child("id").getValue(String::class.java) ?: ""
                    val title = child.child("title").getValue(String::class.java) ?: "Unknown"
                    val artist = child.child("artist").getValue(String::class.java) ?: "Unknown"
                    val cover = child.child("coverImageUrl").getValue(String::class.java) ?: ""
                    val duration = child.child("duration").getValue(Long::class.java)?.toInt() ?: 0
                    val album = child.child("album").getValue(String::class.java) ?: ""
                    val genre = child.child("genre").getValue(String::class.java) ?: "Unknown"
                    val year = child.child("year").getValue(Int::class.java) ?: 0
                    
                    if (id.isNotEmpty()) {
                        musicList.add(Music(
                            id = id,
                            title = title,
                            artist = artist,
                            coverImageUrl = cover,
                            duration = duration,
                            audioUrl = "", // Set empty, allow resolution later
                            album = album,
                            genre = genre,
                            year = year
                        ))
                    }
                } catch (e: Exception) {
                   Log.w(TAG, "Error parsing song from Firebase", e)
                }
            }
            Log.d(TAG, "Fetched ${musicList.size} songs from Firebase")
            musicList
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching from Firebase", e)
            emptyList()
        }
    }
}
