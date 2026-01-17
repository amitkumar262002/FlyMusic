package com.example.flymusicai

import android.os.Bundle
import android.util.Log
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
import com.example.flymusicai.manager.DownloadManager
import com.example.flymusicai.navigation.Screen
import com.example.flymusicai.ui.screens.*
import com.example.flymusicai.ui.theme.FlyMusicAITheme
import com.example.flymusicai.viewmodel.AuthViewModel
import com.example.flymusicai.viewmodel.MusicViewModel
import kotlinx.coroutines.launch

/**
 * Main Activity - Entry point of the Fly Music AI app
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlyMusicAITheme {
                FlyMusicAIApp()
            }
        }

        lifecycleScope.launch {
            val songs = LocalSongsDatabase.getAllSongs()
            println("Total: ${songs.size}")

            val uniqueTitles = songs.map { it.title }.toSet()
            println("Unique: ${uniqueTitles.size}")

            val haryanvi = LocalSongsDatabase.getSongsByCategory("Haryanvi")
            println("Haryanvi: ${haryanvi.size}")

            val downloadManager = DownloadManager(this@MainActivity)
            downloadManager.downloadSong(songs[0])
        }
    }
}

/**
 * Main app composable with navigation
 */
@Composable
fun FlyMusicAIApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val musicViewModel: MusicViewModel = viewModel() // viewModel() handles AndroidViewModel automatically
    val themeViewModel: com.example.flymusicai.viewmodel.ThemeViewModel = viewModel()
    
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    
    FlyMusicAITheme(darkTheme = isDarkMode) {
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.fillMaxSize()
        ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = {
                    if (isAuthenticated) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
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
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                }
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
                onNavigateToLogin = {
                    navController.popBackStack()
                }
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
}