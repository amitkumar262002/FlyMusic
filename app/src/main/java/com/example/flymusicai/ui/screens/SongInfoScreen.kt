package com.example.flymusicai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
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

/** 📀 Song Info Screen - Detailed information about song and artist */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongInfoScreen(
        song: Music,
        relatedSongs: List<Music> = emptyList(),
        onBack: () -> Unit,
        onSongClick: (Music) -> Unit,
        onArtistClick: () -> Unit,
        onAlbumClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("Info", "Credits", "Related")

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = { Text("Song Details", color = Color.White) },
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
                                        IconButton(onClick = { /* Share */}) {
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
                LazyColumn(modifier = modifier.fillMaxSize().padding(paddingValues)) {
                        // Hero Section with Album Art
                        item {
                                Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                                        // Blurred Background
                                        AsyncImage(
                                                model = song.coverImageUrl,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize().blur(50.dp),
                                                contentScale = ContentScale.Crop,
                                                alpha = 0.3f
                                        )

                                        // Gradient Overlay
                                        Box(
                                                modifier =
                                                        Modifier.fillMaxSize()
                                                                .background(
                                                                        Brush.verticalGradient(
                                                                                colors =
                                                                                        listOf(
                                                                                                Color.Transparent,
                                                                                                NavyBlue.copy(
                                                                                                        alpha =
                                                                                                                0.7f
                                                                                                ),
                                                                                                NavyBlue
                                                                                        )
                                                                        )
                                                                )
                                        )

                                        // Album Art
                                        Column(
                                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                        ) {
                                                AsyncImage(
                                                        model = song.coverImageUrl,
                                                        contentDescription = song.title,
                                                        modifier =
                                                                Modifier.size(180.dp)
                                                                        .clip(
                                                                                RoundedCornerShape(
                                                                                        20.dp
                                                                                )
                                                                        ),
                                                        contentScale = ContentScale.Crop
                                                )

                                                Spacer(modifier = Modifier.height(16.dp))

                                                Text(
                                                        text = song.title,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .headlineSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        maxLines = 2,
                                                        overflow = TextOverflow.Ellipsis
                                                )

                                                Text(
                                                        text = song.artist,
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        color = GoldAccent,
                                                        maxLines = 1
                                                )
                                        }
                                }
                        }

                        // Quick Stats
                        item {
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                horizontal = 24.dp,
                                                                vertical = 16.dp
                                                        ),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                        StatItem(
                                                icon = Icons.Default.PlayArrow,
                                                value = "2.5M",
                                                label = "Plays"
                                        )
                                        StatItem(
                                                icon = Icons.Default.Favorite,
                                                value = "150K",
                                                label = "Likes"
                                        )
                                        StatItem(
                                                icon = Icons.Default.Timer,
                                                value = formatDuration(song.duration),
                                                label = "Duration"
                                        )
                                }
                        }

                        // Tabs
                        item {
                                ScrollableTabRow(
                                        selectedTabIndex = selectedTab,
                                        containerColor = NavyBlue,
                                        contentColor = GoldAccent,
                                        edgePadding = 16.dp,
                                        indicator = { tabPositions ->
                                                if (selectedTab < tabPositions.size) {
                                                        TabRowDefaults.SecondaryIndicator(
                                                                modifier =
                                                                        Modifier.tabIndicatorOffset(
                                                                                tabPositions[
                                                                                        selectedTab]
                                                                        ),
                                                                color = GoldAccent
                                                        )
                                                }
                                        }
                                ) {
                                        tabs.forEachIndexed { index, title ->
                                                Tab(
                                                        selected = selectedTab == index,
                                                        onClick = { selectedTab = index },
                                                        text = {
                                                                Text(
                                                                        text = title,
                                                                        fontWeight =
                                                                                if (selectedTab ==
                                                                                                index
                                                                                )
                                                                                        FontWeight
                                                                                                .Bold
                                                                                else
                                                                                        FontWeight
                                                                                                .Normal,
                                                                        color =
                                                                                if (selectedTab ==
                                                                                                index
                                                                                )
                                                                                        GoldAccent
                                                                                else
                                                                                        Color.White
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.6f
                                                                                                )
                                                                )
                                                        }
                                                )
                                        }
                                }
                        }

                        // Tab Content
                        when (selectedTab) {
                                0 -> {
                                        // Info Tab
                                        item { InfoSection(song, onArtistClick, onAlbumClick) }
                                }
                                1 -> {
                                        // Credits Tab
                                        item { CreditsSection(song) }
                                }
                                2 -> {
                                        // Related Tab
                                        item { RelatedSongsSection(relatedSongs, onSongClick) }
                                }
                        }

                        // Bottom padding
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                }
        }
}

@Composable
private fun StatItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        value: String,
        label: String
) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
                Icon(
                        icon,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(24.dp)
                )
                Text(
                        text = value,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )
                Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f)
                )
        }
}

@Composable
private fun InfoSection(song: Music, onArtistClick: () -> Unit, onAlbumClick: () -> Unit) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Text(
                        "About This Song",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )

                // Artist Info Card
                Card(
                        modifier = Modifier.fillMaxWidth().clickable(onClick = onArtistClick),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkNavy)
                ) {
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                // Artist Avatar
                                Box(
                                        modifier =
                                                Modifier.size(60.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                                Brush.linearGradient(
                                                                        colors =
                                                                                listOf(
                                                                                        TealAccent,
                                                                                        GoldAccent
                                                                                )
                                                                )
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(32.dp),
                                                tint = NavyBlue
                                        )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                "Artist",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.6f)
                                        )
                                        Text(
                                                song.artist,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                                        Text(
                                                "2.5M Monthly Listeners",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = GoldAccent
                                        )
                                }

                                Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = "View Artist",
                                        tint = Color.White.copy(alpha = 0.6f)
                                )
                        }
                }

                // Album Info Card
                Card(
                        modifier = Modifier.fillMaxWidth().clickable(onClick = onAlbumClick),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkNavy)
                ) {
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                // Album Art
                                AsyncImage(
                                        model = song.coverImageUrl,
                                        contentDescription = song.album,
                                        modifier =
                                                Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                "Album",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White.copy(alpha = 0.6f)
                                        )
                                        Text(
                                                song.album,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                                        Text(
                                                "2024 • 12 songs",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.6f)
                                        )
                                }

                                Icon(
                                        Icons.Default.ChevronRight,
                                        contentDescription = "View Album",
                                        tint = Color.White.copy(alpha = 0.6f)
                                )
                        }
                }

                // Details
                InfoRow("Genre", song.genre)
                InfoRow("Release Date", "2024")
                InfoRow("Label", "Fly Music Records")
                InfoRow("Copyright", "℗ 2024 Fly Music FlyAI")
        }
}

@Composable
private fun InfoRow(label: String, value: String) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                        label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                        value,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                )
        }
}

@Composable
private fun CreditsSection(song: Music) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                Text(
                        "Production Credits",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )

                CreditItem("Artist", song.artist, Icons.Default.MicExternalOn)
                CreditItem("Producer", "Amit Kumar", Icons.Default.Settings)
                CreditItem("Composer", song.artist, Icons.Default.MusicNote)
                CreditItem("Lyricist", "Renuka Panwar", Icons.Default.EditNote)
                CreditItem("Mixing Engineer", "Sound Studio Pro", Icons.Default.Tune)
                CreditItem("Mastering", "Master Audio Labs", Icons.Default.HighQuality)
        }
}

@Composable
private fun CreditItem(
        role: String,
        name: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector
) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                Box(
                        modifier =
                                Modifier.size(40.dp)
                                        .clip(CircleShape)
                                        .background(GoldAccent.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                ) {
                        Icon(
                                icon,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = GoldAccent
                        )
                }

                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                role,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                                name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                        )
                }
        }
}

@Composable
private fun RelatedSongsSection(songs: List<Music>, onSongClick: (Music) -> Unit) {
        Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Text(
                        "You Might Also Like",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                )

                if (songs.isEmpty()) {
                        // Sample related songs if none provided
                        val sampleSongs =
                                listOf(
                                        Music(
                                                "1",
                                                "Sapna Jahan",
                                                "Neha Kakkar",
                                                "Hits 2024",
                                                210,
                                                ""
                                        ),
                                        Music(
                                                "2",
                                                "Dil Diyan Gallan",
                                                "Atif Aslam",
                                                "Love Songs",
                                                195,
                                                ""
                                        ),
                                        Music("3", "Tere Liye", "Arijit Singh", "Romantic", 200, "")
                                )

                        sampleSongs.forEach { song -> RelatedSongItem(song, onSongClick) }
                } else {
                        songs.forEach { song -> RelatedSongItem(song, onSongClick) }
                }
        }
}

@Composable
private fun RelatedSongItem(song: Music, onClick: (Music) -> Unit) {
        Card(
                modifier = Modifier.fillMaxWidth().clickable { onClick(song) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavy)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        // Album Art
                        Box(
                                modifier =
                                        Modifier.size(50.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                        Brush.linearGradient(
                                                                colors =
                                                                        listOf(
                                                                                TealAccent,
                                                                                GoldAccent
                                                                        )
                                                        )
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                )
                        }

                        // Song Info
                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        song.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                        song.artist,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.6f),
                                        maxLines = 1
                                )
                        }

                        // Play Button
                        IconButton(onClick = { onClick(song) }) {
                                Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = GoldAccent
                                )
                        }
                }
        }
}

private fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%d:%02d", mins, secs)
}
