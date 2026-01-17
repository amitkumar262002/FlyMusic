package com.example.flymusicai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.MusicViewModel
import kotlin.math.abs

/**
 * 🎵 Complete Music Player Screen All-in-one player with playback controls, settings, and advanced
 * features
 */
@Composable
fun MusicPlayerScreen(
        musicViewModel: MusicViewModel,
        themeViewModel: com.example.flymusicai.viewmodel.ThemeViewModel,
        onBackPress: () -> Unit,
        onNavigateToEqualizer: () -> Unit = {}
) {
        val currentSong by musicViewModel.currentSong.collectAsState()
        val isPlaying by musicViewModel.isPlaying.collectAsState()
        val currentPosition by musicViewModel.currentPosition.collectAsState()
        val isRepeatEnabled by musicViewModel.isRepeatEnabled.collectAsState()
        val isShuffleEnabled by musicViewModel.isShuffleEnabled.collectAsState()
        val favoriteSongs by musicViewModel.favoriteSongs.collectAsState()

        // Settings states
        val autoPlayEnabled by themeViewModel.autoPlayEnabled.collectAsState()
        val crossfadeEnabled by themeViewModel.crossfadeEnabled.collectAsState()
        val crossfadeDuration by themeViewModel.crossfadeDuration.collectAsState()
        val lyricsEnabled by themeViewModel.lyricsEnabled.collectAsState()
        val highQualityAudioEnabled by themeViewModel.highQualityAudioEnabled.collectAsState()
        val audioQuality by themeViewModel.audioQuality.collectAsState()
        val equalizerPreset by themeViewModel.equalizerPreset.collectAsState()
        val sleepTimer by themeViewModel.sleepTimer.collectAsState()

        val durationSeconds = currentSong?.duration ?: 210
        val isFavorite =
                currentSong?.let { song -> favoriteSongs.any { it.id == song.id } } ?: false

        var showSettingsPanel by remember { mutableStateOf(false) }
        var showSleepTimerDialog by remember { mutableStateOf(false) }
        var showAudioQualityDialog by remember { mutableStateOf(false) }

        // Rotation animation
        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
        val rotation by
                infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(20000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Restart
                                ),
                        label = "rotation"
                )

        // Swipe gestures
        var offsetX by remember { mutableFloatStateOf(0f) }
        var offsetY by remember { mutableFloatStateOf(0f) }

        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .background(
                                        brush =
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        Color(0xFF1A3A2E),
                                                                        Color(0xFF0D1F1A),
                                                                        Color(0xFF0A1612)
                                                                )
                                                )
                                )
                                .pointerInput(Unit) {
                                        detectHorizontalDragGestures(
                                                onDragEnd = {
                                                        if (abs(offsetX) > 100) {
                                                                if (offsetX > 0)
                                                                        musicViewModel
                                                                                .playPrevious()
                                                                else musicViewModel.playNext()
                                                        }
                                                        offsetX = 0f
                                                },
                                                onHorizontalDrag = { _, dragAmount ->
                                                        offsetX += dragAmount
                                                }
                                        )
                                }
                                .pointerInput(Unit) {
                                        detectVerticalDragGestures(
                                                onDragEnd = {
                                                        if (abs(offsetY) > 100) {
                                                                if (offsetY > 0)
                                                                        musicViewModel
                                                                                .playPrevious()
                                                                else musicViewModel.playNext()
                                                        }
                                                        offsetY = 0f
                                                },
                                                onVerticalDrag = { _, dragAmount ->
                                                        offsetY += dragAmount
                                                }
                                        )
                                }
        ) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // Top Bar
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                IconButton(onClick = onBackPress) {
                                        Icon(
                                                Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back",
                                                tint = TextWhite,
                                                modifier = Modifier.size(28.dp)
                                        )
                                }

                                Text(
                                        text = "Now Playing",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextWhite
                                )

                                IconButton(
                                        onClick = {
                                                currentSong?.let {
                                                        musicViewModel.toggleFavorite(it)
                                                }
                                        }
                                ) {
                                        Icon(
                                                if (isFavorite) Icons.Filled.Favorite
                                                else Icons.Filled.FavoriteBorder,
                                                contentDescription = "Favorite",
                                                tint =
                                                        if (isFavorite) Color(0xFFFF4081)
                                                        else TextWhite,
                                                modifier = Modifier.size(28.dp)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Album Art
                        currentSong?.let { song ->
                                Box(
                                        modifier =
                                                Modifier.size(280.dp)
                                                        .shadow(
                                                                24.dp,
                                                                CircleShape,
                                                                ambientColor = AmberGold,
                                                                spotColor = AmberGold
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.size(300.dp)
                                                                .background(
                                                                        brush =
                                                                                Brush.radialGradient(
                                                                                        colors =
                                                                                                listOf(
                                                                                                        AmberGold
                                                                                                                .copy(
                                                                                                                        alpha =
                                                                                                                                0.3f
                                                                                                                ),
                                                                                                        Color.Transparent
                                                                                                )
                                                                                ),
                                                                        shape = CircleShape
                                                                )
                                        )

                                        AsyncImage(
                                                model = song.coverImageUrl,
                                                contentDescription = song.title,
                                                modifier =
                                                        Modifier.size(280.dp)
                                                                .clip(CircleShape)
                                                                .rotate(
                                                                        if (isPlaying) rotation
                                                                        else 0f
                                                                ),
                                                contentScale = ContentScale.Crop
                                        )

                                        Box(
                                                modifier =
                                                        Modifier.size(80.dp)
                                                                .background(
                                                                        Color(0xFF1A1A1A),
                                                                        CircleShape
                                                                )
                                                                .rotate(
                                                                        if (isPlaying) rotation
                                                                        else 0f
                                                                )
                                        ) {
                                                Box(
                                                        modifier =
                                                                Modifier.size(24.dp)
                                                                        .align(Alignment.Center)
                                                                        .background(
                                                                                Color(0xFF333333),
                                                                                CircleShape
                                                                        )
                                                )
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Song Info
                        currentSong?.let { song ->
                                Text(
                                        text = song.title,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextWhite,
                                        textAlign = TextAlign.Center,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                        text = song.artist,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AmberGold,
                                        textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                Icons.Default.MusicNote,
                                                contentDescription = null,
                                                tint = TextTertiary,
                                                modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                                text = song.genre,
                                                fontSize = 14.sp,
                                                color = TextTertiary
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Progress Bar
                        Column(modifier = Modifier.fillMaxWidth()) {
                                Slider(
                                        value = currentPosition,
                                        onValueChange = { musicViewModel.seekTo(it) },
                                        valueRange = 0f..1f,
                                        colors =
                                                SliderDefaults.colors(
                                                        thumbColor = AmberGold,
                                                        activeTrackColor = AmberGold,
                                                        inactiveTrackColor = Color(0xFF3A3A3A)
                                                ),
                                        modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                text =
                                                        formatTime(
                                                                (currentPosition * durationSeconds)
                                                                        .toLong()
                                                        ),
                                                fontSize = 12.sp,
                                                color = TextTertiary
                                        )
                                        Text(
                                                text = formatTime(durationSeconds.toLong()),
                                                fontSize = 12.sp,
                                                color = TextTertiary
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Playback Controls
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                IconButton(
                                        onClick = { musicViewModel.toggleShuffle() },
                                        modifier = Modifier.size(48.dp)
                                ) {
                                        Icon(
                                                Icons.Filled.Shuffle,
                                                contentDescription = "Shuffle",
                                                tint =
                                                        if (isShuffleEnabled) AmberGold
                                                        else TextWhite,
                                                modifier = Modifier.size(28.dp)
                                        )
                                }

                                IconButton(
                                        onClick = { musicViewModel.playPrevious() },
                                        modifier = Modifier.size(64.dp)
                                ) {
                                        Icon(
                                                Icons.Default.SkipPrevious,
                                                contentDescription = "Previous",
                                                tint = TextWhite,
                                                modifier = Modifier.size(40.dp)
                                        )
                                }

                                FloatingActionButton(
                                        onClick = { musicViewModel.togglePlayPause() },
                                        modifier =
                                                Modifier.size(80.dp)
                                                        .shadow(
                                                                16.dp,
                                                                CircleShape,
                                                                ambientColor = AmberGold
                                                        ),
                                        containerColor = AmberGold,
                                        contentColor = Color(0xFF0A1612)
                                ) {
                                        Icon(
                                                if (isPlaying) Icons.Default.Pause
                                                else Icons.Default.PlayArrow,
                                                contentDescription =
                                                        if (isPlaying) "Pause" else "Play",
                                                modifier = Modifier.size(48.dp)
                                        )
                                }

                                IconButton(
                                        onClick = { musicViewModel.playNext() },
                                        modifier = Modifier.size(64.dp)
                                ) {
                                        Icon(
                                                Icons.Default.SkipNext,
                                                contentDescription = "Next",
                                                tint = TextWhite,
                                                modifier = Modifier.size(40.dp)
                                        )
                                }

                                IconButton(
                                        onClick = { musicViewModel.toggleRepeat() },
                                        modifier = Modifier.size(48.dp)
                                ) {
                                        Icon(
                                                Icons.Default.Repeat,
                                                contentDescription = "Repeat",
                                                tint =
                                                        if (isRepeatEnabled) AmberGold
                                                        else TextWhite,
                                                modifier = Modifier.size(28.dp)
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Settings Panel Toggle
                        TextButton(
                                onClick = { showSettingsPanel = !showSettingsPanel },
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                Text(
                                        if (showSettingsPanel) "Hide Settings"
                                        else "Show Playback Settings",
                                        color = AmberGold,
                                        fontWeight = FontWeight.Bold
                                )
                                Icon(
                                        if (showSettingsPanel) Icons.Default.ExpandLess
                                        else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = AmberGold
                                )
                        }

                        // Settings Panel
                        if (showSettingsPanel) {
                                Spacer(modifier = Modifier.height(16.dp))

                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                Color(0xFF1A3A2E).copy(alpha = 0.7f)
                                                )
                                ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                                // PLAYBACK Section
                                                Text(
                                                        "PLAYBACK",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = AmberGold,
                                                        modifier = Modifier.padding(bottom = 12.dp)
                                                )

                                                SettingItem(
                                                        icon = Icons.Default.PlayArrow,
                                                        title = "Auto-Play",
                                                        description =
                                                                "Automatically play next song",
                                                        checked = autoPlayEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel.toggleAutoPlay()
                                                        }
                                                )

                                                HorizontalDivider(
                                                        color = Color(0xFF2A4A3E),
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                )

                                                SettingItem(
                                                        icon = Icons.Default.Shuffle,
                                                        title = "Crossfade",
                                                        description = "$crossfadeDuration seconds",
                                                        checked = crossfadeEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel.toggleCrossfade()
                                                        }
                                                )

                                                HorizontalDivider(
                                                        color = Color(0xFF2A4A3E),
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                )

                                                SettingItemClickable(
                                                        icon = Icons.Default.Equalizer,
                                                        title = "Equalizer",
                                                        description = equalizerPreset,
                                                        onClick = onNavigateToEqualizer
                                                )

                                                HorizontalDivider(
                                                        color = Color(0xFF2A4A3E),
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                )

                                                SettingItemClickable(
                                                        icon = Icons.Default.Timer,
                                                        title = "Sleep Timer",
                                                        description =
                                                                if (sleepTimer > 0)
                                                                        "$sleepTimer minutes"
                                                                else "Disabled",
                                                        onClick = { showSleepTimerDialog = true }
                                                )

                                                HorizontalDivider(
                                                        color = Color(0xFF2A4A3E),
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                )

                                                SettingItem(
                                                        icon = Icons.Default.Lyrics,
                                                        title = "Show Lyrics",
                                                        description = "Display song lyrics",
                                                        checked = lyricsEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel.toggleLyrics()
                                                        }
                                                )

                                                Spacer(modifier = Modifier.height(24.dp))

                                                // AUDIO QUALITY Section
                                                Text(
                                                        "AUDIO QUALITY",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = AmberGold,
                                                        modifier = Modifier.padding(bottom = 12.dp)
                                                )

                                                SettingItem(
                                                        icon = Icons.Default.HighQuality,
                                                        title = "High Quality Audio",
                                                        description = "Better sound, more data",
                                                        checked = highQualityAudioEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel
                                                                        .toggleHighQualityAudio()
                                                        }
                                                )

                                                HorizontalDivider(
                                                        color = Color(0xFF2A4A3E),
                                                        modifier = Modifier.padding(vertical = 8.dp)
                                                )

                                                SettingItemClickable(
                                                        icon = Icons.Default.GraphicEq,
                                                        title = "Audio Quality",
                                                        description = audioQuality,
                                                        onClick = { showAudioQualityDialog = true }
                                                )
                                        }
                                }
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                }
        }

        // Dialogs
        if (showSleepTimerDialog) {
                SleepTimerDialog(
                        currentTimer = sleepTimer,
                        onDismiss = { showSleepTimerDialog = false },
                        onSetTimer = { minutes ->
                                themeViewModel.setSleepTimer(minutes)
                                showSleepTimerDialog = false
                        }
                )
        }

        if (showAudioQualityDialog) {
                AudioQualityDialog(
                        currentQuality = audioQuality,
                        onDismiss = { showAudioQualityDialog = false },
                        onSelect = { quality ->
                                themeViewModel.setAudioQuality(quality)
                                showAudioQualityDialog = false
                        }
                )
        }
}

@Composable
private fun SettingItem(
        icon: ImageVector,
        title: String,
        description: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                        icon,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                        )
                        Text(description, fontSize = 12.sp, color = TextTertiary)
                }
                Switch(
                        checked = checked,
                        onCheckedChange = onCheckedChange,
                        colors =
                                SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF0A1612),
                                        checkedTrackColor = AmberGold,
                                        uncheckedThumbColor = Color.Gray,
                                        uncheckedTrackColor = Color.DarkGray
                                )
                )
        }
}

@Composable
private fun SettingItemClickable(
        icon: ImageVector,
        title: String,
        description: String,
        onClick: () -> Unit
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clickable(onClick = onClick)
                                .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                        icon,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                        )
                        Text(description, fontSize = 12.sp, color = TextTertiary)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextTertiary)
        }
}

@Composable
private fun SleepTimerDialog(currentTimer: Int, onDismiss: () -> Unit, onSetTimer: (Int) -> Unit) {
        val timers = listOf(0, 15, 30, 45, 60, 90, 120)

        AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Sleep Timer", color = AmberGold, fontWeight = FontWeight.Bold) },
                text = {
                        Column {
                                timers.forEach { minutes ->
                                        TextButton(
                                                onClick = { onSetTimer(minutes) },
                                                modifier = Modifier.fillMaxWidth()
                                        ) {
                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement =
                                                                Arrangement.SpaceBetween
                                                ) {
                                                        Text(
                                                                if (minutes == 0) "Off"
                                                                else "$minutes minutes",
                                                                color = TextWhite
                                                        )
                                                        if (minutes == currentTimer) {
                                                                Icon(
                                                                        Icons.Default.Check,
                                                                        contentDescription = null,
                                                                        tint = AmberGold
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                },
                confirmButton = {
                        TextButton(onClick = onDismiss) { Text("Close", color = AmberGold) }
                },
                containerColor = Color(0xFF1A3A2E)
        )
}

@Composable
private fun AudioQualityDialog(
        currentQuality: String,
        onDismiss: () -> Unit,
        onSelect: (String) -> Unit
) {
        val qualities = listOf("Normal", "High", "Extreme")

        AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Audio Quality", color = AmberGold, fontWeight = FontWeight.Bold) },
                text = {
                        Column {
                                qualities.forEach { quality ->
                                        TextButton(
                                                onClick = { onSelect(quality) },
                                                modifier = Modifier.fillMaxWidth()
                                        ) {
                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement =
                                                                Arrangement.SpaceBetween
                                                ) {
                                                        Text(quality, color = TextWhite)
                                                        if (quality == currentQuality) {
                                                                Icon(
                                                                        Icons.Default.Check,
                                                                        contentDescription = null,
                                                                        tint = AmberGold
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                },
                confirmButton = {
                        TextButton(onClick = onDismiss) { Text("Close", color = AmberGold) }
                },
                containerColor = Color(0xFF1A3A2E)
        )
}

private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        return String.format("%02d:%02d", minutes, seconds)
}
