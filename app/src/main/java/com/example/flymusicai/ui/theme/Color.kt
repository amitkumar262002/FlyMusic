package com.example.flymusicai.ui.theme

import androidx.compose.ui.graphics.Color

// 🌌 Premium Fly Music - Navy & Gold Theme
val DeepNavy = Color(0xFF0A192F) // Base Background
val NavySurface = Color(0xFF112240) // Surface/Card Background
val NavyLight = Color(0xFF172A45) // Elevated Background

// ☀️ Yellow-Orange Accents (Premium Gold feel)
val AmberGold = Color(0xFFFFB600) // Primary Text/Icons
val OrangeVibrant = Color(0xFFFF8C00) // Accent/Buttons
val YellowSoft = Color(0xFFFFD700) // Secondary Text

// 🎭 Theme Colors
val BackgroundDark = DeepNavy
val SurfaceDark = NavySurface
val CardDark = NavyLight

// 📝 Text Colors
val TextPrimary = Color(0xFFFFB600) // Yellow-Orange
val TextSecondary = Color(0xFFFFD700).copy(alpha = 0.8f) // Soft Yellow
val TextTertiary = Color(0xFF8892B0) // Grayish Blue for small text
val TextWhite = Color(0xFFF8F8F8) // Off-white

// 🔄 Backward Compatibility Aliases (Fixed to resolve all errors)
val PrimaryPurple = AmberGold // Linked to Gold
val PrimaryCyan = OrangeVibrant // Linked to Orange
val PrimaryPink = AmberGold // Linked to Gold
val SecondaryBlue = OrangeVibrant // Linked to Orange
val LightPink = YellowSoft
val LightBlue = Color(0xFF64FFDA)
val AccentMagenta = OrangeVibrant
val GradientStart = AmberGold
val GradientEnd = OrangeVibrant
val SurfaceLight = Color(0xFFFFFFFF)
val BackgroundLight = Color(0xFFF8F9FF)
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
val LightTextSecondary = Color(0xFF666666) // Medium Gray
val LightTextTertiary = Color(0xFF999999) // Light Gray
val LightAccent = AmberGold // Keep gold accent in light mode
