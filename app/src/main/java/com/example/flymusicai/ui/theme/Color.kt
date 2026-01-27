package com.example.flymusicai.ui.theme

import androidx.compose.ui.graphics.Color

// 🌌 Premium Fly Music - Dark Green & Gold Theme (Updated from Image)
val DeepNavy = Color(0xFF051315) // Deep Dark Green/Slate Background
val NavySurface = Color(0xFF122529) // Dark Green Card Surface
val NavyLight = Color(0xFF1A3236) // Lighter Green for Elevation

// ☀️ Rich Gold Accents
val AmberGold = Color(0xFFFFC107) // Rich Gold/Yellow
val OrangeVibrant = Color(0xFFFFB300) // Slightly Orangier Gold
val YellowSoft = Color(0xFFFFD54F) // Soft Yellow

// 🎭 Theme Colors
val BackgroundDark = DeepNavy
val SurfaceDark = NavySurface
val CardDark = NavyLight

// 🌊 Additional Colors (Used in aliases below)
val LightBlue = Color(0xFF64FFDA)

// 🎨 Missing Color Aliases (Referenced throughout the app)
val NavyBlue = DeepNavy // Alias for primary background
val DarkNavy = NavySurface // Alias for darker cards
val GoldAccent = AmberGold // Alias for gold accent color
val TealAccent = LightBlue // Alias for teal/cyan accent

// 📝 Text Colors
val TextPrimary = Color(0xFFFFC107) // Gold Primary Text
val TextSecondary = Color(0xFFE0E0E0).copy(alpha = 0.8f) // Light Grayish White
val TextTertiary = Color(0xFFB0BEC5) // Blue Gray
val TextWhite = Color(0xFFFFFFFF) // Pure White

// 🔄 Backward Compatibility Aliases (Fixed to resolve all errors)
val PrimaryPurple = AmberGold
val PrimaryCyan = OrangeVibrant
val PrimaryPink = AmberGold
val SecondaryBlue = OrangeVibrant
val LightPink = YellowSoft
val AccentMagenta = OrangeVibrant
val GradientStart = AmberGold
val GradientEnd = OrangeVibrant
val SurfaceLight = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFFFFFFF)
val ProgressActive = AmberGold
val ProgressInactive = NavyLight
val PlayerBackground = DeepNavy

// ☀️ Light Mode Colors
val LightBackground = Color(0xFFFFFFFF) // Pure White
val LightSurface = Color(0xFFF5F5F5) // Light Gray
val LightCard = Color(0xFFFFFFFF) // White Cards
val LightBorder = Color(0xFFE0E0E0) // Light Border

// Light Mode Text
val LightTextPrimary = Color(0xFF1A1A1A) // Almost Black
val LightTextSecondary = Color(0xFF424242) // Dark Gray
val LightTextTertiary = Color(0xFF757575) // Medium Gray
val LightAccent = AmberGold // Keep gold accent in light mode
