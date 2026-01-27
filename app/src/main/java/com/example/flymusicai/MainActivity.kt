package com.example.flymusicai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flymusicai.data.LocalSongsDatabase
import com.example.flymusicai.navigation.Screen
import com.example.flymusicai.ui.screens.*
import com.example.flymusicai.ui.theme.FlyMusicAITheme
import com.example.flymusicai.viewmodel.AuthViewModel
import com.example.flymusicai.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

/** Main Activity - Entry point of the Fly Music AI app */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
        }
    }

    // --- Advanced Audio Settings Sync ---
    val equalizerEnabled by themeViewModel.equalizerEnabled.collectAsState()
    val equalizerPreset by themeViewModel.equalizerPreset.collectAsState()
    val bassBoost by themeViewModel.bassBoost.collectAsState()
    val virtualizer by themeViewModel.virtualizer.collectAsState()
    val reverb by themeViewModel.reverb.collectAsState()
    val playbackSpeed by themeViewModel.playbackSpeed.collectAsState()
    val sleepTimerRunning by themeViewModel.sleepTimerRunning.collectAsState()

    LaunchedEffect(
            equalizerEnabled,
            equalizerPreset,
            bassBoost,
            virtualizer,
            reverb,
            playbackSpeed
    ) {
        val preset = com.example.flymusicai.data.EqualizerPresets.getPresetByName(equalizerPreset)
        musicViewModel.applyEqualizerSettings(equalizerEnabled, preset.bands)
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

    val sleepTimerRemaining by themeViewModel.sleepTimerRemaining.collectAsState()

    // Handle Sleep Timer
    LaunchedEffect(sleepTimerRemaining) {
        if (sleepTimerRemaining == 0 && themeViewModel.sleepTimer.value > 0) {
            // Timer expired
            if (musicViewModel.isPlaying.value) {
                musicViewModel.togglePlayPause()
            }
            // Reset sleep timer in viewmodel to avoid multiple triggers
            themeViewModel.setSleepTimer(0)
        }
    }
}
