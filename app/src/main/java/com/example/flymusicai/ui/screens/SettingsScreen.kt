package com.example.flymusicai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.AuthViewModel

/** 🎛️ Enhanced Settings Screen with Advanced Features */
@Composable
fun SettingsScreen(
        authViewModel: AuthViewModel,
        themeViewModel: com.example.flymusicai.viewmodel.ThemeViewModel,
        onLogout: () -> Unit,
        onNavigateToHome: () -> Unit = {},
        onNavigateToEqualizer: () -> Unit = {}
) {
        val currentUser by authViewModel.currentUser.collectAsState()

        // Existing preferences
        val darkModeEnabled by themeViewModel.isDarkMode.collectAsState()
        val aiPersonalizationEnabled by themeViewModel.aiPersonalizationEnabled.collectAsState()
        val notificationsEnabled by themeViewModel.notificationsEnabled.collectAsState()

        // New advanced preferences
        val autoPlayEnabled by themeViewModel.autoPlayEnabled.collectAsState()
        val highQualityAudioEnabled by themeViewModel.highQualityAudioEnabled.collectAsState()
        val downloadWifiOnly by themeViewModel.downloadWifiOnly.collectAsState()
        val crossfadeEnabled by themeViewModel.crossfadeEnabled.collectAsState()
        val crossfadeDuration by themeViewModel.crossfadeDuration.collectAsState()
        val audioQuality by themeViewModel.audioQuality.collectAsState()
        val equalizerPreset by themeViewModel.equalizerPreset.collectAsState()
        val sleepTimer by themeViewModel.sleepTimer.collectAsState()
        val lyricsEnabled by themeViewModel.lyricsEnabled.collectAsState()
        val offlineModeEnabled by themeViewModel.offlineModeEnabled.collectAsState()

        var showAudioQualityDialog by remember { mutableStateOf(false) }
        var showEqualizerDialog by remember { mutableStateOf(false) }
        var showSleepTimerDialog by remember { mutableStateOf(false) }
        var showCrossfadeDialog by remember { mutableStateOf(false) }

        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
        ) {
                // 🎯 Premium Animated Logo - tap to go to Home
                com.example.flymusicai.ui.components.CompactPremiumLogo(
                        onClick = onNavigateToHome,
                        modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                        text = "Settings",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 👤 Account Section
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                                imageVector = Icons.Default.AccountCircle,
                                                contentDescription = "Account",
                                                modifier = Modifier.size(56.dp),
                                                tint = AmberGold
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                                Text(
                                                        text = currentUser?.username ?: "User",
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextWhite
                                                )
                                                Text(
                                                        text = currentUser?.email
                                                                        ?: "user@example.com",
                                                        fontSize = 14.sp,
                                                        color = TextTertiary
                                                )
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🎨 Appearance Section
                SectionHeader("APPEARANCE")

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                        Column {
                                PremiumSettingsItem(
                                        icon = Icons.Default.DarkMode,
                                        title = "Dark Mode",
                                        description = "Enable dark theme",
                                        trailing = {
                                                Switch(
                                                        checked = darkModeEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel.toggleDarkMode()
                                                        },
                                                        colors =
                                                                SwitchDefaults.colors(
                                                                        checkedThumbColor =
                                                                                DeepNavy,
                                                                        checkedTrackColor =
                                                                                AmberGold,
                                                                        uncheckedThumbColor =
                                                                                TextTertiary,
                                                                        uncheckedTrackColor =
                                                                                NavyLight
                                                                )
                                                )
                                        }
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🎵 Playback Settings
                SectionHeader("PLAYBACK")

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                        Column {
                                PremiumSettingsItem(
                                        icon = Icons.Default.PlayArrow,
                                        title = "Auto-Play",
                                        description = "Automatically play next song",
                                        trailing = {
                                                Switch(
                                                        checked = autoPlayEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel.toggleAutoPlay()
                                                        },
                                                        colors =
                                                                SwitchDefaults.colors(
                                                                        checkedThumbColor =
                                                                                DeepNavy,
                                                                        checkedTrackColor =
                                                                                AmberGold
                                                                )
                                                )
                                        }
                                )

                                HorizontalDivider(color = NavyLight)

                                PremiumSettingsItem(
                                        icon = Icons.Default.Shuffle,
                                        title = "Crossfade",
                                        description =
                                                if (crossfadeEnabled) "$crossfadeDuration seconds"
                                                else "Disabled",
                                        onClick = { showCrossfadeDialog = true },
                                        trailing = {
                                                Switch(
                                                        checked = crossfadeEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel.toggleCrossfade()
                                                        },
                                                        colors =
                                                                SwitchDefaults.colors(
                                                                        checkedThumbColor =
                                                                                DeepNavy,
                                                                        checkedTrackColor =
                                                                                AmberGold
                                                                )
                                                )
                                        }
                                )

                                HorizontalDivider(color = NavyLight)

                                PremiumSettingsItem(
                                        icon = Icons.Default.Equalizer,
                                        title = "Equalizer",
                                        description = equalizerPreset,
                                        onClick = onNavigateToEqualizer
                                )

                                HorizontalDivider(color = NavyLight)

                                PremiumSettingsItem(
                                        icon = Icons.Default.Timer,
                                        title = "Sleep Timer",
                                        description =
                                                if (sleepTimer > 0) "$sleepTimer minutes"
                                                else "Disabled",
                                        onClick = { showSleepTimerDialog = true }
                                )

                                HorizontalDivider(color = NavyLight)

                                PremiumSettingsItem(
                                        icon = Icons.Default.Lyrics,
                                        title = "Show Lyrics",
                                        description = "Display song lyrics",
                                        trailing = {
                                                Switch(
                                                        checked = lyricsEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel.toggleLyrics()
                                                        },
                                                        colors =
                                                                SwitchDefaults.colors(
                                                                        checkedThumbColor =
                                                                                DeepNavy,
                                                                        checkedTrackColor =
                                                                                AmberGold
                                                                )
                                                )
                                        }
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🎧 Audio Quality Section
                SectionHeader("AUDIO QUALITY")

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                        Column {
                                PremiumSettingsItem(
                                        icon = Icons.Default.HighQuality,
                                        title = "High Quality Audio",
                                        description = "Better sound, more data",
                                        trailing = {
                                                Switch(
                                                        checked = highQualityAudioEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel
                                                                        .toggleHighQualityAudio()
                                                        },
                                                        colors =
                                                                SwitchDefaults.colors(
                                                                        checkedThumbColor =
                                                                                DeepNavy,
                                                                        checkedTrackColor =
                                                                                AmberGold
                                                                )
                                                )
                                        }
                                )

                                HorizontalDivider(color = NavyLight)

                                PremiumSettingsItem(
                                        icon = Icons.Default.GraphicEq,
                                        title = "Audio Quality",
                                        description = audioQuality,
                                        onClick = { showAudioQualityDialog = true }
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 📥 Download Settings
                SectionHeader("DOWNLOADS")

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                        Column {
                                PremiumSettingsItem(
                                        icon = Icons.Default.Wifi,
                                        title = "Download via WiFi Only",
                                        description = "Save mobile data",
                                        trailing = {
                                                Switch(
                                                        checked = downloadWifiOnly,
                                                        onCheckedChange = {
                                                                themeViewModel
                                                                        .toggleDownloadWifiOnly()
                                                        },
                                                        colors =
                                                                SwitchDefaults.colors(
                                                                        checkedThumbColor =
                                                                                DeepNavy,
                                                                        checkedTrackColor =
                                                                                AmberGold
                                                                )
                                                )
                                        }
                                )

                                HorizontalDivider(color = NavyLight)

                                PremiumSettingsItem(
                                        icon = Icons.Default.CloudOff,
                                        title = "Offline Mode",
                                        description = "Play downloaded music only",
                                        trailing = {
                                                Switch(
                                                        checked = offlineModeEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel.toggleOfflineMode()
                                                        },
                                                        colors =
                                                                SwitchDefaults.colors(
                                                                        checkedThumbColor =
                                                                                DeepNavy,
                                                                        checkedTrackColor =
                                                                                AmberGold
                                                                )
                                                )
                                        }
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🤖 AI & Personalization
                SectionHeader("AI & PERSONALIZATION")

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                        Column {
                                PremiumSettingsItem(
                                        icon = Icons.Default.AutoAwesome,
                                        title = "AI Music Personalization",
                                        description = "Get smart recommendations",
                                        trailing = {
                                                Switch(
                                                        checked = aiPersonalizationEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel
                                                                        .toggleAIPersonalization()
                                                        },
                                                        colors =
                                                                SwitchDefaults.colors(
                                                                        checkedThumbColor =
                                                                                DeepNavy,
                                                                        checkedTrackColor =
                                                                                AmberGold
                                                                )
                                                )
                                        }
                                )

                                HorizontalDivider(color = NavyLight)

                                PremiumSettingsItem(
                                        icon = Icons.Default.Notifications,
                                        title = "Notifications",
                                        description = "Get notified about new music",
                                        trailing = {
                                                Switch(
                                                        checked = notificationsEnabled,
                                                        onCheckedChange = {
                                                                themeViewModel.toggleNotifications()
                                                        },
                                                        colors =
                                                                SwitchDefaults.colors(
                                                                        checkedThumbColor =
                                                                                DeepNavy,
                                                                        checkedTrackColor =
                                                                                AmberGold
                                                                )
                                                )
                                        }
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ℹ️ About Section
                SectionHeader("ABOUT")

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface)
                ) {
                        Column {
                                PremiumSettingsItem(
                                        icon = Icons.Default.Info,
                                        title = "App Version",
                                        description = "1.0.0 (Premium Edition)",
                                        onClick = {}
                                )

                                HorizontalDivider(color = NavyLight)

                                PremiumSettingsItem(
                                        icon = Icons.AutoMirrored.Filled.Help,
                                        title = "Help & Support",
                                        description = "Get help with the app",
                                        onClick = {}
                                )

                                HorizontalDivider(color = NavyLight)

                                PremiumSettingsItem(
                                        icon = Icons.Default.PrivacyTip,
                                        title = "Privacy Policy",
                                        description = "Read our privacy policy",
                                        onClick = {}
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 🚪 Logout Button
                Button(
                        onClick = {
                                authViewModel.logout()
                                onLogout()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = TextWhite
                                )
                ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("LOGOUT", fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                }

                Spacer(modifier = Modifier.height(100.dp))
        }

        // 🎛️ Dialogs
        if (showAudioQualityDialog) {
                AudioQualityDialog(
                        currentQuality = audioQuality,
                        onDismiss = { showAudioQualityDialog = false },
                        onSelect = { quality ->
                                themeViewModel.setAudioQuality(quality)
                                showAudioQualityDialog = false
                        }
                )
        }

        if (showEqualizerDialog) {
                EqualizerDialog(
                        currentPreset = equalizerPreset,
                        onDismiss = { showEqualizerDialog = false },
                        onSelect = { preset ->
                                themeViewModel.setEqualizerPreset(preset)
                                showEqualizerDialog = false
                        }
                )
        }

        if (showSleepTimerDialog) {
                SleepTimerDialog(
                        currentTimer = sleepTimer,
                        onDismiss = { showSleepTimerDialog = false },
                        onSelect = { minutes ->
                                themeViewModel.setSleepTimer(minutes)
                                showSleepTimerDialog = false
                        }
                )
        }

        if (showCrossfadeDialog) {
                CrossfadeDialog(
                        currentDuration = crossfadeDuration,
                        onDismiss = { showCrossfadeDialog = false },
                        onSelect = { seconds ->
                                themeViewModel.setCrossfadeDuration(seconds)
                                showCrossfadeDialog = false
                        }
                )
        }
}

@Composable
private fun SectionHeader(text: String) {
        Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGold.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )
}

@Composable
private fun PremiumSettingsItem(
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        title: String,
        description: String,
        trailing: @Composable (() -> Unit)? = null,
        onClick: () -> Unit = {}
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clickable(enabled = trailing == null, onClick = onClick)
                                .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = AmberGold,
                        modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                        )
                        Text(text = description, fontSize = 13.sp, color = TextTertiary)
                }
                if (trailing != null) {
                        trailing()
                } else {
                        Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextTertiary
                        )
                }
        }
}

// 🎛️ Dialog Components

@Composable
private fun AudioQualityDialog(
        currentQuality: String,
        onDismiss: () -> Unit,
        onSelect: (String) -> Unit
) {
        val qualities = listOf("Normal", "High", "Extreme")

        AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Audio Quality", color = AmberGold, fontWeight = FontWeight.Bold) },
                text = {
                        Column {
                                qualities.forEach { quality ->
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .clickable { onSelect(quality) }
                                                                .padding(vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                RadioButton(
                                                        selected = quality == currentQuality,
                                                        onClick = { onSelect(quality) },
                                                        colors =
                                                                RadioButtonDefaults.colors(
                                                                        selectedColor = AmberGold
                                                                )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(quality, color = TextWhite)
                                        }
                                }
                        }
                },
                confirmButton = {
                        TextButton(onClick = onDismiss) { Text("Close", color = AmberGold) }
                },
                containerColor = NavySurface
        )
}

@Composable
private fun EqualizerDialog(
        currentPreset: String,
        onDismiss: () -> Unit,
        onSelect: (String) -> Unit
) {
        val presets = listOf("Flat", "Pop", "Rock", "Jazz", "Classical", "Electronic", "Hip-Hop")

        AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                        Text("Equalizer Preset", color = AmberGold, fontWeight = FontWeight.Bold)
                },
                text = {
                        Column {
                                presets.forEach { preset ->
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .clickable { onSelect(preset) }
                                                                .padding(vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                RadioButton(
                                                        selected = preset == currentPreset,
                                                        onClick = { onSelect(preset) },
                                                        colors =
                                                                RadioButtonDefaults.colors(
                                                                        selectedColor = AmberGold
                                                                )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(preset, color = TextWhite)
                                        }
                                }
                        }
                },
                confirmButton = {
                        TextButton(onClick = onDismiss) { Text("Close", color = AmberGold) }
                },
                containerColor = NavySurface
        )
}

@Composable
private fun SleepTimerDialog(currentTimer: Int, onDismiss: () -> Unit, onSelect: (Int) -> Unit) {
        val timers = listOf(0, 15, 30, 45, 60, 90, 120)

        AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text("Sleep Timer", color = AmberGold, fontWeight = FontWeight.Bold) },
                text = {
                        Column {
                                timers.forEach { minutes ->
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .clickable { onSelect(minutes) }
                                                                .padding(vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                RadioButton(
                                                        selected = minutes == currentTimer,
                                                        onClick = { onSelect(minutes) },
                                                        colors =
                                                                RadioButtonDefaults.colors(
                                                                        selectedColor = AmberGold
                                                                )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                        if (minutes == 0) "Disabled"
                                                        else "$minutes minutes",
                                                        color = TextWhite
                                                )
                                        }
                                }
                        }
                },
                confirmButton = {
                        TextButton(onClick = onDismiss) { Text("Close", color = AmberGold) }
                },
                containerColor = NavySurface
        )
}

@Composable
private fun CrossfadeDialog(currentDuration: Int, onDismiss: () -> Unit, onSelect: (Int) -> Unit) {
        val durations = listOf(2, 5, 8, 10, 12, 15)

        AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                        Text("Crossfade Duration", color = AmberGold, fontWeight = FontWeight.Bold)
                },
                text = {
                        Column {
                                durations.forEach { seconds ->
                                        Row(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .clickable { onSelect(seconds) }
                                                                .padding(vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                        ) {
                                                RadioButton(
                                                        selected = seconds == currentDuration,
                                                        onClick = { onSelect(seconds) },
                                                        colors =
                                                                RadioButtonDefaults.colors(
                                                                        selectedColor = AmberGold
                                                                )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("$seconds seconds", color = TextWhite)
                                        }
                                }
                        }
                },
                confirmButton = {
                        TextButton(onClick = onDismiss) { Text("Close", color = AmberGold) }
                },
                containerColor = NavySurface
        )
}
