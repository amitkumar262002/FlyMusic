package com.example.flymusicai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.R
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.MusicViewModel
import kotlin.math.abs

/**
 * Complete Music Player Screen All-in-one player with playback controls, settings, and advanced
 * features
 */
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
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
    val trackDuration by musicViewModel.trackDuration.collectAsState()
    val trackDurationMs by musicViewModel.trackDurationMs.collectAsState()
    val currentPositionMsState = musicViewModel.currentPositionMs.collectAsState()
    val currentPositionMs = currentPositionMsState.value
    val lyrics by musicViewModel.currentLyrics.collectAsState()
    val dynamicTheme by musicViewModel.dynamicThemeColors.collectAsState()
    val visualizerData by musicViewModel.visualizerData.collectAsState()

    var showSettingsPanel by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showAudioQualityDialog by remember { mutableStateOf(false) }
    var showLyricsOverlay by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val durationSeconds = if (trackDuration > 0) trackDuration else (currentSong?.duration ?: 210)
    val elapsedSeconds =
            if (trackDurationMs > 0) (currentPositionMs / 1000).toInt()
            else (currentPosition * durationSeconds).toInt()
    val isFavorite = currentSong?.let { song -> favoriteSongs.any { it.id == song.id } } ?: false

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
                                                    colors = dynamicTheme.backgroundGradient
                                            )
                            )
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                        onDragEnd = {
                                            if (abs(offsetX) > 100) {
                                                if (offsetX > 0) musicViewModel.playPrevious()
                                                else musicViewModel.playNext()
                                            }
                                            offsetX = 0f
                                        },
                                        onHorizontalDrag = { _, dragAmount ->
                                            offsetX += dragAmount
                                        }
                                )
                            }
    ) {
        Column(
                modifier =
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
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

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                            text = "PLAYING FROM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold.copy(alpha = 0.7f),
                            letterSpacing = 1.5.sp
                    )
                    Text(
                            text = currentSong?.genre ?: "FlyMusic AI",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite
                    )
                }

                // Action Icons Row (Top Right)
                IconButton(onClick = { /* More Options */}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextWhite)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main Content: Album Art + Side Action Bar
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                // Album Art (Weight 0.85)
                Box(modifier = Modifier.weight(0.85f), contentAlignment = Alignment.Center) {
                    currentSong?.let { song ->
                        val isBuffering by musicViewModel.isBuffering.collectAsState()

                        // Outer Pulsating Glow
                        val pulseScale by
                                infiniteTransition.animateFloat(
                                        initialValue = 1f,
                                        targetValue = 1.05f,
                                        animationSpec =
                                                infiniteRepeatable(
                                                        animation =
                                                                tween(
                                                                        2000,
                                                                        easing = FastOutSlowInEasing
                                                                ),
                                                        repeatMode = RepeatMode.Reverse
                                                ),
                                        label = "pulse"
                                )

                        // Glow Layer
                        Box(
                                modifier =
                                        Modifier.size(280.dp * pulseScale)
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

                        // The Image (Clean Circle, NO Black Dot)
                        AsyncImage(
                                model = song.coverImageUrl,
                                contentDescription = song.title,
                                modifier =
                                        Modifier.size(260.dp)
                                                .clip(CircleShape)
                                                .border(
                                                        2.dp,
                                                        AmberGold.copy(alpha = 0.5f),
                                                        CircleShape
                                                )
                                                .rotate(if (isPlaying) rotation else 0f),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(com.example.flymusicai.R.drawable.music_placeholder),
                                error = painterResource(com.example.flymusicai.R.drawable.music_placeholder)
                        )

                        // Buffering Indicator
                        if (isBuffering) {
                            CircularProgressIndicator(
                                    color = AmberGold,
                                    modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }

                // Side Action Bar (Weight 0.15)
                Column(
                        modifier = Modifier.weight(0.15f).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Bottom)
                ) {
                    // 1. Heart / Favorite
                    IconButton(
                            onClick = { currentSong?.let { musicViewModel.toggleFavorite(it) } }
                    ) {
                        Icon(
                                if (isFavorite) Icons.Filled.Favorite
                                else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color(0xFFFF4081) else TextWhite,
                                modifier = Modifier.size(28.dp)
                        )
                    }

                    // 2. Lyrics
                    IconButton(onClick = { showLyricsOverlay = true }) {
                        Icon(
                                Icons.Default.Lyrics,
                                contentDescription = "Lyrics",
                                tint = if (showLyricsOverlay) AmberGold else TextWhite,
                                modifier = Modifier.size(28.dp)
                        )
                    }

                    // 3. Add to Queue
                    IconButton(onClick = { currentSong?.let { musicViewModel.addToQueue(it) } }) {
                        Icon(
                                Icons.Default.Add,
                                contentDescription = "Add to Queue",
                                tint = TextWhite,
                                modifier = Modifier.size(28.dp)
                        )
                    }

                    // 4. Download
                    IconButton(onClick = { currentSong?.let { musicViewModel.downloadSong(it) } }) {
                        Icon(
                                Icons.Default.Download,
                                contentDescription = "Download",
                                tint = TextWhite,
                                modifier = Modifier.size(28.dp)
                        )
                    }

                    // 5. Share
                    val context = androidx.compose.ui.platform.LocalContext.current
                    IconButton(
                            onClick = { currentSong?.let { musicViewModel.shareSong(context, it) } }
                    ) {
                        Icon(
                                Icons.Default.Share,
                                contentDescription = "Share",
                                tint = TextWhite,
                                modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // --- Visualizer ---
            Row(
                    modifier = Modifier.fillMaxWidth().height(30.dp).padding(horizontal = 40.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.Bottom
            ) {
                visualizerData.forEach { height ->
                    Box(
                            modifier =
                                    Modifier.weight(1f)
                                            .fillMaxHeight(height.coerceIn(0.1f, 1f))
                                            .background(
                                                    dynamicTheme.primaryColor.copy(alpha = 0.6f),
                                                    RoundedCornerShape(2.dp)
                                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Song Info with Marquee Animation
            currentSong?.let { song ->
                Text(
                        text = song.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextWhite,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier =
                                Modifier.fillMaxWidth()
                                        .basicMarquee(
                                                iterations = Int.MAX_VALUE,
                                                repeatDelayMillis = 2000
                                        )
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
                ) { Text(text = song.genre, fontSize = 14.sp, color = TextTertiary) }
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
                            text = formatTime((currentPosition * durationSeconds * 1000).toLong()),
                            fontSize = 12.sp,
                            color = TextTertiary
                    )
                    Text(
                            text = formatTime((durationSeconds * 1000).toLong()),
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
                            tint = if (isShuffleEnabled) AmberGold else TextWhite,
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
                                        .shadow(16.dp, CircleShape, ambientColor = AmberGold),
                        containerColor = AmberGold,
                        contentColor = Color(0xFF0A1612)
                ) {
                    Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
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
                            tint = if (isRepeatEnabled) AmberGold else TextWhite,
                            modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Equalizer Button
            OutlinedButton(
                    onClick = onNavigateToEqualizer,
                    modifier = Modifier.fillMaxWidth(0.7f).height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors =
                            ButtonDefaults.outlinedButtonColors(
                                    contentColor = AmberGold,
                                    containerColor = Color.Transparent
                            ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, AmberGold)
            ) {
                Icon(
                        Icons.Default.Equalizer,
                        contentDescription = "Equalizer",
                        modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Equalizer", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Settings Panel Toggle
            TextButton(
                    onClick = { showSettingsPanel = !showSettingsPanel },
                    modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                        if (showSettingsPanel) "Hide Settings" else "Show Playback Settings",
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

            if (showSettingsPanel) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = Color(0xFF1A3A2E).copy(alpha = 0.7f)
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
                                description = "Automatically play next song",
                                checked = autoPlayEnabled,
                                onCheckedChange = { themeViewModel.toggleAutoPlay() }
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
                                onCheckedChange = { themeViewModel.toggleCrossfade() }
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
                                        if (sleepTimer > 0) "$sleepTimer minutes" else "Disabled",
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
                                onCheckedChange = { themeViewModel.toggleLyrics() }
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
                                onCheckedChange = { themeViewModel.toggleHighQualityAudio() }
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

            Spacer(modifier = Modifier.height(32.dp))

            // Recommended Songs Section
            RecommendedSuggestionsSection(
                    musicViewModel = musicViewModel,
                    onSongClick = { song ->
                        musicViewModel.playSong(song, musicViewModel.playerSuggestions.value)
                    }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // NEW: Up Next / Queue Section (SaavnMp3 Style)
            val queue by musicViewModel.currentQueue.collectAsState()
            val queueIndex by musicViewModel.currentQueueIndex.collectAsState()

            if (queue.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                text = "Up Next",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                        )
                        Text(
                                text = "${queue.size - queueIndex - 1} songs remaining",
                                fontSize = 12.sp,
                                color = AmberGold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val upcomingSongs = queue.drop(queueIndex + 1)
                    if (upcomingSongs.isEmpty()) {
                        Box(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(100.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.White.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                        ) { Text("No more songs in queue", color = TextTertiary) }
                    } else {
                        upcomingSongs.take(10).forEach { song ->
                            Row(
                                    modifier =
                                            Modifier.fillMaxWidth()
                                                    .padding(vertical = 8.dp)
                                                    .clickable {
                                                        musicViewModel.playSong(song, queue)
                                                    },
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                        model =
                                                song.coverImageUrl.ifEmpty {
                                                    "https://c.saavncdn.com/artists/${song.artist.replace(" ", "_")}_500x500.jpg"
                                                },
                                        contentDescription = null,
                                        modifier =
                                                Modifier.size(52.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(NavyLight),
                                        contentScale = ContentScale.Crop,
                                        error =
                                                androidx.compose.ui.res.painterResource(
                                                        id = android.R.drawable.ic_menu_gallery
                                                )
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                            text = song.title,
                                            color = TextWhite,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                            text = song.artist,
                                            color = TextTertiary,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            fontWeight = FontWeight.Medium
                                    )
                                }
                                Icon(
                                        Icons.Default.DragHandle,
                                        contentDescription = null,
                                        tint = AmberGold.copy(alpha = 0.5f)
                                )
                            }
                        }

                        if (upcomingSongs.size > 10) {
                            TextButton(
                                    onClick = { /* View Full Queue */},
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) { Text("View Full Queue", color = AmberGold) }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Lyrics Overlay
        androidx.compose.animation.AnimatedVisibility(
                visible = showLyricsOverlay,
                enter =
                        androidx.compose.animation.slideInVertically(initialOffsetY = { it }) +
                                androidx.compose.animation.fadeIn(),
                exit =
                        androidx.compose.animation.slideOutVertically(targetOffsetY = { it }) +
                                androidx.compose.animation.fadeOut()
        ) {
            Box(
                    modifier =
                            Modifier.fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.95f))
                                    .clickable { showLyricsOverlay = false }
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                "Lyrics",
                                color = AmberGold,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showLyricsOverlay = false }) {
                            Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                            )
                        }
                    }

                    if (lyrics.isEmpty()) {
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) { Text("No lyrics available for this track", color = TextTertiary) }
                    } else {
                        val currentSeconds =
                                (currentPosition * (currentSong?.duration ?: 1)).toInt()
                        val currentLyricIndex =
                                lyrics.indexOfLast { it.timestamp <= currentSeconds }
                        val listState = rememberLazyListState()

                        LaunchedEffect(currentLyricIndex) {
                            if (currentLyricIndex >= 0) {
                                listState.animateScrollToItem(maxOf(0, currentLyricIndex - 2))
                            }
                        }

                        LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 100.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            itemsIndexed(lyrics) { index, lyric ->
                                val isActive = index == currentLyricIndex
                                Text(
                                        text = lyric.text,
                                        fontSize = if (isActive) 26.sp else 20.sp,
                                        fontWeight =
                                                if (isActive) FontWeight.ExtraBold
                                                else FontWeight.Bold,
                                        color =
                                                if (isActive) Color.White
                                                else Color.White.copy(alpha = 0.4f),
                                        textAlign = TextAlign.Center,
                                        modifier =
                                                Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
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
        Icon(icon, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
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
                    Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = AmberGold, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
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
                                    horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                        if (minutes == 0) "Off" else "$minutes minutes",
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
                                    horizontalArrangement = Arrangement.SpaceBetween
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

@Composable
private fun RecommendedSuggestionsSection(
        musicViewModel: MusicViewModel,
        onSongClick: (com.example.flymusicai.data.Music) -> Unit
) {
    val suggestions by musicViewModel.playerSuggestions.collectAsState()
    var showAll by remember { mutableStateOf(false) }

    if (suggestions.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    "Up Next & Suggested",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
            )

            TextButton(onClick = { showAll = !showAll }) {
                Text(if (showAll) "Show Less" else "View All", color = AmberGold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val displayCount = if (showAll) suggestions.size else 4

        suggestions.take(displayCount).forEach { song ->
            Row(
                    modifier =
                            Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable {
                                onSongClick(song)
                            },
                    verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                        model =
                                song.coverImageUrl.ifEmpty {
                                    "https://c.saavncdn.com/artists/${song.artist.replace(" ", "_")}_500x500.jpg"
                                },
                        contentDescription = null,
                        modifier =
                                Modifier.size(52.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(NavyLight),
                        contentScale = ContentScale.Crop,
                        placeholder =
                                androidx.compose.ui.res.painterResource(
                                        id = com.example.flymusicai.R.drawable.music_placeholder
                                ),
                        error =
                                androidx.compose.ui.res.painterResource(
                                        id = com.example.flymusicai.R.drawable.music_placeholder
                                )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(song.title, color = TextWhite, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text(song.artist, color = TextTertiary, fontSize = 12.sp, maxLines = 1)
                }

                Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = AmberGold,
                        modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
