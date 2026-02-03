package com.example.flymusicai.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.data.Music
import com.example.flymusicai.ui.theme.AmberGold
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ForYouScreen(
        musicViewModel: com.example.flymusicai.viewmodel.MusicViewModel,
        onSongClick: (Music) -> Unit = {},
        modifier: Modifier = Modifier
) {
        val forYouSongs by musicViewModel.forYouSongs.collectAsState()
        val allMusic by musicViewModel.allMusic.collectAsState()
        val currentSong by musicViewModel.currentSong.collectAsState()
        val isPaused by musicViewModel.isPlaying.collectAsState().let { 
            derivedStateOf { !it.value } 
        }

        // Use forYouSongs as the source of truth for this screen
        // If forYouSongs is empty, try to use allMusic, if allMusic is empty, show loading
        val displaySongs: List<Music> =
                remember(forYouSongs, allMusic) {
                        if (forYouSongs.isNotEmpty()) {
                            forYouSongs
                        } else {
                            allMusic.shuffled().take(15)
                        }
                }

        // Initialize pager state
        val initialPage = remember(displaySongs) {
            val index = displaySongs.indexOfFirst { it.id == currentSong?.id }
            if (index != -1) index else 0
        }
        
        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { displaySongs.size }
        )

        // Sync with global playback when currentSong changes externally
        LaunchedEffect(currentSong) {
            val index = displaySongs.indexOfFirst { it.id == currentSong?.id }
            if (index != -1 && index != pagerState.currentPage) {
                pagerState.animateScrollToPage(index)
            }
        }

        // Automatic playback when page changes or entered
        LaunchedEffect(pagerState.currentPage, displaySongs) {
                if (displaySongs.isNotEmpty() && pagerState.currentPage < displaySongs.size) {
                        val song = displaySongs[pagerState.currentPage]
                        // Smart auto-play: If it's the current song but paused, play it. 
                        // If it's a different song, switch and play.
                        if (currentSong?.id != song.id) {
                            musicViewModel.playSong(song, displaySongs)
                        } else if (isPaused) {
                            musicViewModel.togglePlayPause()
                        }
                }
        }

        Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
                if (displaySongs.isEmpty()) {
                        Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                CircularProgressIndicator(color = AmberGold)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Curating your feed...", color = Color.Gray)
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                Button(
                                    onClick = { musicViewModel.fetchByGenre("Top Hits") },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                                ) {
                                    Text("Force Refresh", color = Color.Black)
                                }
                        }
                } else {
                        VerticalPager(
                            state = pagerState, 
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                                ForYouPageItem(
                                        song = displaySongs[page],
                                        musicViewModel = musicViewModel,
                                        onSongClick = onSongClick
                                )
                        }
                }
        }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ForYouPageItem(
        song: Music,
        musicViewModel: com.example.flymusicai.viewmodel.MusicViewModel,
        onSongClick: (Music) -> Unit
) {
        val isCurrentPlaying by musicViewModel.isPlaying.collectAsState()
        val currentSong by musicViewModel.currentSong.collectAsState()
        val lyrics by musicViewModel.currentLyrics.collectAsState()
        val isThisSongActive = currentSong?.id == song.id

        val isPlaying = isThisSongActive && isCurrentPlaying

        var showHeartAnimation by remember { mutableStateOf(false) }
        var showLyricsOverlay by remember { mutableStateOf(false) }
        val isFavoriteFlow by musicViewModel.isFavorite(song.id).collectAsState(initial = song.isFavorite)
        var isLiked by remember { mutableStateOf(song.isFavorite) }
        
        LaunchedEffect(isFavoriteFlow) {
            isLiked = isFavoriteFlow
        }

        val context = androidx.compose.ui.platform.LocalContext.current

        val infiniteTransition = rememberInfiniteTransition(label = "disc_rotation")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(animation = tween(8000, easing = LinearEasing)),
            label = "rotation"
        )

        val currentPosition by musicViewModel.currentPosition.collectAsState()
        val currentProgress = if (isThisSongActive) currentPosition else 0f
        
        val currentSeconds = if (isThisSongActive) (currentProgress * song.duration).toInt() else 0
        val currentLyric = remember(currentSeconds, lyrics) {
            lyrics.findLast { it.timestamp <= currentSeconds }?.text ?: ""
        }

        LaunchedEffect(showHeartAnimation) {
                if (showHeartAnimation) {
                        delay(1000)
                        showHeartAnimation = false
                }
        }

        Box(
                modifier =
                        Modifier.fillMaxSize().pointerInput(Unit) {
                                detectTapGestures(
                                        onDoubleTap = {
                                                isLiked = true
                                                showHeartAnimation = true
                                                musicViewModel.toggleFavorite(song)
                                        },
                                        onTap = {
                                                if (isThisSongActive) {
                                                        musicViewModel.togglePlayPause()
                                                } else {
                                                        musicViewModel.playSongById(song.id)
                                                }
                                        }
                                )
                        }
        ) {
            // Background
            AsyncImage(
                model = song.coverImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(com.example.flymusicai.R.drawable.music_placeholder),
                error = painterResource(com.example.flymusicai.R.drawable.music_placeholder)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Black.copy(alpha = 0.6f),
                                Color.Black.copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            // Disc
            Box(
                modifier = Modifier.align(Alignment.Center),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .graphicsLayer {
                            rotationZ = if (isPlaying) rotation else 0f
                        }
                        .clip(CircleShape)
                        .background(Color.Black)
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = song.coverImageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        placeholder = painterResource(com.example.flymusicai.R.drawable.music_placeholder),
                        error = painterResource(com.example.flymusicai.R.drawable.music_placeholder)
                    )
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(Color(0xFF1A1A1A))
                    )
                }
            }

            // Heart Animation
            AnimatedVisibility(
                visible = showHeartAnimation,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(100.dp)
                )
            }

            // Floating Live Lyrics (Tappable)
            AnimatedVisibility(
                visible = currentLyric.isNotEmpty() && !showLyricsOverlay,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 20 }),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
                    .clickable { showLyricsOverlay = true }
            ) {
                Text(
                    text = currentLyric,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }

            // Right Actions
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ActionIcon(
                    icon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    label = "",
                    tint = if (isLiked) AmberGold else Color.White,
                    onClick = {
                        musicViewModel.toggleFavorite(song)
                        isLiked = !isLiked
                        if (isLiked) showHeartAnimation = true
                    }
                )

                // Lyrics Button
                ActionIcon(
                    icon = Icons.Default.Lyrics,
                    label = "",
                    tint = if (showLyricsOverlay) AmberGold else Color.White,
                    onClick = { showLyricsOverlay = !showLyricsOverlay }
                )

                ActionIcon(
                    icon = Icons.Default.PlaylistAdd,
                    label = "",
                    onClick = {
                        android.widget.Toast.makeText(context, "Added to Play Queue", android.widget.Toast.LENGTH_SHORT).show()
                    }
                )

                ActionIcon(
                    icon = Icons.Outlined.Share,
                    label = "",
                    onClick = {
                        val shareIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, "Check out ${song.title}!")
                            type = "text/plain"
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share"))
                    }
                )
            }

                    // Bottom Info
                    Row(
                        modifier =
                            Modifier.align(Alignment.BottomStart)
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.basicMarquee()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = song.artist,
                                style = MaterialTheme.typography.bodyMedium,
                                color = AmberGold.copy(alpha = 0.9f)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (isThisSongActive) musicViewModel.togglePlayPause()
                                else musicViewModel.playSongById(song.id)
                            },
                            modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Progress Bar
                    if (isThisSongActive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .fillMaxWidth(currentProgress)
                                .height(2.dp)
                                .background(AmberGold)
                        )
                    }

                    // Lyrics Overlay
                    AnimatedVisibility(
                        visible = showLyricsOverlay,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.85f))
                                .clickable { showLyricsOverlay = false }
                        ) {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Lyrics", color = AmberGold, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { showLyricsOverlay = false }) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                                    }
                                }

                                if (lyrics.isEmpty() || !isThisSongActive) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Lyrics will load when the song plays", color = Color.Gray)
                                    }
                                } else {
                                    val currentSeconds = (currentPosition * (song.duration)).toInt()
                                    val currentLyricIndex = lyrics.indexOfLast { it.timestamp <= currentSeconds }
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
                                                fontSize = if (isActive) 24.sp else 18.sp,
                                                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Bold,
                                                color = if (isActive) Color.White else Color.White.copy(alpha = 0.4f),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
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
fun ActionIcon(
        icon: ImageVector,
        label: String,
        tint: Color = Color.White,
        onClick: () -> Unit = {}
) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                        onClick = onClick,
                        modifier =
                                Modifier.size(44.dp)
                                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                        Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = tint,
                                modifier = Modifier.size(22.dp)
                        )
                }
                if (label.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontSize = 10.sp
                        )
                }
        }
}
