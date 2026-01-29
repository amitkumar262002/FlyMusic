package com.example.flymusicai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flymusicai.navigation.Screen
import com.example.flymusicai.ui.screens.*
import com.example.flymusicai.ui.theme.FlyMusicAITheme
import com.example.flymusicai.viewmodel.AuthViewModel
import com.example.flymusicai.viewmodel.MusicViewModel

/** Main Activity - Entry point of the FlyMusic AI app */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request Notification Permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (checkSelfPermission(permission) !=
                            android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(permission), 101)
            }
        }

        setContent { FlyMusicAITheme { FlyMusicAIApp() } }
    }
}

/** Main app composable with navigation */
@Composable
fun FlyMusicAIApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val musicViewModel: MusicViewModel =
            viewModel() // viewModel() handles AndroidViewModel automatically
    val themeViewModel: com.example.flymusicai.viewmodel.ThemeViewModel = viewModel()

    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val effectiveDarkMode by themeViewModel.effectiveDarkMode.collectAsState()

    // Sync system mode once
    LaunchedEffect(isSystemDark) { themeViewModel.syncSystemDarkMode(isSystemDark) }

    val sleepTimerRemaining by themeViewModel.sleepTimerRemaining.collectAsState()

    // --- Advanced Audio Settings Sync ---
    val equalizerEnabled by themeViewModel.equalizerEnabled.collectAsState()
    val equalizerPreset by themeViewModel.equalizerPreset.collectAsState()
    val eqBands by themeViewModel.eqBands.collectAsState()
    val bassBoost by themeViewModel.bassBoost.collectAsState()
    val virtualizer by themeViewModel.virtualizer.collectAsState()
    val reverb by themeViewModel.reverb.collectAsState()
    val playbackSpeed by themeViewModel.playbackSpeed.collectAsState()

    LaunchedEffect(
            equalizerEnabled,
            equalizerPreset,
            eqBands,
            bassBoost,
            virtualizer,
            reverb,
            playbackSpeed
    ) {
        musicViewModel.applyEqualizerSettings(equalizerEnabled, eqBands)
        musicViewModel.setBassBoost(bassBoost)
        musicViewModel.setVirtualizer(virtualizer)
        musicViewModel.setReverb(reverb)
        musicViewModel.setPlaybackSpeed(playbackSpeed)
    }

    // --- Regional & Language Sync ---
    val musicLanguages by themeViewModel.musicLanguages.collectAsState()
    val displayLanguage by themeViewModel.displayLanguage.collectAsState()

    LaunchedEffect(musicLanguages, displayLanguage) {
        musicViewModel.updateRegionAndLanguage(musicLanguages, displayLanguage)
    }

    // Handle Sleep Timer
    LaunchedEffect(sleepTimerRemaining) {
        if (sleepTimerRemaining == 0 && themeViewModel.sleepTimer.value > 0) {
            if (musicViewModel.isPlaying.value) {
                musicViewModel.togglePlayPause()
            }
            themeViewModel.setSleepTimer(0)
        }
    }

    FlyMusicAITheme(darkTheme = effectiveDarkMode) {
        NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier.fillMaxSize()
        ) {
            // Splash Screen
            composable(Screen.Splash.route) {
                SplashScreen(
                        onSplashComplete = {
                            // Skip Login for now
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                )
            }

            // Login Screen
            composable(Screen.Login.route) {
                LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToSignUp = { navController.navigate(Screen.SignUp.route) }
                )
            }

            // Sign Up Screen
            composable(Screen.SignUp.route) {
                SignUpScreen(
                        authViewModel = authViewModel,
                        onSignUpSuccess = {
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.SignUp.route) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = { navController.popBackStack() }
                )
            }

            // Main Screen (with bottom navigation)
            composable(Screen.Main.route) {
                MainScreen(
                        musicViewModel = musicViewModel,
                        authViewModel = authViewModel,
                        themeViewModel = themeViewModel,
                        onLogout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Main.route) { inclusive = true }
                            }
                        }
                )
            }

            // Artist Detail Screen
            composable(Screen.ArtistDetail.route) { backStackEntry ->
                val artistName = backStackEntry.arguments?.getString("artistName") ?: "Unknown"
                ArtistDetailScreen(
                        musicViewModel = musicViewModel,
                        artistName = artistName,
                        onBack = { navController.popBackStack() },
                        onSongClick = { songId, list ->
                            musicViewModel.playSongById(songId, list)
                            navController.navigate(Screen.MusicPlayer.createRoute(songId))
                        }
                )
            }
        }

        // --- Update Checker ---
        val context = androidx.compose.ui.platform.LocalContext.current
        val updateManager = remember { com.example.flymusicai.manager.UpdateManager(context) }
        var showUpdateDialog by remember { mutableStateOf(false) }
        var updateInfo by remember {
            mutableStateOf<com.example.flymusicai.manager.UpdateInfo?>(null)
        }

        LaunchedEffect(Unit) {
            while (true) {
                try {
                    val info = updateManager.checkForUpdate()
                    if (info != null) {
                        updateInfo = info
                        showUpdateDialog = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                kotlinx.coroutines.delay(3000) // Check every 3 seconds
            }
        }

        if (showUpdateDialog && updateInfo != null) {
            androidx.compose.material3.AlertDialog(
                    onDismissRequest = {
                        if (updateInfo!!.showLaterButton) showUpdateDialog = false
                    },
                    title = { androidx.compose.material3.Text(updateInfo!!.title) },
                    text = { androidx.compose.material3.Text(updateInfo!!.message) },
                    confirmButton = {
                        androidx.compose.material3.Button(
                                onClick = {
                                    val intent =
                                            android.content.Intent(
                                                    android.content.Intent.ACTION_VIEW,
                                                    android.net.Uri.parse(updateInfo!!.updateLink)
                                            )
                                    context.startActivity(intent)
                                }
                        ) { androidx.compose.material3.Text(updateInfo!!.updateNowText) }
                    },
                    dismissButton =
                            if (updateInfo!!.showLaterButton) {
                                {
                                    androidx.compose.material3.TextButton(
                                            onClick = { showUpdateDialog = false }
                                    ) { androidx.compose.material3.Text(updateInfo!!.laterText) }
                                }
                            } else null
            )
        }
    }
}
