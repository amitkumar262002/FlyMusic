package com.example.flymusicai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flymusicai.ui.theme.*

/** 🎚️ Advanced Audio Settings Screen */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSettingsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
        var streamQuality by remember { mutableStateOf("Auto") }
        var downloadQuality by remember { mutableStateOf("Extreme") }
        var normalization by remember { mutableStateOf(true) }
        var bassBoost by remember { mutableStateOf(false) }
        var virtualizer by remember { mutableStateOf(false) }
        var crossfade by remember { mutableStateOf(true) }
        var crossfadeDuration by remember { mutableStateOf(5f) }
        var gaplessPlayback by remember { mutableStateOf(true) }
        var monoAudio by remember { mutableStateOf(false) }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text(
                                                "Audio Settings",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                                },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.AutoMirrored.Filled.ArrowBack,
                                                        contentDescription = "Back",
                                                        tint = Color.White
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
                        )
                },
                containerColor = NavyBlue
        ) { paddingValues ->
                LazyColumn(
                        modifier = modifier.fillMaxSize().padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        // Quality Settings Section
                        item { SectionHeader("Audio Quality", Icons.Default.HighQuality) }

                        item {
                                QualityCard(
                                        title = "Streaming Quality",
                                        description = "Quality for online playback",
                                        currentValue = streamQuality,
                                        options =
                                                listOf("Auto", "Low", "Normal", "High", "Extreme"),
                                        onSelect = { streamQuality = it }
                                )
                        }

                        item {
                                QualityCard(
                                        title = "Download Quality",
                                        description = "Quality for offline downloads",
                                        currentValue = downloadQuality,
                                        options = listOf("Normal", "High", "Extreme"),
                                        onSelect = { downloadQuality = it }
                                )
                        }

                        // Playback Settings Section
                        item {
                                Spacer(modifier = Modifier.height(8.dp))
                                SectionHeader("Playback", Icons.Default.PlayArrow)
                        }

                        item {
                                SettingToggleCard(
                                        icon = Icons.Default.GraphicEq,
                                        title = "Volume Normalization",
                                        description =
                                                "Maintain consistent volume across all tracks",
                                        checked = normalization,
                                        onCheckedChange = { normalization = it }
                                )
                        }

                        item {
                                SettingToggleCard(
                                        icon = Icons.AutoMirrored.Filled.QueueMusic,
                                        title = "Gapless Playback",
                                        description = "Seamless transitions between songs",
                                        checked = gaplessPlayback,
                                        onCheckedChange = { gaplessPlayback = it }
                                )
                        }

                        item {
                                SettingToggleCard(
                                        icon = Icons.Default.Shuffle,
                                        title = "Crossfade",
                                        description =
                                                "Fade between tracks ($crossfadeDuration seconds)",
                                        checked = crossfade,
                                        onCheckedChange = { crossfade = it },
                                        expandable = true,
                                        expandedContent = {
                                                if (crossfade) {
                                                        Column(
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .padding(
                                                                                        top = 16.dp
                                                                                )
                                                        ) {
                                                                Text(
                                                                        "Crossfade Duration",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodyMedium,
                                                                        color = Color.White,
                                                                        fontWeight =
                                                                                FontWeight.SemiBold
                                                                )

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        8.dp
                                                                                )
                                                                )

                                                                Row(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        verticalAlignment =
                                                                                Alignment
                                                                                        .CenterVertically,
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                12.dp
                                                                                        )
                                                                ) {
                                                                        Text(
                                                                                "1s",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall,
                                                                                color =
                                                                                        Color.White
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.6f
                                                                                                )
                                                                        )

                                                                        Slider(
                                                                                value =
                                                                                        crossfadeDuration,
                                                                                onValueChange = {
                                                                                        crossfadeDuration =
                                                                                                it
                                                                                },
                                                                                valueRange =
                                                                                        1f..12f,
                                                                                steps = 10,
                                                                                colors =
                                                                                        SliderDefaults
                                                                                                .colors(
                                                                                                        thumbColor =
                                                                                                                GoldAccent,
                                                                                                        activeTrackColor =
                                                                                                                GoldAccent,
                                                                                                        inactiveTrackColor =
                                                                                                                Color.White
                                                                                                                        .copy(
                                                                                                                                alpha =
                                                                                                                                        0.3f
                                                                                                                        )
                                                                                                ),
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        )
                                                                        )

                                                                        Text(
                                                                                "12s",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall,
                                                                                color =
                                                                                        Color.White
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.6f
                                                                                                )
                                                                        )
                                                                }

                                                                Text(
                                                                        "${crossfadeDuration.toInt()} seconds",
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .bodySmall,
                                                                        color = GoldAccent,
                                                                        modifier =
                                                                                Modifier.align(
                                                                                        Alignment
                                                                                                .CenterHorizontally
                                                                                )
                                                                )
                                                        }
                                                }
                                        }
                                )
                        }

                        // Audio Effects Section
                        item {
                                Spacer(modifier = Modifier.height(8.dp))
                                SectionHeader("Audio Effects", Icons.Default.Tune)
                        }

                        item {
                                SettingToggleCard(
                                        icon = Icons.Default.Speaker,
                                        title = "Bass Boost",
                                        description = "Enhance low frequencies",
                                        checked = bassBoost,
                                        onCheckedChange = { bassBoost = it }
                                )
                        }

                        item {
                                SettingToggleCard(
                                        icon = Icons.Default.Headphones,
                                        title = "Virtualizer",
                                        description = "3D surround sound effect",
                                        checked = virtualizer,
                                        onCheckedChange = { virtualizer = it }
                                )
                        }

                        // Accessibility Section
                        item {
                                Spacer(modifier = Modifier.height(8.dp))
                                SectionHeader("Accessibility", Icons.Default.Accessibility)
                        }

                        item {
                                SettingToggleCard(
                                        icon = Icons.Default.VolumeUp,
                                        title = "Mono Audio",
                                        description = "Combine stereo channels",
                                        checked = monoAudio,
                                        onCheckedChange = { monoAudio = it }
                                )
                        }

                        // Info Card
                        item {
                                Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                CardDefaults.cardColors(
                                                        containerColor =
                                                                TealAccent.copy(alpha = 0.15f)
                                                )
                                ) {
                                        Row(
                                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.Info,
                                                        contentDescription = null,
                                                        tint = TealAccent,
                                                        modifier = Modifier.size(24.dp)
                                                )

                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                "Quality Impact",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleSmall,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.White
                                                        )
                                                        Text(
                                                                "Higher quality uses more data and storage. Extreme quality is 320kbps.",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .bodySmall,
                                                                color =
                                                                        Color.White.copy(
                                                                                alpha = 0.8f
                                                                        )
                                                        )
                                                }
                                        }
                                }
                        }

                        // Bottom Space
                        item { Spacer(modifier = Modifier.height(100.dp)) }
                }
        }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
        Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
        ) {
                Icon(
                        icon,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(20.dp)
                )

                Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent
                )
        }
}

@Composable
private fun QualityCard(
        title: String,
        description: String,
        currentValue: String,
        options: List<String>,
        onSelect: (String) -> Unit
) {
        var expanded by remember { mutableStateOf(false) }

        Card(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavy)
        ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                        )

                                        Text(
                                                text = description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.6f)
                                        )
                                }

                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        Text(
                                                text = currentValue,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldAccent
                                        )

                                        Icon(
                                                if (expanded) Icons.Default.ExpandLess
                                                else Icons.Default.ExpandMore,
                                                contentDescription = null,
                                                tint = Color.White.copy(alpha = 0.6f)
                                        )
                                }
                        }

                        AnimatedVisibility(
                                visible = expanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                        ) {
                                Column(
                                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                        options.forEach { option ->
                                                val isSelected = option == currentValue

                                                Surface(
                                                        modifier =
                                                                Modifier.fillMaxWidth().clickable {
                                                                        onSelect(option)
                                                                        expanded = false
                                                                },
                                                        shape = RoundedCornerShape(12.dp),
                                                        color =
                                                                if (isSelected)
                                                                        GoldAccent.copy(
                                                                                alpha = 0.2f
                                                                        )
                                                                else Color.Transparent
                                                ) {
                                                        Row(
                                                                modifier =
                                                                        Modifier.fillMaxWidth()
                                                                                .padding(12.dp),
                                                                horizontalArrangement =
                                                                        Arrangement.SpaceBetween,
                                                                verticalAlignment =
                                                                        Alignment.CenterVertically
                                                        ) {
                                                                Column {
                                                                        Text(
                                                                                text = option,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodyMedium,
                                                                                fontWeight =
                                                                                        if (isSelected
                                                                                        )
                                                                                                FontWeight
                                                                                                        .Bold
                                                                                        else
                                                                                                FontWeight
                                                                                                        .Normal,
                                                                                color =
                                                                                        if (isSelected
                                                                                        )
                                                                                                GoldAccent
                                                                                        else
                                                                                                Color.White
                                                                        )

                                                                        Text(
                                                                                text =
                                                                                        when (option
                                                                                        ) {
                                                                                                "Auto" ->
                                                                                                        "Adapts to connection"
                                                                                                "Low" ->
                                                                                                        "~96 kbps"
                                                                                                "Normal" ->
                                                                                                        "~160 kbps"
                                                                                                "High" ->
                                                                                                        "~256 kbps"
                                                                                                "Extreme" ->
                                                                                                        "320 kbps"
                                                                                                else ->
                                                                                                        ""
                                                                                        },
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .labelSmall,
                                                                                color =
                                                                                        Color.White
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.5f
                                                                                                )
                                                                        )
                                                                }

                                                                if (isSelected) {
                                                                        Icon(
                                                                                Icons.Default
                                                                                        .CheckCircle,
                                                                                contentDescription =
                                                                                        "Selected",
                                                                                tint = GoldAccent,
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                20.dp
                                                                                        )
                                                                        )
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun SettingToggleCard(
        icon: ImageVector,
        title: String,
        description: String,
        checked: Boolean,
        onCheckedChange: (Boolean) -> Unit,
        expandable: Boolean = false,
        expandedContent: @Composable () -> Unit = {}
) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavy)
        ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                                if (checked)
                                                                        GoldAccent.copy(
                                                                                alpha = 0.2f
                                                                        )
                                                                else Color.White.copy(alpha = 0.1f)
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Icon(
                                                icon,
                                                contentDescription = null,
                                                tint =
                                                        if (checked) GoldAccent
                                                        else Color.White.copy(alpha = 0.6f),
                                                modifier = Modifier.size(20.dp)
                                        )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = title,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White
                                        )

                                        Text(
                                                text = description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color.White.copy(alpha = 0.6f)
                                        )
                                }

                                Switch(
                                        checked = checked,
                                        onCheckedChange = onCheckedChange,
                                        colors =
                                                SwitchDefaults.colors(
                                                        checkedThumbColor = NavyBlue,
                                                        checkedTrackColor = GoldAccent,
                                                        uncheckedThumbColor = Color.Gray,
                                                        uncheckedTrackColor = Color.DarkGray
                                                )
                                )
                        }

                        if (expandable) {
                                expandedContent()
                        }
                }
        }
}
