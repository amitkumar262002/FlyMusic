package com.example.flymusicai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.flymusicai.data.Music
import com.example.flymusicai.ui.theme.*

/** 📋 Queue Screen - Manage playing queue and upcoming songs */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
        currentSong: Music?,
        queue: List<Music>,
        currentIndex: Int,
        onSongClick: (Int) -> Unit,
        onRemoveFromQueue: (Int) -> Unit,
        onMoveUp: (Int) -> Unit,
        onMoveDown: (Int) -> Unit,
        onClearQueue: () -> Unit,
        onSaveAsPlaylist: () -> Unit,
        onBack: () -> Unit,
        modifier: Modifier = Modifier
) {
        var showClearDialog by remember { mutableStateOf(false) }
        var showSaveDialog by remember { mutableStateOf(false) }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Column {
                                                Text(
                                                        "Playing Queue",
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                )
                                                Text(
                                                        "${queue.size} songs",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.White.copy(alpha = 0.7f)
                                                )
                                        }
                                },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Close",
                                                        tint = Color.White
                                                )
                                        }
                                },
                                actions = {
                                        // Save as Playlist
                                        IconButton(onClick = { showSaveDialog = true }) {
                                                Icon(
                                                        Icons.AutoMirrored.Filled.PlaylistAdd,
                                                        contentDescription = "Save as Playlist",
                                                        tint = GoldAccent
                                                )
                                        }
                                        // Clear Queue
                                        IconButton(onClick = { showClearDialog = true }) {
                                                Icon(
                                                        Icons.Default.ClearAll,
                                                        contentDescription = "Clear Queue",
                                                        tint = Color.White
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
                        )
                },
                containerColor = NavyBlue
        ) { paddingValues ->
                Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
                        if (queue.isEmpty()) {
                                // Empty State
                                Column(
                                        modifier = Modifier.fillMaxSize().padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                        Icon(
                                                Icons.AutoMirrored.Filled.QueueMusic,
                                                contentDescription = null,
                                                modifier = Modifier.size(80.dp),
                                                tint = Color.White.copy(alpha = 0.3f)
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                                "Queue is Empty",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.5f)
                                        )

                                        Text(
                                                "Add songs to start playing",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White.copy(alpha = 0.4f)
                                        )
                                }
                        } else {
                                LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        // Now Playing Header
                                        item {
                                                Text(
                                                        "NOW PLAYING",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = GoldAccent,
                                                        modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                        }

                                        // Current Song
                                        currentSong?.let { song -> item { CurrentSongCard(song) } }

                                        // Next Up Header
                                        item {
                                                Spacer(modifier = Modifier.height(24.dp))
                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement =
                                                                Arrangement.SpaceBetween,
                                                        verticalAlignment =
                                                                Alignment.CenterVertically
                                                ) {
                                                        Text(
                                                                "NEXT UP",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .labelSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color =
                                                                        Color.White.copy(
                                                                                alpha = 0.7f
                                                                        )
                                                        )
                                                        Text(
                                                                "${queue.size - currentIndex - 1} songs",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color =
                                                                        Color.White.copy(
                                                                                alpha = 0.5f
                                                                        )
                                                        )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                        }

                                        // Queue Items (excluding current)
                                        itemsIndexed(
                                                items =
                                                        queue.filterIndexed { index, _ ->
                                                                index > currentIndex
                                                        }
                                        ) { displayIndex, song ->
                                                val actualIndex = currentIndex + displayIndex + 1
                                                QueueSongItem(
                                                        song = song,
                                                        position = actualIndex - currentIndex,
                                                        isPlaying = false,
                                                        onClick = { onSongClick(actualIndex) },
                                                        onRemove = {
                                                                onRemoveFromQueue(actualIndex)
                                                        },
                                                        onMoveUp =
                                                                if (actualIndex > currentIndex + 1
                                                                ) {
                                                                        { onMoveUp(actualIndex) }
                                                                } else null,
                                                        onMoveDown =
                                                                if (actualIndex < queue.lastIndex) {
                                                                        { onMoveDown(actualIndex) }
                                                                } else null
                                                )
                                        }

                                        // Padding at bottom
                                        item { Spacer(modifier = Modifier.height(100.dp)) }
                                }
                        }
                }
        }

        // Clear Queue Dialog
        if (showClearDialog) {
                AlertDialog(
                        onDismissRequest = { showClearDialog = false },
                        title = {
                                Text(
                                        "Clear Queue?",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                )
                        },
                        text = {
                                Text(
                                        "This will remove all songs from the queue except the currently playing song.",
                                        color = Color.White.copy(alpha = 0.8f)
                                )
                        },
                        confirmButton = {
                                Button(
                                        onClick = {
                                                onClearQueue()
                                                showClearDialog = false
                                        },
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = Color.Red
                                                )
                                ) { Text("Clear") }
                        },
                        dismissButton = {
                                TextButton(onClick = { showClearDialog = false }) {
                                        Text("Cancel", color = GoldAccent)
                                }
                        },
                        containerColor = DarkNavy
                )
        }

        // Save as Playlist Dialog
        if (showSaveDialog) {
                var playlistName by remember { mutableStateOf("") }

                AlertDialog(
                        onDismissRequest = { showSaveDialog = false },
                        title = {
                                Text(
                                        "Save as Playlist",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                )
                        },
                        text = {
                                Column {
                                        Text(
                                                "Enter playlist name:",
                                                color = Color.White.copy(alpha = 0.8f)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        OutlinedTextField(
                                                value = playlistName,
                                                onValueChange = { playlistName = it },
                                                placeholder = { Text("My Queue Playlist") },
                                                colors =
                                                        OutlinedTextFieldDefaults.colors(
                                                                focusedBorderColor = GoldAccent,
                                                                unfocusedBorderColor =
                                                                        Color.White.copy(
                                                                                alpha = 0.3f
                                                                        ),
                                                                focusedTextColor = Color.White,
                                                                unfocusedTextColor = Color.White
                                                        ),
                                                modifier = Modifier.fillMaxWidth()
                                        )
                                }
                        },
                        confirmButton = {
                                Button(
                                        onClick = {
                                                if (playlistName.isNotBlank()) {
                                                        onSaveAsPlaylist()
                                                        showSaveDialog = false
                                                }
                                        },
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = GoldAccent
                                                )
                                ) { Text("Save", color = NavyBlue) }
                        },
                        dismissButton = {
                                TextButton(onClick = { showSaveDialog = false }) {
                                        Text("Cancel", color = GoldAccent)
                                }
                        },
                        containerColor = DarkNavy
                )
        }
}

@Composable
private fun CurrentSongCard(song: Music) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(
                                                Brush.horizontalGradient(
                                                        colors =
                                                                listOf(
                                                                        GoldAccent.copy(
                                                                                alpha = 0.3f
                                                                        ),
                                                                        TealAccent.copy(
                                                                                alpha = 0.2f
                                                                        )
                                                                )
                                                )
                                        )
                                        .padding(16.dp)
                ) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                // Album Art with Playing Indicator
                                Box {
                                        AsyncImage(
                                                model = song.coverImageUrl,
                                                contentDescription = song.title,
                                                modifier =
                                                        Modifier.size(70.dp)
                                                                .clip(RoundedCornerShape(12.dp)),
                                                contentScale = ContentScale.Crop
                                        )

                                        // Playing Indicator
                                        Box(
                                                modifier =
                                                        Modifier.align(Alignment.BottomEnd)
                                                                .padding(4.dp)
                                                                .size(24.dp)
                                                                .clip(CircleShape)
                                                                .background(GoldAccent),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Icon(
                                                        Icons.Default.PlayArrow,
                                                        contentDescription = "Playing",
                                                        modifier = Modifier.size(16.dp),
                                                        tint = NavyBlue
                                                )
                                        }
                                }

                                // Song Info
                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = song.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                        )

                                        Text(
                                                text = song.artist,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.8f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                        )

                                        // Duration
                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.Timer,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(12.dp),
                                                        tint = Color.White.copy(alpha = 0.6f)
                                                )
                                                Text(
                                                        text = formatDuration(song.duration),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = Color.White.copy(alpha = 0.6f)
                                                )
                                        }
                                }

                                // Audio Waves Animation
                                Icon(
                                        Icons.Default.GraphicEq,
                                        contentDescription = "Playing",
                                        modifier = Modifier.size(32.dp),
                                        tint = GoldAccent
                                )
                        }
                }
        }
}

@Composable
private fun QueueSongItem(
        song: Music,
        position: Int,
        isPlaying: Boolean,
        onClick: () -> Unit,
        onRemove: () -> Unit,
        onMoveUp: (() -> Unit)?,
        onMoveDown: (() -> Unit)?
) {
        var showMenu by remember { mutableStateOf(false) }

        Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavy)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        // Position Number
                        Box(
                                modifier =
                                        Modifier.size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                        ) {
                                if (isPlaying) {
                                        Icon(
                                                Icons.Default.GraphicEq,
                                                contentDescription = null,
                                                tint = GoldAccent,
                                                modifier = Modifier.size(16.dp)
                                        )
                                } else {
                                        Text(
                                                text = "$position",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.7f)
                                        )
                                }
                        }

                        // Album Art
                        AsyncImage(
                                model = song.coverImageUrl,
                                contentDescription = song.title,
                                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                        )

                        // Song Info
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = song.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                        text = song.artist,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                        }

                        // Duration
                        Text(
                                text = formatDuration(song.duration),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.5f)
                        )

                        // Menu
                        Box {
                                IconButton(onClick = { showMenu = true }) {
                                        Icon(
                                                Icons.Default.MoreVert,
                                                contentDescription = "Options",
                                                tint = Color.White.copy(alpha = 0.6f)
                                        )
                                }

                                DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false },
                                        modifier = Modifier.background(DarkNavy)
                                ) {
                                        // Move Up
                                        onMoveUp?.let { moveUp ->
                                                DropdownMenuItem(
                                                        text = {
                                                                Row(
                                                                        verticalAlignment =
                                                                                Alignment
                                                                                        .CenterVertically,
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                8.dp
                                                                                        )
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .ArrowUpward,
                                                                                contentDescription =
                                                                                        null,
                                                                                tint = Color.White
                                                                        )
                                                                        Text(
                                                                                "Move Up",
                                                                                color = Color.White
                                                                        )
                                                                }
                                                        },
                                                        onClick = {
                                                                moveUp()
                                                                showMenu = false
                                                        }
                                                )
                                        }

                                        // Move Down
                                        onMoveDown?.let { moveDown ->
                                                DropdownMenuItem(
                                                        text = {
                                                                Row(
                                                                        verticalAlignment =
                                                                                Alignment
                                                                                        .CenterVertically,
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                8.dp
                                                                                        )
                                                                ) {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .ArrowDownward,
                                                                                contentDescription =
                                                                                        null,
                                                                                tint = Color.White
                                                                        )
                                                                        Text(
                                                                                "Move Down",
                                                                                color = Color.White
                                                                        )
                                                                }
                                                        },
                                                        onClick = {
                                                                moveDown()
                                                                showMenu = false
                                                        }
                                                )
                                        }

                                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                                        // Remove from Queue
                                        DropdownMenuItem(
                                                text = {
                                                        Row(
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically,
                                                                horizontalArrangement =
                                                                        Arrangement.spacedBy(8.dp)
                                                        ) {
                                                                Icon(
                                                                        Icons.Default
                                                                                .RemoveCircleOutline,
                                                                        contentDescription = null,
                                                                        tint = Color.Red
                                                                )
                                                                Text(
                                                                        "Remove from Queue",
                                                                        color = Color.Red
                                                                )
                                                        }
                                                },
                                                onClick = {
                                                        onRemove()
                                                        showMenu = false
                                                }
                                        )
                                }
                        }
                }
        }
}

private fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", mins, secs)
}
