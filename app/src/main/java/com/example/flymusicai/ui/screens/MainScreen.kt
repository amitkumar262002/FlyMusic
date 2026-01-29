package com.example.flymusicai.ui.screens

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.flymusicai.R
import com.example.flymusicai.navigation.Screen
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.AuthViewModel
import com.example.flymusicai.viewmodel.MusicViewModel
import com.example.flymusicai.ui.components.UpdateDialog

/** Main screen with bottom navigation */
@Composable
fun MainScreen(
        musicViewModel: MusicViewModel,
        authViewModel: AuthViewModel,
        themeViewModel: com.example.flymusicai.viewmodel.ThemeViewModel,
        onLogout: () -> Unit
) {
        val navController = rememberNavController()
        val currentSong by musicViewModel.currentSong.collectAsState()
        val isPlaying by musicViewModel.isPlaying.collectAsState()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        // Sync Recently Played from DataStore on start
        val recentlyPlayedJson by themeViewModel.recentlyPlayedJson.collectAsState()
        val allMusic by musicViewModel.allMusic.collectAsState()
        
        // App Update Dialog
        val appUpdateConfig by musicViewModel.appUpdateConfig.collectAsState()
        var showUpdateDialog by remember { mutableStateOf(false) }

        LaunchedEffect(appUpdateConfig) {
            if (appUpdateConfig != null) {
                showUpdateDialog = true
            }
        }

        if (showUpdateDialog && appUpdateConfig != null) {
            UpdateDialog(
                config = appUpdateConfig!!,
                onDismiss = { showUpdateDialog = false }
            )
        }

        LaunchedEffect(recentlyPlayedJson, allMusic) {
            if (allMusic.isNotEmpty()) {
                try {
                    val historyIds = Json.decodeFromString<List<String>>(recentlyPlayedJson)
                    val historySongs = historyIds.mapNotNull { id -> 
                        allMusic.find { it.id == id } 
                    }
                    musicViewModel.setRecentlyPlayedList(historySongs)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Save Recently Played to DataStore when it changes
        val recentlyPlayed by musicViewModel.recentlyPlayed.collectAsState()
        LaunchedEffect(recentlyPlayed) {
            if (recentlyPlayed.isNotEmpty()) {
                val ids = recentlyPlayed.map { it.id }
                themeViewModel.setRecentlyPlayed(Json.encodeToString(ids))
            }
        }

        // Hide mini player on MusicPlayer and Equalizer screens
        val showMiniPlayer =
                currentSong != null &&
                        currentRoute != Screen.MusicPlayer.route &&
                        currentRoute != Screen.Equalizer.route &&
                        currentRoute != "for_you"

        Scaffold(
                bottomBar = {
                        // Column with mini player above navigation bar
                        Column {
                                // Global Mini Player at BOTTOM (above navigation bar)
                                AnimatedVisibility(
                                        visible = showMiniPlayer,
                                        enter = slideInVertically(initialOffsetY = { it }),
                                        exit = slideOutVertically(targetOffsetY = { it })
                                ) {
                                        currentSong?.let { song ->
                                                MiniPlayer(
                                                        song = song,
                                                        isPlaying = isPlaying,
                                                        onPlayPauseClick = {
                                                                musicViewModel.togglePlayPause()
                                                        },
                                                        onPreviousClick = {
                                                                musicViewModel.playPrevious()
                                                        },
                                                        onNextClick = { musicViewModel.playNext() },
                                                        onClick = {
                                                                navController.navigate(
                                                                        Screen.MusicPlayer
                                                                                .createRoute(
                                                                                        song.id
                                                                                )
                                                                )
                                                        }
                                                )
                                        }
                                }
                                // Bottom Navigation Bar
                                BottomNavigationBar(navController = navController)
                        }
                }
        ) { paddingValues ->
                NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(paddingValues)
                ) {
                        composable(Screen.Home.route) {
                                HomeScreen(
                                        musicViewModel = musicViewModel,
                                        onSearchClick = {
                                                navController.navigate(Screen.Search.route)
                                        },
                                        onSongClick = { songId ->
                                                musicViewModel.playSongById(
                                                        songId,
                                                        musicViewModel.allMusic.value
                                                )
                                        },
                                        onPlaylistClick = { playlistId ->
                                                navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                                        },
                                        onArtistClick = { artistName ->
                                                navController.navigate(Screen.ArtistDetail.createRoute(artistName))
                                        },
                                        onNavigateToSettings = {
                                                navController.navigate(Screen.Settings.route)
                                        }
                                )
                        }

                        composable(Screen.Search.route) {
                                SearchScreen(
                                        musicViewModel = musicViewModel,
                                        onSongClick = { songId ->
                                                musicViewModel.playSongById(
                                                        songId,
                                                        musicViewModel.searchResults.value
                                                )
                                        },
                                        onNavigateToHome = {
                                                navController.navigate(Screen.Home.route) {
                                                        popUpTo(Screen.Home.route) {
                                                                inclusive = true
                                                        }
                                                }
                                        }
                                )
                        }

                        composable(Screen.Favorites.route) {
                                FavoritesScreen(
                                        musicViewModel = musicViewModel,
                                        onSongClick = { songId ->
                                                musicViewModel.playSongById(
                                                        songId,
                                                        musicViewModel.favoriteSongs.value
                                                )
                                        },
                                        onNavigateToHome = {
                                                navController.navigate(Screen.Home.route) {
                                                        popUpTo(Screen.Home.route) {
                                                                inclusive = true
                                                        }
                                                }
                                        }
                                )
                        }

                        composable(Screen.Settings.route) {
                                SettingsScreen(
                                        authViewModel = authViewModel,
                                        themeViewModel = themeViewModel,
                                        onLogout = onLogout,
                                        onNavigateToHome = {
                                                navController.navigate(Screen.Home.route) {
                                                        popUpTo(Screen.Home.route) {
                                                                inclusive = true
                                                        }
                                                }
                                        },
                                        onNavigateToEqualizer = {
                                                navController.navigate(Screen.Equalizer.route)
                                        },
                                        onNavigateToEditProfile = {
                                                navController.navigate(Screen.EditProfile.route)
                                        }
                                )
                        }

                        composable(Screen.Equalizer.route) {
                                EqualizerScreen(
                                        themeViewModel = themeViewModel,
                                        onBack = { navController.popBackStack() }
                                )
                        }

                        composable(Screen.MusicPlayer.route) {
                                MusicPlayerScreen(
                                        musicViewModel = musicViewModel,
                                        themeViewModel = themeViewModel,
                                        onBackPress = { navController.popBackStack() },
                                        onNavigateToEqualizer = {
                                                navController.navigate(Screen.Equalizer.route)
                                        }
                                )
                        }

                        composable("for_you") {
                                ForYouScreen(
                                        musicViewModel = musicViewModel,
                                        onSongClick = { song ->
                                                musicViewModel.playSong(
                                                        song,
                                                        musicViewModel.allMusic.value
                                                )
                                        }
                                )
                        }

                        composable("library") {
                                LibraryScreen(
                                        musicViewModel = musicViewModel,
                                        authViewModel = authViewModel,
                                        onSongClick = { song ->
                                                musicViewModel.playSong(
                                                        song,
                                                        musicViewModel.allMusic.value
                                                )
                                        },
                                        onNavigateToSettings = {
                                                navController.navigate(Screen.Settings.route)
                                        },
                                        onNavigateToEditProfile = {
                                                navController.navigate(Screen.EditProfile.route)
                                        }
                                )
                        }

                        composable(Screen.EditProfile.route) {
                                EditProfileScreen(
                                        authViewModel = authViewModel,
                                        onBack = { navController.popBackStack() }
                                )
                        }

                        composable("pro") { ProScreen() }

                        composable(Screen.PlaylistDetail.route) { backStackEntry ->
                                val playlistId = backStackEntry.arguments?.getString("playlistId") ?: ""
                                PlaylistDetailScreen(
                                        musicViewModel = musicViewModel,
                                        playlistId = playlistId,
                                        onBack = { navController.popBackStack() },
                                        onSongClick = { songId ->
                                                // Fetch the relevant songs for this playlist
                                                val allPlaylists = musicViewModel.playlists.value + musicViewModel.albumsForYou.value
                                                val playlist = allPlaylists.find { it.id == playlistId }
                                                musicViewModel.playSongById(
                                                        songId,
                                                        playlist?.songs ?: musicViewModel.allMusic.value
                                                )
                                        }
                                )
                        }

                        composable(Screen.ArtistDetail.route) { backStackEntry ->
                                val artistName = backStackEntry.arguments?.getString("artistName") ?: ""
                                ArtistDetailScreen(
                                        musicViewModel = musicViewModel,
                                        artistName = artistName,
                                        onBack = { navController.popBackStack() },
                                        onSongClick = { songId, playlist ->
                                                musicViewModel.playSongById(songId, playlist)
                                        }
                                )
                        }
                }
        }
}

/** Bottom Navigation Bar with 5 tabs like FlyMusicAI */
@Composable
private fun BottomNavigationBar(navController: NavHostController) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBar(
                containerColor = NavyBlue,
                tonalElevation = 0.dp,
                contentColor = androidx.compose.ui.graphics.Color.White
        ) {
                // Home
                NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                                navController.navigate(Screen.Home.route) {
                                        popUpTo(Screen.Home.route) { inclusive = true }
                                }
                        },
                        colors =
                                NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldAccent,
                                        selectedTextColor = GoldAccent,
                                        unselectedIconColor =
                                                androidx.compose.ui.graphics.Color.White.copy(
                                                        alpha = 0.6f
                                                ),
                                        unselectedTextColor =
                                                androidx.compose.ui.graphics.Color.White.copy(
                                                        alpha = 0.6f
                                                ),
                                        indicatorColor =
                                                androidx.compose.ui.graphics.Color.Transparent
                                )
                )

                // Search
                NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search", style = MaterialTheme.typography.labelSmall) },
                        selected = currentRoute == Screen.Search.route,
                        onClick = {
                                navController.navigate(Screen.Search.route) {
                                        popUpTo(Screen.Home.route)
                                        launchSingleTop = true
                                }
                        },
                        colors =
                                NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldAccent,
                                        selectedTextColor = GoldAccent,
                                        unselectedIconColor =
                                                androidx.compose.ui.graphics.Color.White.copy(
                                                        alpha = 0.6f
                                                ),
                                        unselectedTextColor =
                                                androidx.compose.ui.graphics.Color.White.copy(
                                                        alpha = 0.6f
                                                ),
                                        indicatorColor =
                                                androidx.compose.ui.graphics.Color.Transparent
                                )
                )

                // For You
                NavigationBarItem(
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "For You") },
                        label = { Text("For You", style = MaterialTheme.typography.labelSmall) },
                        selected = currentRoute == "for_you",
                        onClick = {
                                navController.navigate("for_you") {
                                        popUpTo(Screen.Home.route)
                                        launchSingleTop = true
                                }
                        },
                        colors =
                                NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldAccent,
                                        selectedTextColor = GoldAccent,
                                        unselectedIconColor =
                                                androidx.compose.ui.graphics.Color.White.copy(
                                                        alpha = 0.6f
                                                ),
                                        unselectedTextColor =
                                                androidx.compose.ui.graphics.Color.White.copy(
                                                        alpha = 0.6f
                                                ),
                                        indicatorColor =
                                                androidx.compose.ui.graphics.Color.Transparent
                                )
                )

                // Library
                NavigationBarItem(
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                        label = { Text("Library", style = MaterialTheme.typography.labelSmall) },
                        selected = currentRoute == "library",
                        onClick = {
                                navController.navigate("library") {
                                        popUpTo(Screen.Home.route)
                                        launchSingleTop = true
                                }
                        },
                        colors =
                                NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldAccent,
                                        selectedTextColor = GoldAccent,
                                        unselectedIconColor =
                                                androidx.compose.ui.graphics.Color.White.copy(
                                                        alpha = 0.6f
                                                ),
                                        unselectedTextColor =
                                                androidx.compose.ui.graphics.Color.White.copy(
                                                        alpha = 0.6f
                                                ),
                                        indicatorColor =
                                                androidx.compose.ui.graphics.Color.Transparent
                                )
                )

                // Pro
                NavigationBarItem(
                        icon = {
                                Icon(
                                        Icons.Default.WorkspacePremium,
                                        contentDescription = "Pro",
                                        tint = if (currentRoute == "pro") GoldAccent else TealAccent
                                )
                        },
                        label = {
                                Text(
                                        "Pro",
                                        style = MaterialTheme.typography.labelSmall,
                                        color =
                                                if (currentRoute == "pro") GoldAccent
                                                else TealAccent
                                )
                        },
                        selected = currentRoute == "pro",
                        onClick = {
                                navController.navigate("pro") {
                                        popUpTo(Screen.Home.route)
                                        launchSingleTop = true
                                }
                        },
                        colors =
                                NavigationBarItemDefaults.colors(
                                        selectedIconColor = GoldAccent,
                                        selectedTextColor = GoldAccent,
                                        unselectedIconColor = TealAccent,
                                        unselectedTextColor = TealAccent,
                                        indicatorColor =
                                                androidx.compose.ui.graphics.Color.Transparent
                                )
                )
        }
}

@Composable
private fun MiniPlayer(
        song: com.example.flymusicai.data.Music,
        isPlaying: Boolean,
        onPlayPauseClick: () -> Unit,
        onPreviousClick: () -> Unit = {},
        onNextClick: () -> Unit = {},
        onClick: () -> Unit
) {
        Surface(
                modifier =
                        Modifier.fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .height(64.dp)
                                .clickable(onClick = onClick),
                shape = RoundedCornerShape(12.dp),
                color = NavySurface,
                tonalElevation = 8.dp
        ) {
                Row(
                        modifier =
                                Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        AsyncImage(
                                model = song.coverImageUrl.ifEmpty { "https://c.saavncdn.com/artists/${song.artist.replace(" ", "_")}_500x500.jpg" },
                                contentDescription = song.title,
                                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                                placeholder = androidx.compose.ui.res.painterResource(com.example.flymusicai.R.drawable.music_placeholder),
                                error = androidx.compose.ui.res.painterResource(com.example.flymusicai.R.drawable.music_placeholder)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = song.title,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                        text = song.artist,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AmberGold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                        }

                        // Playback controls (AmberGold as per Screenshot)
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                                IconButton(onClick = onPreviousClick) {
                                        Icon(
                                                imageVector = Icons.Default.SkipPrevious,
                                                contentDescription = "Previous",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                        )
                                }

                                IconButton(onClick = onPlayPauseClick) {
                                        Icon(
                                                imageVector =
                                                        if (isPlaying) Icons.Default.Pause
                                                        else Icons.Default.PlayArrow,
                                                contentDescription = "Play/Pause",
                                                tint = AmberGold,
                                                modifier = Modifier.size(32.dp)
                                        )
                                }

                                IconButton(onClick = onNextClick) {
                                        Icon(
                                                imageVector = Icons.Default.SkipNext,
                                                contentDescription = "Next",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                        )
                                }
                        }
                }
        }
}
