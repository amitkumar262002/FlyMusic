package com.example.flymusicai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.AuthViewModel

/** 🎛️ Premium Settings Screen matching User Screenshots */
@Composable
fun SettingsScreen(
        authViewModel: AuthViewModel,
        themeViewModel: com.example.flymusicai.viewmodel.ThemeViewModel,
        onLogout: () -> Unit,
        onNavigateToHome: () -> Unit = {},
        onNavigateToEqualizer: () -> Unit = {},
        onNavigateToEditProfile: () -> Unit = {}
) {
    val currentUser by authViewModel.currentUser.collectAsState()

    // Preferences State
    val autoPlayEnabled by themeViewModel.autoPlayEnabled.collectAsState()
    val lyricsEnabled by themeViewModel.lyricsEnabled.collectAsState()

    // Local State for UI Demo (matching screenshots)
    val explicitContentEnabled by themeViewModel.explicitContentEnabled.collectAsState()
    val annotationsEnabled by themeViewModel.annotationsEnabled.collectAsState()
    val showAds by themeViewModel.showAds.collectAsState()
    val mobileNotificationsEnabled by themeViewModel.mobileNotificationsEnabled.collectAsState()
    val emailNotificationsEnabled by themeViewModel.emailNotificationsEnabled.collectAsState()
    val videoPlaybackEnabled by themeViewModel.videoPlaybackEnabled.collectAsState()
    val audioQuality by themeViewModel.audioQuality.collectAsState()
    val musicLanguages by themeViewModel.musicLanguages.collectAsState()
    val displayLanguage by themeViewModel.displayLanguage.collectAsState()
    val appTheme by themeViewModel.appTheme.collectAsState()
    val sleepTimer by themeViewModel.sleepTimer.collectAsState()

    var showQualityDialog by remember { mutableStateOf(false) }
    var showMusicLanguagesDialog by remember { mutableStateOf(false) }
    var showDisplayLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var showPlaybackSpeedDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showClearSearchesDialog by remember { mutableStateOf(false) }

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(DeepNavy) // Dark Navy Background
                            .verticalScroll(rememberScrollState())
    ) {
        // --- Header & Profile ---
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                        )
                    }
                    Text(
                            text = "Settings",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                                text = currentUser?.username ?: "Demo User",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = currentUser?.email ?: "demo@flymusicai.com",
                                fontSize = 14.sp,
                                color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(shape = RoundedCornerShape(4.dp), color = AmberGold) {
                            Text(
                                    text = "Premium",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Text(
                                text = "Subscription Active",
                                fontSize = 12.sp,
                                color = AmberGold,
                                modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    // Big Avatar Circle with "Pro" overlay
                    Box(
                            contentAlignment = Alignment.BottomCenter,
                            modifier = Modifier.size(90.dp)
                    ) {
                        Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color(0xFF333333)
                        ) {
                            if (currentUser?.profileImageUrl != null) {
                                AsyncImage(
                                        model = currentUser?.profileImageUrl,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                        Surface(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .height(22.dp)
                                                .clip(
                                                        RoundedCornerShape(
                                                                bottomStart = 45.dp,
                                                                bottomEnd = 45.dp
                                                        )
                                                ),
                                color = Color.Black.copy(alpha = 0.5f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                        "Pro",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f))

        // --- Core Actions ---
        SettingsActionItem("Edit Profile", onClick = onNavigateToEditProfile)
        SettingsActionItem("QR Scanner", textColor = AmberGold)
        SettingsActionItem("Try FlyMusic Pro", textColor = AmberGold)
        SettingsActionItem("Activate Pro", textColor = AmberGold)

        Spacer(modifier = Modifier.height(24.dp))

        // --- My Purchases ---
        SectionHeader("My Purchases")
        SettingsActionItem("Purchased Content", subText = "0 Purchases")
        SettingsActionItem("Invoices")

        Spacer(modifier = Modifier.height(24.dp))

        // --- Music & Playback ---
        SectionHeader("Music & Playback")
        SettingsActionItem(
                "Streaming Quality",
                subText = audioQuality,
                onClick = { showQualityDialog = true }
        )
        SettingsActionItem(
                "Music Languages",
                subText = musicLanguages,
                onClick = { showMusicLanguagesDialog = true }
        )
        SettingsActionItem(
                "Display Languages",
                subText = displayLanguage,
                onClick = { showDisplayLanguageDialog = true }
        )
        SettingsActionItem("Artist Selection", onClick = { /* Navigate to Artist Selection */})
        SettingsActionItem("Equalizer", subText = "Off", onClick = onNavigateToEqualizer)
        SettingsActionItem(
                "Sleep Timer",
                subText = if (sleepTimer == 0) "Off" else "$sleepTimer Minutes",
                onClick = { showSleepTimerDialog = true }
        )
        SettingsActionItem(
                "Playback Speed",
                subText = "${themeViewModel.playbackSpeed.collectAsState().value}x",
                onClick = { showPlaybackSpeedDialog = true }
        )
        SettingsSwitchItem(
                title = "Data Saver",
                description = "Reduce audio quality on mobile networks to save data.",
                checked = themeViewModel.dataSaverEnabled.collectAsState().value,
                onCheckedChange = { themeViewModel.toggleDataSaver() }
        )

        SettingsSwitchItem(
                title = "Disable Explicit Content",
                description =
                        "Turn this ON to skip Explicit Content. This setting applies to only this account on this device.",
                checked = !explicitContentEnabled,
                onCheckedChange = { themeViewModel.toggleExplicitContent() }
        )

        HorizontalDivider(
                color = Color.DarkGray.copy(alpha = 0.3f),
                modifier = Modifier.padding(start = 16.dp)
        )

        SettingsSwitchItem(
                title = "Show Lyrics",
                description = "Sing along with lyrics available right in your player!",
                checked = lyricsEnabled,
                onCheckedChange = { themeViewModel.toggleLyrics() }
        )

        HorizontalDivider(
                color = Color.DarkGray.copy(alpha = 0.3f),
                modifier = Modifier.padding(start = 16.dp)
        )

        SettingsSwitchItem(
                title = "Annotations",
                description = "Discover the meaning and interesting stories behind the lyrics.",
                checked = annotationsEnabled,
                onCheckedChange = { themeViewModel.toggleAnnotations() }
        )

        HorizontalDivider(
                color = Color.DarkGray.copy(alpha = 0.3f),
                modifier = Modifier.padding(start = 16.dp)
        )

        SettingsSwitchItem(
                title = "Autoplay",
                description =
                        "Non-stop tunes! Continue playing similar songs when your queue ends.",
                checked = autoPlayEnabled,
                onCheckedChange = { themeViewModel.toggleAutoPlay() }
        )

        HorizontalDivider(
                color = Color.DarkGray.copy(alpha = 0.3f),
                modifier = Modifier.padding(start = 16.dp)
        )

        SettingsSwitchItem(
                title = "Videos",
                description = "Enable video playback, when available.",
                checked = videoPlaybackEnabled,
                onCheckedChange = { themeViewModel.toggleVideoPlayback() }
        )

        SettingsActionItem(
                "Clear Recent History",
                textColor = AmberGold,
                onClick = { showClearHistoryDialog = true }
        )
        SettingsActionItem(
                "Clear Recent Searches",
                textColor = AmberGold,
                onClick = { showClearSearchesDialog = true }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Display ---
        SectionHeader("Display")
        SettingsSwitchItem(
                title = "Show Ads",
                description = "",
                checked = showAds,
                onCheckedChange = { themeViewModel.toggleShowAds() }
        )
        SettingsActionItem("Theme", subText = appTheme, onClick = { showThemeDialog = true })

        Spacer(modifier = Modifier.height(24.dp))

        // --- Quick Action ---
        SectionHeader("Quick Action")
        SettingsActionItem(
                "Set FlyTune",
                trailingContent = {
                    Icon(
                            Icons.AutoMirrored.Filled.ArrowForwardIos,
                            null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                    )
                }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- Notifications ---
        SectionHeader("Notifications")
        SettingsSwitchItem(
                title = "Mobile Notifications",
                description = "Receive push notifications on this device.",
                checked = mobileNotificationsEnabled,
                onCheckedChange = { themeViewModel.toggleMobileNotifications() }
        )
        SettingsSwitchItem(
                title = "Email Notifications",
                description = "Receive updates and offers via email.",
                checked = emailNotificationsEnabled,
                onCheckedChange = { themeViewModel.toggleEmailNotifications() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- FlyMusic ---
        SectionHeader("FlyMusic")
        SettingsActionItem("Share")
        SettingsActionItem("Rate on Google Play")
        SettingsActionItem("Help & FAQ")
        SettingsActionItem("Terms & Privacy")
        SettingsActionItem("Account Settings")

        SettingsActionItem(
                "Log Out",
                textColor = AmberGold,
                onClick = {
                    authViewModel.logout()
                    onLogout()
                }
        )

        Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
        ) {
            Text(
                    "Version 10.2.1\nBlood Badshah(ANKIT) By Created\n© 2026 FlyMusicAI Media Limited. All rights reserved.",
                    color = Color.Gray,
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // --- Dialogs ---
    if (showQualityDialog) {
        AlertDialog(
                onDismissRequest = { showQualityDialog = false },
                title = { Text("Streaming Quality", color = Color.White) },
                text = {
                    Column {
                        listOf("Auto", "High", "Standard", "Low").forEach {
                            TextButton(
                                    onClick = {
                                        themeViewModel.setAudioQuality(it)
                                        showQualityDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                            ) { Text(it, color = AmberGold) }
                        }
                    }
                },
                containerColor = NavySurface,
                confirmButton = {}
        )
    }

    // --- Music Languages Dialog (Multiple Selection) ---
    if (showMusicLanguagesDialog) {
        val languages =
                listOf(
                        "Hindi",
                        "English",
                        "Punjabi",
                        "Tamil",
                        "Telugu",
                        "Marathi",
                        "Gujarati",
                        "Bengali",
                        "Kannada",
                        "Malayalam"
                )
        val selectedLangs = remember {
            mutableStateListOf<String>().apply {
                addAll(musicLanguages.split(", ").filter { it.isNotEmpty() })
            }
        }

        AlertDialog(
                onDismissRequest = { showMusicLanguagesDialog = false },
                title = { Text("Select Music Languages", color = Color.White) },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        languages.forEach { lang ->
                            val isSelected = selectedLangs.contains(lang)
                            Row(
                                    modifier =
                                            Modifier.fillMaxWidth()
                                                    .clickable {
                                                        if (isSelected) selectedLangs.remove(lang)
                                                        else selectedLangs.add(lang)
                                                    }
                                                    .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null,
                                        colors = CheckboxDefaults.colors(checkedColor = AmberGold)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(lang, color = Color.White)
                            }
                        }
                    }
                },
                containerColor = NavySurface,
                confirmButton = {
                    TextButton(
                            onClick = {
                                themeViewModel.setMusicLanguages(selectedLangs.joinToString(", "))
                                showMusicLanguagesDialog = false
                            }
                    ) { Text("DONE", color = AmberGold, fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showMusicLanguagesDialog = false }) {
                        Text("CANCEL", color = Color.Gray)
                    }
                }
        )
    }

    // --- Display Language Dialog ---
    if (showDisplayLanguageDialog) {
        val displayLangs = listOf("English", "Hindi", "Marathi", "Bengali")
        AlertDialog(
                onDismissRequest = { showDisplayLanguageDialog = false },
                title = { Text("Display Language", color = Color.White) },
                text = {
                    Column {
                        displayLangs.forEach { lang ->
                            TextButton(
                                    onClick = {
                                        themeViewModel.setDisplayLanguage(lang)
                                        showDisplayLanguageDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                        lang,
                                        color =
                                                if (displayLanguage == lang) AmberGold
                                                else Color.White
                                )
                            }
                        }
                    }
                },
                containerColor = NavySurface,
                confirmButton = {}
        )
    }

    // --- Theme Dialog ---
    if (showThemeDialog) {
        val themes = listOf("System Default", "Light", "Dark")
        AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("App Theme", color = Color.White) },
                text = {
                    Column {
                        themes.forEach { theme ->
                            TextButton(
                                    onClick = {
                                        themeViewModel.setAppTheme(theme)
                                        showThemeDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                        theme,
                                        color = if (appTheme == theme) AmberGold else Color.White
                                )
                            }
                        }
                    }
                },
                containerColor = NavySurface,
                confirmButton = {}
        )
    }

    // --- Sleep Timer Dialog ---
    if (showSleepTimerDialog) {
        val timers = listOf(0, 15, 30, 45, 60, 90)
        AlertDialog(
                onDismissRequest = { showSleepTimerDialog = false },
                title = { Text("Sleep Timer", color = Color.White) },
                text = {
                    Column {
                        timers.forEach { timer ->
                            TextButton(
                                    onClick = {
                                        themeViewModel.setSleepTimer(timer)
                                        showSleepTimerDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                        if (timer == 0) "Off" else "$timer Minutes",
                                        color = if (sleepTimer == timer) AmberGold else Color.White
                                )
                            }
                        }
                    }
                },
                containerColor = NavySurface,
                confirmButton = {}
        )
    }

    // --- Confirmation Dialogs for Clear actions ---
    if (showClearHistoryDialog) {
        AlertDialog(
                onDismissRequest = { showClearHistoryDialog = false },
                title = { Text("Clear History", color = Color.White) },
                text = {
                    Text(
                            "Are you sure you want to clear your listening history?",
                            color = Color.LightGray
                    )
                },
                containerColor = NavySurface,
                confirmButton = {
                    TextButton(onClick = { showClearHistoryDialog = false }) {
                        Text("CLEAR", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearHistoryDialog = false }) {
                        Text("CANCEL", color = Color.Gray)
                    }
                }
        )
    }

    if (showClearSearchesDialog) {
        AlertDialog(
                onDismissRequest = { showClearSearchesDialog = false },
                title = { Text("Clear Searches", color = Color.White) },
                text = {
                    Text(
                            "Are you sure you want to clear your recent searches?",
                            color = Color.LightGray
                    )
                },
                containerColor = NavySurface,
                confirmButton = {
                    TextButton(onClick = { showClearSearchesDialog = false }) {
                        Text("CLEAR", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearSearchesDialog = false }) {
                        Text("CANCEL", color = Color.Gray)
                    }
                }
        )
    }

    // --- Playback Speed Dialog ---
    if (showPlaybackSpeedDialog) {
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        AlertDialog(
                onDismissRequest = { showPlaybackSpeedDialog = false },
                title = { Text("Playback Speed", color = Color.White) },
                text = {
                    Column {
                        speeds.forEach { speed ->
                            TextButton(
                                    onClick = {
                                        themeViewModel.setPlaybackSpeed(speed)
                                        showPlaybackSpeedDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                        "${speed}x",
                                        color =
                                                if (themeViewModel.playbackSpeed.value == speed)
                                                        AmberGold
                                                else Color.White
                                )
                            }
                        }
                    }
                },
                containerColor = NavySurface,
                confirmButton = {}
        )
    }
}

// --- Helper Composables ---

@Composable
fun SectionHeader(title: String) {
    Text(
            text = title,
            color = Color.LightGray,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsActionItem(
        title: String,
        subText: String? = null,
        textColor: Color = Color.White,
        trailingContent: @Composable (() -> Unit)? = null,
        onClick: () -> Unit = {}
) {
    Column {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .clickable(onClick = onClick)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = title,
                        fontSize = 16.sp,
                        color = textColor,
                        fontWeight = FontWeight.Normal
                )
                if (subText != null) {
                    Text(text = subText, fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                )
            }
        }
        HorizontalDivider(
                color = Color.DarkGray.copy(alpha = 0.3f),
                modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun SettingsSwitchItem(
        title: String,
        description: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit
) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clickable { onCheckedChange(!checked) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal
            )
            if (description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors =
                        SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF00E5FF), // Cyan-ish active color
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.DarkGray
                        )
        )
    }
}
