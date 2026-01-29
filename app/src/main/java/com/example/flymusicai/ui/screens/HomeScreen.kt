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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.R
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
        onPlaylistClick: (String) -> Unit,
        onArtistClick: (String) -> Unit,
        onNavigateToSettings: () -> Unit
) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val songs by musicViewModel.songs.collectAsState()
        val playlists by musicViewModel.playlists.collectAsState()
        val trendingMusic = remember(songs) { musicViewModel.getTrendingMusic() }

        val listState = rememberLazyListState()
        val showScrollToTop by remember { derivedStateOf { listState.firstVisibleItemIndex > 2 } }
        val coroutineScope = rememberCoroutineScope()

        Box(modifier = Modifier.fillMaxSize().background(DeepNavy)) {
                // Subtle Gradient Background
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        Color(
                                                                                0xFF16253D
                                                                        ), // Slightly lighter navy
                                                                        DeepNavy,
                                                                        Color.Black
                                                                )
                                                )
                                        )
                )

                var selectedCategory by remember { mutableStateOf("All") }
                val categories =
                        listOf(
                                "All",
                                "Singers",
                                "Romance",
                                "Top Releases",
                                "Bollywood",
                                "Punjabi",
                                "Pop",
                                "Party",
                                "Workout"
                        )

                // Collect states at the top level
                val recentlyPlayed by musicViewModel.recentlyPlayed.collectAsState()
                val genreSongs by musicViewModel.genreSongs.collectAsState()
                val indiaRising by musicViewModel.indiaRising.collectAsState()
                val romanceNow by musicViewModel.romanceNow.collectAsState()
                val bestOf90s by musicViewModel.bestOf90s.collectAsState()
                val hindiHits by musicViewModel.hindiHits.collectAsState()
                val albumsForYou by musicViewModel.albumsForYou.collectAsState()
                val popularSongs by musicViewModel.popularSongs.collectAsState()
                val playlists by musicViewModel.playlists.collectAsState()
                val charts by musicViewModel.charts.collectAsState()
                val moods by musicViewModel.moods.collectAsState()
                
                // AI Assistant State
                val aiResponse by musicViewModel.aiResponse.collectAsState()
                val isAILoading by musicViewModel.isAILoading.collectAsState()
                var showAIDialog by remember { mutableStateOf(false) }
                var aiQuery by remember { mutableStateOf("") }
                
                // Bottom Sheet State
                var selectedSongForOptions by remember { mutableStateOf<com.example.flymusicai.data.Music?>(null) }
                var showSongOptions by remember { mutableStateOf(false) }
                val favoriteSongs by musicViewModel.favoriteSongs.collectAsState()

                LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp) // Space for MiniPlayer
                ) {
                        // 1. Premium Header (Logo & Search)
                        item {
                                PremiumHeader(
                                        onSearchClick = onSearchClick,
                                        onSettingsClick = onNavigateToSettings,
                                        onAIClick = { showAIDialog = true }
                                )
                        }

                        // 2. Category Tabs
                        item {
                                CategoryTabsRow(
                                        categories = categories,
                                        selectedCategory = selectedCategory,
                                        onCategorySelected = { category ->
                                                selectedCategory = category
                                                when (category) {
                                                        "Singers" ->
                                                                musicViewModel.fetchByGenre(
                                                                        "Popular Artists"
                                                                )
                                                        "Romance" ->
                                                                musicViewModel.fetchByGenre(
                                                                        "Romance"
                                                                )
                                                        "Top Releases" ->
                                                                musicViewModel.fetchByGenre(
                                                                        "Trending"
                                                                )
                                                        "All" -> {}
                                                        else ->
                                                                musicViewModel.fetchByGenre(
                                                                        category
                                                                )
                                                }
                                        }
                                )
                        }





                        // 🎵 Popular Singers & Songs (Restored with Real Data)
                        if (selectedCategory == "All") {
                                item {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        com.example.flymusicai.ui.components.PopularArtistsSection(
                                                artists = com.example.flymusicai.data.IndianMusicDatabase.popularArtists,
                                                onArtistClick = { artist ->
                                                        onArtistClick(artist.name)
                                                }
                                        )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(24.dp))
                                    val displaySongs = remember(popularSongs) {
                                        if (popularSongs.isNotEmpty()) popularSongs else {
                                            com.example.flymusicai.data.IndianMusicDatabase.forYouSongs.map { dbSong ->
                                                val rawImageUrl = dbSong.imageUrl
                                                val finalImageUrl = if (rawImageUrl.contains("_I8I_I7")) {
                                                    "https://c.saavncdn.com/artists/${dbSong.artist.replace(" ", "_")}_500x500.jpg"
                                                } else rawImageUrl.replace("img.youtube.com", "i.ytimg.com")

                                                com.example.flymusicai.data.Music(
                                                    id = if (dbSong.id.startsWith("yt_")) dbSong.id else "yt_${dbSong.id}",
                                                    title = dbSong.title,
                                                    artist = dbSong.artist,
                                                    duration = 240, // Required parameter
                                                    coverImageUrl = finalImageUrl,
                                                    album = dbSong.album,
                                                    genre = dbSong.category.firstOrNull() ?: "Bollywood"
                                                )
                                            }
                                        }
                                    }
                                    com.example.flymusicai.ui.components.PopularSongsSection(
                                        songs = displaySongs,
                                        onSongClick = { song ->
                                            musicViewModel.playSong(song, displaySongs)
                                        }
                                    )
                                }
                        }

                        // 3. Recently Played (Horizontal Scroll) - Only show in "All"
                        if (selectedCategory == "All" && recentlyPlayed.isNotEmpty()) {
                                item {
                                        SectionHeader(title = "Recently Played")
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                items(recentlyPlayed) { song ->
                                                        StandardSongCard(
    song = song,
    onClick = { onSongClick(song.id) },
    onMoreClick = {
        selectedSongForOptions = song
        showSongOptions = true
    }
)
                                                }
                                        }
                                }
                        }


                        // New: India Rising Section with Album Cards
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            SectionHeader(
                                topTitle = "CELEBRATING THE REPUBLIC & ITS YOUNG VOICES",
                                title = "India Rising",
                                titleColor = AmberGold
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 📀 Dynamic Trending Songs (50+)
                                items(indiaRising) { song ->
                                    StandardSongCard(
                                        song = song,
                                        onClick = { musicViewModel.playSong(song, indiaRising) },
                                        onMoreClick = {
                                            selectedSongForOptions = song
                                            showSongOptions = true
                                        }
                                    )
                                }
                                
                                // 📀 Album-style cards for Indian music collections
                                items(playlists.filter {
                                    it.name.contains("India", ignoreCase = true) ||
                                    it.name.contains("Desi", ignoreCase = true) ||
                                    it.name.contains("Punjabi", ignoreCase = true) ||
                                    it.name.contains("Regional", ignoreCase = true)
                                }.take(10)) { album ->
                                    WidePlaylistCard(
                                        playlist = album,
                                        onClick = { onPlaylistClick(album.id) }
                                    )
                                }
                            }
                        }

                        // genreSongs section (Focus when category is selected)
                        if (selectedCategory != "All") {
                                item {
                                        SectionHeader(
                                                title = "$selectedCategory Special",
                                                titleColor = AmberGold,
                                                onViewAllClick = {
                                                        android.widget.Toast.makeText(
                                                                        context,
                                                                        "Explore all $selectedCategory content",
                                                                        android.widget.Toast
                                                                                .LENGTH_SHORT
                                                                )
                                                                .show()
                                                }
                                        )

                                        if (genreSongs.isEmpty()) {
                                                Box(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .height(150.dp),
                                                        contentAlignment = Alignment.Center
                                                ) { CircularProgressIndicator(color = AmberGold) }
                                        } else {
                                                LazyRow(
                                                        contentPadding =
                                                                PaddingValues(horizontal = 16.dp),
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(16.dp)
                                                ) {
                                                        items(genreSongs) { song ->
                                                                FeaturedCard(
                                                                        song = song,
                                                                        onClick = {
                                                                                onSongClick(song.id)
                                                                        }
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }

                        // New: Trending Community Playlists
                        if (selectedCategory == "All") {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(title = "Trending community playlists")
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                items(playlists.shuffled().take(50)) { playlist ->
                                                        ChartCard(
                                                                playlist = playlist,
                                                                onClick = {
                                                                        onPlaylistClick(playlist.id)
                                                                }
                                                        )
                                                }
                                        }
                                }
                        }

                        // 4. "Your Daily Mix" / Top Picks with Album Cards
                        item {
                                Spacer(modifier = Modifier.height(24.dp))
                                SectionHeader(
                                        topTitle = "MADE FOR YOU",
                                        title =
                                                if (selectedCategory == "All") "Top Picks For You"
                                                else "More $selectedCategory For You"
                                )

                                LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                        // Album-style cards for personalized picks
                                        items(playlists.shuffled().take(10)) { album ->
                                            WidePlaylistCard(
                                                playlist = album,
                                                onClick = { onPlaylistClick(album.id) }
                                            )
                                        }
                                }
                        }

                        // New: Romance Right Now Section with Album Cards
                        if (selectedCategory == "All" || selectedCategory == "Romance") {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(
                                                topTitle = "BACKGROUND SCORE TO YOUR LOVE STORY",
                                                title = "Romance Right Now",
                                                titleColor = AmberGold
                                        )
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                // Album-style cards for romantic collections
                                                items(playlists.filter { 
                                                    it.name.contains("Love", ignoreCase = true) || 
                                                    it.name.contains("Romance", ignoreCase = true) ||
                                                    it.name.contains("Heart", ignoreCase = true)
                                                }.take(10).ifEmpty { playlists.take(10) }) { album ->
                                                    WidePlaylistCard(
                                                        playlist = album,
                                                        onClick = { onPlaylistClick(album.id) }
                                                    )
                                                }
                                        }
                                }
                        }

                        // NEW: Albums for you Section
                        if (selectedCategory == "All") {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(
                                                title = "Albums for you",
                                                titleColor = AmberGold
                                        )
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                items(albumsForYou) { album ->
                                                        ChartCard(
                                                                playlist = album,
                                                                onClick = { onPlaylistClick(album.id) }
                                                        )
                                                }
                                        }
                                }
                        }

                        // 6. Top Charts (Real Dynamic Data)
                        if (selectedCategory == "All" && charts.isNotEmpty()) {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(topTitle = "GLOBAL IMPACT", title = "Global Charts")
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                items(charts) { song ->
                                                        StandardSongCard(
                                                                song = song,
                                                                onClick = { musicViewModel.playSong(song, charts) },
                                                                onMoreClick = {
                                                                    selectedSongForOptions = song
                                                                    showSongOptions = true
                                                                }
                                                        )
                                                }
                                        }
                                }
                        }

                        // NEW: Hindi Hits Section with Album Cards
                        if (selectedCategory == "All" || selectedCategory == "Bollywood") {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(
                                                topTitle = "TRENDING IN BOLLYWOOD",
                                                title = "Hindi Hits",
                                                titleColor = AmberGold,
                                                onViewAllClick = { onPlaylistClick("hindi_hits") }
                                        )
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                // Album-style cards for Hindi music collections
                                                items(playlists.filter { 
                                                    it.name.contains("Hindi", ignoreCase = true) || 
                                                    it.name.contains("Bollywood", ignoreCase = true) ||
                                                    it.name.contains("Desi", ignoreCase = true)
                                                }.take(10).ifEmpty { playlists.shuffled().take(10) }) { album ->
                                                    WidePlaylistCard(
                                                        playlist = album,
                                                        onClick = { onPlaylistClick(album.id) }
                                                    )
                                                }
                                        }
                                }
                        }

                        // 8. Top Releases with Album Cards
                        if (selectedCategory == "All" || selectedCategory == "Top Releases") {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(
                                                topTitle = "FRESH ARRIVALS",
                                                title = "Top Releases",
                                                titleColor = AmberGold
                                        )
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                // Album-style cards for new releases
                                                items(playlists.filter { 
                                                    it.name.contains("New", ignoreCase = true) || 
                                                    it.name.contains("Latest", ignoreCase = true) ||
                                                    it.name.contains("2024", ignoreCase = true) ||
                                                    it.name.contains("2025", ignoreCase = true)
                                                }.take(10).ifEmpty { playlists.reversed().take(10) }) { album ->
                                                    WidePlaylistCard(
                                                        playlist = album,
                                                        onClick = { onPlaylistClick(album.id) }
                                                    )
                                                }
                                        }
                                }
                        }

                        // 9. "Best of 90s" with Album Cards
                        if (selectedCategory == "All") {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(
                                                title = "Best of 90s",
                                                topTitle = "Nostalgic hits",
                                                onViewAllClick = { onPlaylistClick("90s") }
                                        )
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                // Album-style cards for 90s music collections
                                                items(playlists.filter { 
                                                    it.name.contains("90", ignoreCase = true) || 
                                                    it.name.contains("Retro", ignoreCase = true) ||
                                                    it.name.contains("Classic", ignoreCase = true) ||
                                                    it.name.contains("Old", ignoreCase = true)
                                                }.take(10).ifEmpty { playlists.shuffled().take(10) }) { album ->
                                                    WidePlaylistCard(
                                                        playlist = album,
                                                        onClick = { onPlaylistClick(album.id) }
                                                    )
                                                }
                                        }
                                }
                        }

                        // NEW: Punjabi Hits Section with Album Cards
                        if (selectedCategory == "All" || selectedCategory == "Punjabi") {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(
                                                topTitle = "DESI VIBES",
                                                title = "Punjabi Hits",
                                                titleColor = AmberGold
                                        )
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                // Album-style cards for Punjabi music collections
                                                items(playlists.filter { 
                                                    it.name.contains("Punjabi", ignoreCase = true) || 
                                                    it.name.contains("Bhangra", ignoreCase = true) ||
                                                    it.name.contains("Desi", ignoreCase = true)
                                                }.take(10).ifEmpty { playlists.shuffled().take(10) }) { album ->
                                                    WidePlaylistCard(
                                                        playlist = album,
                                                        onClick = { onPlaylistClick(album.id) }
                                                    )
                                                }
                                        }
                                }
                        }

                        // NEW: Pop Hits Section with Album Cards
                        if (selectedCategory == "All" || selectedCategory == "Pop") {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(
                                                topTitle = "GLOBAL CHART-TOPPERS",
                                                title = "Pop Hits",
                                                titleColor = AmberGold
                                        )
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                // Album-style cards for Pop music collections
                                                items(playlists.filter { 
                                                    it.name.contains("Pop", ignoreCase = true) || 
                                                    it.name.contains("Chart", ignoreCase = true) ||
                                                    it.name.contains("Global", ignoreCase = true)
                                                }.take(10).ifEmpty { playlists.shuffled().take(10) }) { album ->
                                                    WidePlaylistCard(
                                                        playlist = album,
                                                        onClick = { onPlaylistClick(album.id) }
                                                    )
                                                }
                                        }
                                }
                        }

                        // NEW: Party Playlists Section with Album Cards
                        if (selectedCategory == "All" || selectedCategory == "Party") {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(
                                                topTitle = "TURN UP THE VOLUME",
                                                title = "Party Playlists",
                                                titleColor = AmberGold
                                        )
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                // Album-style cards for Party music collections
                                                items(playlists.filter { 
                                                    it.name.contains("Party", ignoreCase = true) || 
                                                    it.name.contains("Dance", ignoreCase = true) ||
                                                    it.name.contains("Club", ignoreCase = true) ||
                                                    it.name.contains("EDM", ignoreCase = true)
                                                }.take(10).ifEmpty { playlists.shuffled().take(10) }) { album ->
                                                    WidePlaylistCard(
                                                        playlist = album,
                                                        onClick = { onPlaylistClick(album.id) }
                                                    )
                                                }
                                        }
                                }
                        }

                        // 10. Top Genres & Moods (Rectangular Cards with Text Overlay)
                        if (selectedCategory == "All" && moods.isNotEmpty()) {
                                item {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        SectionHeader(title = "Moods & Genres")
                                        LazyRow(
                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                                items(moods) { (name, colorStr) ->
                                                        val colorLong = colorStr.toLongOrNull()
                                                        val finalColor = if (colorLong != null) Color(colorLong) else AmberGold
                                                        
                                                        GenreCard(
                                                                genre = name,
                                                                color = finalColor,
                                                                onClick = { selectedCategory = name; musicViewModel.fetchByGenre(name) }
                                                        )
                                                }
                                        }
                                }
                        }
                        item {
                                Spacer(modifier = Modifier.height(24.dp))
                                SectionHeader(
                                        title = "Top Genres & Moods",
                                        topTitle = "Explore by genre",
                                        onViewAllClick = {}
                                )
                                LazyRow(
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                        val genres =
                                                listOf(
                                                        "Bollywood",
                                                        "Punjabi",
                                                        "Pop",
                                                        "Romantic",
                                                        "Party",
                                                        "Workout"
                                                )
                                        items(genres.size) { index ->
                                                GenreCard(
                                                        name = genres[index],
                                                        imageUrl =
                                                                "https://images.unsplash.com/photo-1514525253361-b83f85f051c0?w=500&q=80",
                                                        onClick = {
                                                                selectedCategory = genres[index]
                                                                musicViewModel.fetchByGenre(
                                                                        genres[index]
                                                                )
                                                                coroutineScope.launch {
                                                                        listState
                                                                                .animateScrollToItem(
                                                                                        1
                                                                                )
                                                                } // Scroll to tabs
                                                        }
                                                )
                                        }
                                }
                        }

                        // 11. Featured Artist Spotlight (e.g. Arijit Singh)
                        item {
                                Spacer(modifier = Modifier.height(32.dp))
                                ArtistSpotlightSection(
                                        artistName = "Arijit Singh",
                                        imageUrl = "https://c.saavncdn.com/artists/Arijit_Singh_007_20230916071548_500x500.jpg",
                                        songs = songs.take(4), // Simulating artist songs
                                        onSongClick = onSongClick,
                                        onMoreClick = {
                                            selectedSongForOptions = it
                                            showSongOptions = true
                                        }
                                )
                        }

                        item { Spacer(modifier = Modifier.height(40.dp)) }
                }
                // --- AI Assist Dialog ---
                if (showAIDialog) {
                    AlertDialog(
                        onDismissRequest = { showAIDialog = false },
                        containerColor = DeepNavy,
                        titleContentColor = AmberGold,
                        title = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, null, tint = AmberGold)
                                Spacer(Modifier.width(8.dp))
                                Text("FlyMusic AI Assistant")
                            }
                        },
                        text = {
                            Column {
                                Text(
                                    "Tell me your mood or what you want to hear (e.g., 'Play something energetic')",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = aiQuery,
                                    onValueChange = { aiQuery = it },
                                    placeholder = { Text("How are you feeling?", color = Color.Gray) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        cursorColor = AmberGold,
                                        focusedBorderColor = AmberGold
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                if (aiResponse.isNotEmpty()) {
                                    Spacer(Modifier.height(12.dp))
                                    Text(
                                        aiResponse,
                                        color = AmberGold,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { musicViewModel.askAI(aiQuery) },
                                enabled = !isAILoading && aiQuery.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                            ) {
                                if (isAILoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = DeepNavy)
                                } else {
                                    Text("Ask AI", color = DeepNavy)
                                }
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAIDialog = false }) {
                                Text("Close", color = Color.Gray)
                            }
                        }
                    )
                }

                // Scroll To Top FAB
                androidx.compose.animation.AnimatedVisibility(
                        visible = showScrollToTop,
                        modifier =
                                Modifier.align(Alignment.BottomEnd)
                                        .padding(end = 16.dp, bottom = 120.dp),
                        enter = androidx.compose.animation.fadeIn(),
                        exit = androidx.compose.animation.fadeOut()
                ) {
                        FloatingActionButton(
                                onClick = {
                                        coroutineScope.launch { listState.animateScrollToItem(0) }
                                },
                                containerColor = NavyLight,
                                contentColor = AmberGold,
                                modifier = Modifier.size(56.dp)
                        ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Top")
                        }
                }

                // --- Song Options Bottom Sheet ---
                if (showSongOptions && selectedSongForOptions != null) {
                    val song = selectedSongForOptions!!
                    com.example.flymusicai.ui.components.SongOptionsBottomSheet(
                        song = song,
                        onDismiss = { showSongOptions = false },
                        onPlayNext = { musicViewModel.addToPlayNext(it) },
                        onAddToQueue = { musicViewModel.addToQueue(it) },
                        onAddToPlaylist = { /* show playlist picker */ },
                        onDownload = { musicViewModel.downloadSong(it) },
                        onViewArtist = { /* Navigate to artist */ },
                        onShare = { musicViewModel.shareSong(context, it) },
                        onStartRadio = { musicViewModel.startRadio(it) },
                        onAddToLibrary = { musicViewModel.toggleFavorite(it) },
                        isFavorite = favoriteSongs.any { it.id == song.id },
                        onToggleFavorite = { musicViewModel.toggleFavorite(it) }
                    )
                }
        }
}

// --- Components ---

@Composable
fun CategoryTabsRow(
        categories: List<String>,
        selectedCategory: String,
        onCategorySelected: (String) -> Unit
) {
        LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                items(categories.size) { index ->
                        val isSelected = categories[index] == selectedCategory
                        Box(
                                modifier =
                                        Modifier.clip(RoundedCornerShape(20.dp))
                                                .background(
                                                        if (isSelected) AmberGold else NavyLight
                                                )
                                                .clickable { onCategorySelected(categories[index]) }
                                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                                Text(
                                        text = categories[index],
                                        color = if (isSelected) DeepNavy else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                )
                        }
                }
        }
}

@Composable
fun StandardSongCard(
    song: com.example.flymusicai.data.Music, 
    onClick: () -> Unit,
    onMoreClick: () -> Unit = {}
) {
        Column(
            modifier = Modifier
                .width(140.dp)
                .clickable { onClick() }
        ) {
                Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        modifier =
                                Modifier.size(140.dp)
                                        .shadow(
                                                8.dp,
                                                RoundedCornerShape(12.dp),
                                                ambientColor = AmberGold,
                                                spotColor = AmberGold
                                        )
                ) {
                        AsyncImage(
                                model = song.coverImageUrl.ifEmpty { "https://c.saavncdn.com/artists/${song.artist.replace(" ", "_")}_500x500.jpg" },
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                placeholder = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder),
                                error = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder)
                        )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = song.title,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                overflow = TextOverflow.Ellipsis
                        )
                        Text(
                                text = song.artist,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        onClick = onMoreClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
        }
}

@Composable
fun FeaturedCard(song: com.example.flymusicai.data.Music, onClick: () -> Unit) {
        Card(
                modifier =
                        Modifier.width(220.dp)
                                .clickable { onClick() }
                                .shadow(
                                        12.dp,
                                        RoundedCornerShape(16.dp),
                                        ambientColor = AmberGold,
                                        spotColor = AmberGold
                                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
        ) {
                Box(modifier = Modifier.height(280.dp)) {
                        AsyncImage(
                                model = song.coverImageUrl.ifEmpty { "https://c.saavncdn.com/artists/${song.artist.replace(" ", "_")}_500x500.jpg" },
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                placeholder = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder),
                                error = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder)
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
                                                                                Color.Black.copy(
                                                                                        alpha = 0.8f
                                                                                )
                                                                        ),
                                                                startY = 100f
                                                        )
                                                )
                        )
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                                Text(
                                        text = "TOP PICK",
                                        color = AmberGold,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                        text = song.title,
                                        color = Color.White,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2
                                )
                        }
                }
        }
}

@Composable
fun ArtistStationItem(artistName: String, imageUrl: String, onClick: () -> Unit) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onClick() }.width(100.dp)
        ) {
                Box(
                        modifier =
                                Modifier.size(100.dp)
                                        .clip(CircleShape)
                                        .shadow(
                                                12.dp,
                                                CircleShape,
                                                ambientColor = AmberGold,
                                                spotColor = AmberGold
                                        )
                                        .border(2.dp, AmberGold, CircleShape)
                ) {
                        AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                        )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = artistName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        maxLines = 1,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
        }
}

@Composable
fun ChartCard(playlist: com.example.flymusicai.data.Playlist, onClick: () -> Unit) {
        Column(modifier = Modifier.width(150.dp).clickable { onClick() }) {
                Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier =
                                Modifier.size(150.dp)
                                        .shadow(
                                                8.dp,
                                                RoundedCornerShape(12.dp),
                                                ambientColor = AmberGold,
                                                spotColor = AmberGold
                                        )
                ) {
                        Box {
                                AsyncImage(
                                        model = playlist.coverImageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .background(Color.Black.copy(alpha = 0.3f)),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.PlayCircleOutline,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.8f),
                                                modifier = Modifier.size(48.dp)
                                        )
                                }
                        }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = playlist.name,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                )
                Text(text = "Top 50 • Global", color = TextSecondary, fontSize = 12.sp)
        }
}

@Composable
fun VinylMoodItem(moodCategory: com.example.flymusicai.data.MoodCategory, color: Color) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(110.dp)
        ) {
                Box(contentAlignment = Alignment.Center) {
                        // Vinyl Background
                        Box(
                                modifier =
                                        Modifier.size(110.dp)
                                                .clip(CircleShape)
                                                .background(
                                                        Brush.radialGradient(
                                                                listOf(
                                                                        Color(0xFF222222),
                                                                        Color.Black
                                                                )
                                                        )
                                                )
                        )
                        // Label Color Ring
                        Box(
                                modifier =
                                        Modifier.size(50.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                                .border(
                                                        10.dp,
                                                        Color(0xFF111111),
                                                        CircleShape
                                                ), // Inner visuals
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        imageVector = moodCategory.icon,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                )
                        }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = moodCategory.displayName,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                )
        }
}

@Composable
fun WidePlaylistCard(playlist: com.example.flymusicai.data.Playlist, onClick: () -> Unit) {
        Box(
                modifier =
                        Modifier.width(280.dp)
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .shadow(
                                        10.dp,
                                        RoundedCornerShape(12.dp),
                                        ambientColor = AmberGold,
                                        spotColor = AmberGold
                                )
                                .clickable { onClick() }
        ) {
                AsyncImage(
                        model = playlist.coverImageUrl.ifEmpty { "https://c.saavncdn.com/editorial/charts_TopWeeklyHindi_139364_20231201123456_500x500.jpg" },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder),
                        error = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder)
                )
                // Dark Overlay
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                // Center Text
                Text(
                        text = playlist.name,
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
        }
}

@Composable
fun GenreCard(genre: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(color.copy(alpha=0.8f), color)))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            text = genre,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Composable
fun GenreCard(name: String, imageUrl: String, onClick: () -> Unit) {
        Box(
                modifier =
                        Modifier.width(160.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onClick() }
        ) {
                AsyncImage(
                        model = imageUrl.ifEmpty { "https://c.saavncdn.com/editorial/charts_TopWeeklyHindi_139364_20231201123456_500x500.jpg" },
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder),
                        error = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder)
                )
                // Colorful Gradient Overlay
                Box(
                        modifier =
                                Modifier.fillMaxSize()
                                        .background(
                                                Brush.verticalGradient(
                                                        colors =
                                                                listOf(
                                                                        Color.Transparent,
                                                                        Color.Black.copy(
                                                                                alpha = 0.7f
                                                                        )
                                                                )
                                                )
                                        )
                )
                Text(
                        text = name,
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                )
        }
}

@Composable
fun ArtistSpotlightSection(
        artistName: String,
        imageUrl: String,
        songs: List<com.example.flymusicai.data.Music>,
        onSongClick: (String) -> Unit,
        onMoreClick: (com.example.flymusicai.data.Music) -> Unit = {}
) {
        Column(modifier = Modifier.fillMaxWidth()) {
                // Artist Banner
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                        )
                        Box(
                                modifier =
                                        Modifier.fillMaxSize()
                                                .background(
                                                        Brush.verticalGradient(
                                                                colors =
                                                                        listOf(
                                                                                Color.Transparent,
                                                                                DeepNavy
                                                                        )
                                                        )
                                                )
                        )
                        Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)) {
                                Text(
                                        text = "ARTIST SPOTLIGHT",
                                        color = AmberGold,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        text = artistName,
                                        color = Color.White,
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.Bold
                                )
                        }
                }

                // Artist Songs
                Column(modifier = Modifier.padding(16.dp)) {
                        songs.forEachIndexed { index, song ->
                                Row(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(vertical = 8.dp)
                                                        .clickable { onSongClick(song.id) },
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                text = "${index + 1}",
                                                color = TextSecondary,
                                                modifier = Modifier.width(24.dp)
                                        )
                                        AsyncImage(
                                                model = song.coverImageUrl,
                                                contentDescription = null,
                                                modifier =
                                                        Modifier.size(48.dp)
                                                                .clip(RoundedCornerShape(4.dp)),
                                                contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                        text = song.title,
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Medium
                                                )
                                                Text(
                                                        text = song.artist,
                                                        color = TextSecondary,
                                                        style = MaterialTheme.typography.bodySmall
                                                )
                                        }
                                        IconButton(onClick = { onMoreClick(song) }) {
                                            Icon(
                                                    imageVector = Icons.Default.MoreVert,
                                                    contentDescription = null,
                                                    tint = TextSecondary
                                            )
                                        }
                                }
                        }
                        Text(
                                text = "View Discography >",
                                color = AmberGold,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                        )
                }
        }
}

/** Premium Header with glassmorphism and gradient (Updated Logic) */
@Composable
internal fun PremiumHeader(onSearchClick: () -> Unit, onSettingsClick: () -> Unit, onAIClick: () -> Unit) {
        Column(
                modifier =
                        Modifier.fillMaxWidth()
                                .background(DeepNavy)
                                .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
        ) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                ) {
                        Image(
                                painter = painterResource(id = R.drawable.fly_music_logo),
                                contentDescription = "Logo",
                                modifier = Modifier.height(50.dp).width(70.dp),
                                contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = "FlyMusic AI",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        
                        // AI Assistant Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(AmberGold.copy(alpha = 0.1f))
                                .border(1.dp, AmberGold.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .clickable { onAIClick() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, null, tint = AmberGold, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("AI", color = AmberGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        IconButton(onClick = onSettingsClick) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar (Static)
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(NavyLight)
                                        .clickable { onSearchClick() }
                                        .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                        text = "What do you want to play?",
                                        color = TextSecondary,
                                        fontSize = 15.sp
                                )
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.Mic, null, tint = AmberGold, modifier = Modifier.size(20.dp))
                        }
                }
        }
}

/** Reusable Section Header */
@Composable
internal fun SectionHeader(
        title: String,
        topTitle: String? = null,
        titleColor: Color = AmberGold,
        onViewAllClick: (() -> Unit)? = null
) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                if (topTitle != null) {
                        Text(
                                text = topTitle.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                letterSpacing = 1.2.sp,
                                modifier = Modifier.padding(bottom = 2.dp)
                        )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                                text = title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = titleColor,
                                modifier =
                                        Modifier.shadow(
                                                12.dp,
                                                RoundedCornerShape(4.dp),
                                                ambientColor = titleColor,
                                                spotColor = titleColor
                                        )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (onViewAllClick != null) {
                                Text(
                                        text = "View All",
                                        fontSize = 12.sp,
                                        color = AmberGold,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.clickable { onViewAllClick() }
                                )
                        }
                }
        }
}
