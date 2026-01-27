package com.example.flymusicai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.data.Music
import com.example.flymusicai.ui.theme.*
import kotlinx.coroutines.launch

data class LyricLine(
        val timestamp: Int, // in seconds
        val text: String
)

/** 🎤 Lyrics Screen - Display synchronized lyrics with playback */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsScreen(
        song: Music,
        currentPosition: Float, // 0f to 1f
        isPlaying: Boolean,
        onBack: () -> Unit,
        onShare: () -> Unit,
        modifier: Modifier = Modifier
) {
        // Sample lyrics - in production, fetch from API
        val lyrics = remember {
                listOf(
                        LyricLine(0, "🎵"),
                        LyricLine(5, "Yanta feat. Nawazuddin Siddiqui"),
                        LyricLine(10, "Renuka Panwar, Raja"),
                        LyricLine(15, ""),
                        LyricLine(20, "Verse 1:"),
                        LyricLine(25, "Dil ki baat sunlo zara"),
                        LyricLine(30, "Meri jaan ho tum yara"),
                        LyricLine(35, "Har pal har ghadi"),
                        LyricLine(40, "Tere sath hoon main khadi"),
                        LyricLine(45, ""),
                        LyricLine(50, "Chorus:"),
                        LyricLine(55, "Yanta re yanta"),
                        LyricLine(60, "Dil ka yeh dhanta"),
                        LyricLine(65, "Pyar ho gaya hai"),
                        LyricLine(70, "Sapna sa yeh"),
                        LyricLine(75, ""),
                        LyricLine(80, "Verse 2:"),
                        LyricLine(85, "Raatein ho ya din"),
                        LyricLine(90, "Baatein teri yaad mein"),
                        LyricLine(95, "Door ho ya paas"),
                        LyricLine(100, "Rehte ho tum mere aas"),
                        LyricLine(105, ""),
                        LyricLine(110, "Chorus:"),
                        LyricLine(115, "Yanta re yanta"),
                        LyricLine(120, "Dil ka yeh dhanta"),
                        LyricLine(125, "Pyar ho gaya hai"),
                        LyricLine(130, "Sapna sa yeh"),
                        LyricLine(135, ""),
                        LyricLine(140, "Bridge:"),
                        LyricLine(145, "Tere bina adhoora"),
                        LyricLine(150, "Lagta hai jahan poora"),
                        LyricLine(155, "Tu hai meri duniya"),
                        LyricLine(160, "Mera pyaar tu hai sach"),
                        LyricLine(165, ""),
                        LyricLine(170, "Final Chorus:"),
                        LyricLine(175, "Yanta re yanta"),
                        LyricLine(180, "Dil ka yeh dhanta"),
                        LyricLine(185, "Pyar ho gaya hai"),
                        LyricLine(190, "Sapna sa yeh"),
                        LyricLine(195, "Ho... yanta re yanta"),
                        LyricLine(200, "🎵")
                )
        }

        val currentSeconds = (currentPosition * (song.duration)).toInt()
        val currentLyricIndex = lyrics.indexOfLast { it.timestamp <= currentSeconds }

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()

        var displayMode by remember { mutableStateOf("scroll") } // "scroll" or "static"
        var fontSize by remember { mutableStateOf(18) }

        // Auto-scroll to current lyric
        LaunchedEffect(currentLyricIndex) {
                if (displayMode == "scroll" && currentLyricIndex >= 0) {
                        coroutineScope.launch {
                                listState.animateScrollToItem(
                                        index = maxOf(0, currentLyricIndex - 1)
                                )
                        }
                }
        }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Column {
                                                Text(
                                                        "Lyrics",
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                )
                                                Text(
                                                        song.title,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.White.copy(alpha = 0.7f),
                                                        maxLines = 1
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
                                        // Font Size
                                        IconButton(
                                                onClick = {
                                                        fontSize =
                                                                if (fontSize >= 24) 16
                                                                else fontSize + 2
                                                }
                                        ) {
                                                Icon(
                                                        Icons.Default.FormatSize,
                                                        contentDescription = "Font Size",
                                                        tint = Color.White
                                                )
                                        }

                                        // Display Mode
                                        IconButton(
                                                onClick = {
                                                        displayMode =
                                                                if (displayMode == "scroll")
                                                                        "static"
                                                                else "scroll"
                                                }
                                        ) {
                                                Icon(
                                                        if (displayMode == "scroll")
                                                                Icons.Default.AutoMode
                                                        else Icons.Default.ViewList,
                                                        contentDescription = "Display Mode",
                                                        tint =
                                                                if (displayMode == "scroll")
                                                                        GoldAccent
                                                                else Color.White
                                                )
                                        }

                                        // Share Lyrics
                                        IconButton(onClick = onShare) {
                                                Icon(
                                                        Icons.Default.Share,
                                                        contentDescription = "Share",
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
                        Column(modifier = Modifier.fillMaxSize()) {
                                // Song Info Card
                                Card(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor = Color.Transparent
                                                )
                                ) {
                                        Box(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .background(
                                                                        Brush.horizontalGradient(
                                                                                colors =
                                                                                        listOf(
                                                                                                TealAccent
                                                                                                        .copy(
                                                                                                                alpha =
                                                                                                                        0.2f
                                                                                                        ),
                                                                                                GoldAccent
                                                                                                        .copy(
                                                                                                                alpha =
                                                                                                                        0.2f
                                                                                                        )
                                                                                        )
                                                                        )
                                                                )
                                                                .padding(16.dp)
                                        ) {
                                                Row(
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(16.dp)
                                                ) {
                                                        // Album Art
                                                        AsyncImage(
                                                                model = song.coverImageUrl,
                                                                contentDescription = song.title,
                                                                modifier =
                                                                        Modifier.size(60.dp)
                                                                                .clip(
                                                                                        RoundedCornerShape(
                                                                                                12.dp
                                                                                        )
                                                                                )
                                                        )

                                                        // Song Details
                                                        Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                        text = song.title,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium,
                                                                        fontWeight =
                                                                                FontWeight.Bold,
                                                                        color = Color.White,
                                                                        maxLines = 1
                                                                )

                                                                Text(
                                                                        text = song.artist,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        color =
                                                                                Color.White.copy(
                                                                                        alpha = 0.7f
                                                                                ),
                                                                        maxLines = 1
                                                                )
                                                        }

                                                        // Playing Status
                                                        if (isPlaying) {
                                                                Icon(
                                                                        Icons.Default.GraphicEq,
                                                                        contentDescription =
                                                                                "Playing",
                                                                        modifier =
                                                                                Modifier.size(
                                                                                        32.dp
                                                                                ),
                                                                        tint = GoldAccent
                                                                )
                                                        }
                                                }
                                        }
                                }

                                // Lyrics Display
                                LazyColumn(
                                        state = listState,
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding =
                                                PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                        itemsIndexed(lyrics) { index, lyric ->
                                                val isCurrentLine = index == currentLyricIndex
                                                val isPastLine = index < currentLyricIndex

                                                AnimatedVisibility(
                                                        visible = true,
                                                        enter = fadeIn() + expandVertically(),
                                                        exit = fadeOut() + shrinkVertically()
                                                ) {
                                                        LyricLineItem(
                                                                text = lyric.text,
                                                                isCurrent = isCurrentLine,
                                                                isPast = isPastLine,
                                                                fontSize = fontSize,
                                                                displayMode = displayMode
                                                        )
                                                }
                                        }

                                        // Bottom padding
                                        item { Spacer(modifier = Modifier.height(200.dp)) }
                                }
                        }

                        // Scroll Indicator
                        if (displayMode == "scroll") {
                                Box(
                                        modifier =
                                                Modifier.align(Alignment.CenterEnd)
                                                        .padding(end = 8.dp)
                                                        .width(4.dp)
                                                        .height(100.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(
                                                                Brush.verticalGradient(
                                                                        colors =
                                                                                listOf(
                                                                                        Color.Transparent,
                                                                                        GoldAccent
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.5f
                                                                                                ),
                                                                                        GoldAccent,
                                                                                        GoldAccent
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.5f
                                                                                                ),
                                                                                        Color.Transparent
                                                                                )
                                                                )
                                                        )
                                )
                        }
                }
        }
}

@Composable
private fun LyricLineItem(
        text: String,
        isCurrent: Boolean,
        isPast: Boolean,
        fontSize: Int,
        displayMode: String
) {
        val textColor =
                when {
                        isCurrent -> Color.White
                        isPast -> Color.White.copy(alpha = 0.4f)
                        else -> Color.White.copy(alpha = 0.5f)
                }

        val weight =
                when {
                        isCurrent -> FontWeight.ExtraBold
                        else -> FontWeight.Normal
                }

        val scale =
                when {
                        isCurrent && displayMode == "scroll" -> 1.1f
                        else -> 1f
                }

        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .then(
                                        if (isCurrent && displayMode == "scroll") {
                                                Modifier.background(
                                                        Brush.horizontalGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color.Transparent,
                                                                                GoldAccent.copy(
                                                                                        alpha = 0.1f
                                                                                ),
                                                                                Color.Transparent
                                                                        )
                                                        ),
                                                        shape = RoundedCornerShape(8.dp)
                                                )
                                        } else Modifier
                                )
                                .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
        ) {
                if (text == "🎵") {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size((fontSize * 1.5).dp)
                    )
                } else {
                    Text(
                            text = text,
                            fontSize = (fontSize * scale).sp,
                            fontWeight = weight,
                            color = textColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            lineHeight = (fontSize * 1.5).sp
                    )
                }
        }
}
