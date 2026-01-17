package com.example.flymusicai.ui.screens

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.flymusicai.navigation.Screen
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.AuthViewModel
import com.example.flymusicai.viewmodel.MusicViewModel

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

    // Hide mini player on MusicPlayer and Equalizer screens
    val showMiniPlayer =
            currentSong != null &&
                    currentRoute != Screen.MusicPlayer.route &&
                    currentRoute != Screen.Equalizer.route

    Scaffold(
            topBar = {
                // Mini player at TOP
                AnimatedVisibility(
                        visible = showMiniPlayer,
                        enter = slideInVertically(initialOffsetY = { -it }),
                        exit = slideOutVertically(targetOffsetY = { -it })
                ) {
                    currentSong?.let { song ->
                        MiniPlayer(
                                song = song,
                                isPlaying = isPlaying,
                                onPlayPauseClick = { musicViewModel.togglePlayPause() },
                                onPreviousClick = { musicViewModel.playPrevious() },
                                onNextClick = { musicViewModel.playNext() },
                                onClick = {
                                    navController.navigate(Screen.MusicPlayer.createRoute(song.id))
                                }
                        )
                    }
                }
            },
            bottomBar = {
                // Bottom Navigation Bar only
                BottomNavigationBar(navController = navController)
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
                        onSearchClick = { navController.navigate(Screen.Search.route) },
                        onSongClick = { songId ->
                            val song = musicViewModel.allMusic.value.find { it.id == songId }
                            song?.let { musicViewModel.playSong(it, musicViewModel.allMusic.value) }
                        },
                        onPlaylistClick = { playlistId ->
                            val playlist =
                                    musicViewModel.playlists.value.find { it.id == playlistId }
                            playlist?.let { musicViewModel.playPlaylist(it) }
                        }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                        musicViewModel = musicViewModel,
                        onSongClick = { songId ->
                            val song = musicViewModel.allMusic.value.find { it.id == songId }
                            song?.let { musicViewModel.playSong(it, musicViewModel.allMusic.value) }
                        },
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                )
            }

            composable(Screen.Favorites.route) {
                FavoritesScreen(
                        musicViewModel = musicViewModel,
                        onSongClick = { songId ->
                            val song = musicViewModel.favoriteSongs.value.find { it.id == songId }
                            song?.let {
                                musicViewModel.playSong(it, musicViewModel.favoriteSongs.value)
                            }
                        },
                        onNavigateToHome = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
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
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        onNavigateToEqualizer = { navController.navigate(Screen.Equalizer.route) }
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
                        onNavigateToEqualizer = { navController.navigate(Screen.Equalizer.route) }
                )
            }
        }
    }
}

/** Bottom Navigation Bar */
@Composable
private fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
        NavigationBarItem(
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") },
                selected = currentRoute == Screen.Home.route,
                onClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                colors =
                        NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryPurple,
                                selectedTextColor = PrimaryPurple,
                                indicatorColor = PrimaryPurple.copy(alpha = 0.1f)
                        )
        )

        NavigationBarItem(
                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                label = { Text("Search") },
                selected = currentRoute == Screen.Search.route,
                onClick = {
                    navController.navigate(Screen.Search.route) { popUpTo(Screen.Home.route) }
                },
                colors =
                        NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryCyan,
                                selectedTextColor = PrimaryCyan,
                                indicatorColor = PrimaryCyan.copy(alpha = 0.1f)
                        )
        )

        NavigationBarItem(
                icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                label = { Text("Favorites") },
                selected = currentRoute == Screen.Favorites.route,
                onClick = {
                    navController.navigate(Screen.Favorites.route) { popUpTo(Screen.Home.route) }
                },
                colors =
                        NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryPurple,
                                selectedTextColor = PrimaryPurple,
                                indicatorColor = PrimaryPurple.copy(alpha = 0.1f)
                        )
        )

        NavigationBarItem(
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text("Settings") },
                selected = currentRoute == Screen.Settings.route,
                onClick = {
                    navController.navigate(Screen.Settings.route) { popUpTo(Screen.Home.route) }
                },
                colors =
                        NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryCyan,
                                selectedTextColor = PrimaryCyan,
                                indicatorColor = PrimaryCyan.copy(alpha = 0.1f)
                        )
        )
    }
}

/** Mini player at the bottom with playback controls */
@Composable
private fun MiniPlayer(
        song: com.example.flymusicai.data.Music,
        isPlaying: Boolean,
        onPlayPauseClick: () -> Unit,
        onPreviousClick: () -> Unit = {},
        onNextClick: () -> Unit = {},
        onClick: () -> Unit
) {
    Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(8.dp),
            shape = RoundedCornerShape(12.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                    model = song.coverImageUrl,
                    contentDescription = song.title,
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = song.title,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
                Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
            }

            // Playback controls
            Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onPreviousClick, modifier = Modifier.size(40.dp)) {
                    Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onPlayPauseClick, modifier = Modifier.size(40.dp)) {
                    Icon(
                            imageVector =
                                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = PrimaryPurple,
                            modifier = Modifier.size(28.dp)
                    )
                }

                IconButton(onClick = onNextClick, modifier = Modifier.size(40.dp)) {
                    Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
