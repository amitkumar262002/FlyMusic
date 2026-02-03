package com.example.flymusicai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/** 🎨 Dark Theme - Navy & Gold Premium */
private val DarkColorScheme =
        darkColorScheme(
                primary = AmberGold,
                secondary = OrangeVibrant,
                tertiary = YellowSoft,
                background = DeepNavy,
                surface = NavySurface,
                onPrimary = DeepNavy,
                onSecondary = DeepNavy,
                onTertiary = DeepNavy,
                onBackground = TextWhite,
                onSurface = TextWhite,
                surfaceVariant = NavyLight,
                onSurfaceVariant = TextTertiary
        )

/** ☀️ Light Theme - Clean White & Gold */
private val LightColorScheme =
        lightColorScheme(
                primary = AmberGold,
                secondary = OrangeVibrant,
                tertiary = YellowSoft,
                background = Color.White,
                surface = Color(0xFFF5F5F5),
                onPrimary = Color.White,
                onSecondary = Color.White,
                onTertiary = DeepNavy,
                onBackground = Color(0xFF1A1A1A),
                onSurface = Color(0xFF1A1A1A),
                surfaceVariant = Color(0xFFE0E0E0),
                onSurfaceVariant = Color(0xFF666666)
        )

@Composable
fun FlyMusicAITheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    // Force Dark Mode always
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Dark mode - Navy status bar
            window.statusBarColor = DeepNavy.toArgb()
            window.navigationBarColor = DeepNavy.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
