package com.example.flymusicai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.R
import com.example.flymusicai.data.PlaylistCategory
import com.example.flymusicai.ui.components.MusicCard
import com.example.flymusicai.ui.components.PlaylistCard
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

/**
 * 🏠 Premium Home Screen - Advanced Professional Design Features: Glassmorphism, gradient
 * backgrounds, smooth animations, modern layouts
 */
@Composable
fun HomeScreen(
        musicViewModel: MusicViewModel,
        onSearchClick: () -> Unit,
        onSongClick: (String) -> Unit,
        onPlaylistClick: (String) -> Unit
) {
        val songs by musicViewModel.songs.collectAsState()
        val ringtones by musicViewModel.ringtones.collectAsState()
        val playlists by musicViewModel.playlists.collectAsState()
        val recommendedPlaylist by musicViewModel.recommendedPlaylist.collectAsState()
        val trendingMusic = remember(songs) { musicViewModel.getTrendingMusic() }

        val listState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 2 } }

        // Animated gradient offset
        val infiniteTransition = rememberInfiniteTransition(label = "bg")
        val gradientOffset by
                infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1000f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(10000, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "gradient"
                )

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                // Animated gradient background
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(
                                                brush =
                                                        Brush.radialGradient(
                                                                colors =
                                                                        listOf(
                                                                                AmberGold.copy(
                                                                                        alpha = 0.1f
                                                                                ),
                                                                                Color.Transparent,
                                                                                OrangeVibrant.copy(
                                                                                        alpha = 0.1f
                                                                                ),
                                                                                Color.Transparent
                                                                        ),
                                                                center =
                                                                        androidx.compose.ui.geometry
                                                                                .Offset(
                                                                                        x =
                                                                                                gradientOffset,
                                                                                        y = 500f
                                                                                ),
                                                                radius = 1200f
                                                        )
                                        )
                )

                LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                        // Premium Header
                        item { PremiumHeader(onSearchClick = onSearchClick) }

                        // Quick Stats Card
                        item {
                                QuickStatsCard(
                                        totalSongs = songs.size,
                                        totalPlaylists = playlists.size
                                )
                        }

                        // Recommended for You (AI-powered)
                        recommendedPlaylist?.let { playlist ->
                                item {
                                        SectionHeader(
                                                title = "Recommended for You",
                                                icon = "🤖",
                                                subtitle = "AI-powered selections"
                                        )
                                }

                                item {
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                items(playlist.songs.take(10)) { music ->
                                                        MusicCard(
                                                                music = music,
                                                                onClick = { onSongClick(music.id) },
                                                                isFavorite =
                                                                        musicViewModel.isFavorite(
                                                                                music.id
                                                                        ),
                                                                onFavoriteClick = {
                                                                        musicViewModel
                                                                                .toggleFavorite(
                                                                                        music
                                                                                )
                                                                }
                                                        )
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                }
                        }

                        // Top Trending
                        item {
                                SectionHeader(
                                        title = "Top Trending",
                                        icon = "🔥",
                                        subtitle = "Hot tracks right now"
                                )
                        }

                        item {
                                LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        items(trendingMusic) { music ->
                                                MusicCard(
                                                        music = music,
                                                        onClick = { onSongClick(music.id) },
                                                        isFavorite =
                                                                musicViewModel.isFavorite(music.id),
                                                        onFavoriteClick = {
                                                                musicViewModel.toggleFavorite(music)
                                                        }
                                                )
                                        }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Editor's Picks
                        item {
                                SectionHeader(
                                        title = "Editor's Picks",
                                        icon = "⭐",
                                        subtitle = "Handpicked collections"
                                )
                        }

                        item {
                                val editorPicks =
                                        playlists.filter {
                                                it.category == PlaylistCategory.EDITORS_PICK
                                        }
                                LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        items(editorPicks) { playlist ->
                                                PlaylistCard(
                                                        playlist = playlist,
                                                        onClick = { onPlaylistClick(playlist.id) }
                                                )
                                        }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Ringtones Section
                        if (ringtones.isNotEmpty()) {
                                item {
                                        SectionHeader(
                                                title = "Ringtones",
                                                icon = "🔔",
                                                subtitle = "Perfect for your calls"
                                        )
                                }

                                item {
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                items(ringtones.take(15)) { music ->
                                                        MusicCard(
                                                                music = music,
                                                                onClick = { onSongClick(music.id) },
                                                                isFavorite =
                                                                        musicViewModel.isFavorite(
                                                                                music.id
                                                                        ),
                                                                onFavoriteClick = {
                                                                        musicViewModel
                                                                                .toggleFavorite(
                                                                                        music
                                                                                )
                                                                }
                                                        )
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                }
                        }

                        // Browse Playlists
                        item {
                                SectionHeader(
                                        title = "Browse Playlists",
                                        icon = "📚",
                                        subtitle = "Curated for every mood"
                                )
                        }

                        item {
                                LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                        items(playlists) { playlist ->
                                                PlaylistCard(
                                                        playlist = playlist,
                                                        onClick = { onPlaylistClick(playlist.id) }
                                                )
                                        }
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                        }
                }

                // Premium Scroll to Top FAB
                androidx.compose.animation.AnimatedVisibility(
                        visible = showScrollToTop,
                        modifier =
                                Modifier.align(Alignment.BottomEnd)
                                        .padding(end = 16.dp, bottom = 120.dp),
                        enter =
                                androidx.compose.animation.fadeIn() +
                                        androidx.compose.animation.scaleIn(),
                        exit =
                                androidx.compose.animation.fadeOut() +
                                        androidx.compose.animation.scaleOut()
                ) {
                        FloatingActionButton(
                                onClick = {
                                        coroutineScope.launch { listState.animateScrollToItem(0) }
                                },
                                modifier =
                                        Modifier.border(
                                                1.dp,
                                                AmberGold.copy(alpha = 0.5f),
                                                CircleShape
                                        ),
                                containerColor = NavySurface,
                                contentColor = AmberGold,
                                shape = CircleShape,
                                elevation =
                                        FloatingActionButtonDefaults.elevation(
                                                defaultElevation = 8.dp,
                                                pressedElevation = 12.dp
                                        )
                        ) {
                                Icon(
                                        imageVector = Icons.Default.KeyboardArrowUp,
                                        contentDescription = "Scroll to Top",
                                        tint = AmberGold
                                )
                        }
                }
        }
}

/** Premium Header with glassmorphism and gradient */
@Composable
private fun PremiumHeader(onSearchClick: () -> Unit) {
        Box(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(
                                        brush =
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        SurfaceDark.copy(
                                                                                alpha = 0.9f
                                                                        ),
                                                                        BackgroundDark.copy(
                                                                                alpha = 0.7f
                                                                        ),
                                                                        Color.Transparent
                                                                )
                                                )
                                )
                                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
                Column {
                        // Logo and Welcome
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                                // Premium Gold Winged Logo
                                Box(
                                        modifier =
                                                Modifier.size(64.dp)
                                                        .clip(CircleShape)
                                                        .background(NavySurface)
                                                        .border(
                                                                1.dp,
                                                                AmberGold.copy(alpha = 0.3f),
                                                                CircleShape
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Image(
                                                painter =
                                                        painterResource(
                                                                id = R.drawable.fly_music_logo
                                                        ),
                                                contentDescription = "Fly Music Logo",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                        )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = "Welcome to",
                                                fontSize = 14.sp,
                                                color = TextSecondary,
                                                fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                                text = "FLY MUSIC",
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = TextPrimary,
                                                letterSpacing = 1.sp
                                        )
                                }

                                // Notification Icon
                                IconButton(onClick = { /* TODO: Notifications */}) {
                                        Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = "Notifications",
                                                tint = TextSecondary
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Premium Search Bar
                        Card(
                                modifier = Modifier.fillMaxWidth().clickable { onSearchClick() },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CardDark),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = 20.dp,
                                                                vertical = 16.dp
                                                        ),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.Search,
                                                contentDescription = "Search",
                                                tint = PrimaryPurple,
                                                modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                                text = "What do you want to play?",
                                                color = TextSecondary,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium
                                        )
                                }
                        }
                }
        }
}

/** Quick Stats Card - Shows library statistics */
@Composable
private fun QuickStatsCard(totalSongs: Int, totalPlaylists: Int) {
        Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardDark),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
                Row(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(
                                                brush =
                                                        Brush.horizontalGradient(
                                                                colors =
                                                                        listOf(
                                                                                PrimaryPurple.copy(
                                                                                        alpha =
                                                                                                0.15f
                                                                                ),
                                                                                PrimaryCyan.copy(
                                                                                        alpha =
                                                                                                0.15f
                                                                                )
                                                                        )
                                                        )
                                        )
                                        .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                        StatItem(
                                icon = Icons.Default.MusicNote,
                                value = totalSongs.toString(),
                                label = "Songs",
                                color = PrimaryPurple
                        )

                        Box(
                                modifier =
                                        Modifier.width(1.dp)
                                                .height(50.dp)
                                                .background(TextSecondary.copy(alpha = 0.3f))
                        )

                        StatItem(
                                icon = Icons.Default.PlaylistPlay,
                                value = totalPlaylists.toString(),
                                label = "Playlists",
                                color = PrimaryCyan
                        )

                        Box(
                                modifier =
                                        Modifier.width(1.dp)
                                                .height(50.dp)
                                                .background(TextSecondary.copy(alpha = 0.3f))
                        )

                        StatItem(
                                icon = Icons.Default.Favorite,
                                value = "Mix",
                                label = "Genres",
                                color = AccentMagenta
                        )
                }
        }
}

/** Individual stat item */
@Composable
private fun StatItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        value: String,
        label: String,
        color: Color
) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = value,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                )
                Text(
                        text = label,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                )
        }
}

/** Premium Section Header with icon and subtitle */
@Composable
private fun SectionHeader(title: String, icon: String, subtitle: String? = null) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = icon, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                                Text(
                                        text = title,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextPrimary,
                                        letterSpacing = 0.5.sp
                                )
                                if (subtitle != null) {
                                        Text(
                                                text = subtitle,
                                                fontSize = 13.sp,
                                                color = TextSecondary,
                                                fontWeight = FontWeight.Medium
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Gradient underline
                Box(
                        modifier =
                                Modifier.width(100.dp)
                                        .height(3.dp)
                                        .background(
                                                brush =
                                                        Brush.horizontalGradient(
                                                                colors =
                                                                        listOf(
                                                                                PrimaryPurple,
                                                                                PrimaryCyan
                                                                        )
                                                        ),
                                                shape = RoundedCornerShape(2.dp)
                                        )
                )
        }
}
