package com.example.flymusicai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.data.EqualizerPresets
import com.example.flymusicai.ui.theme.*

/**
 * 🎛️ Advanced Equalizer Screen Based on the provided design with frequency sliders, presets, bass
 * booster, and virtualizer
 */
@Composable
fun EqualizerScreen(
        themeViewModel: com.example.flymusicai.viewmodel.ThemeViewModel,
        onBack: () -> Unit
) {
    val equalizerEnabled by themeViewModel.equalizerEnabled.collectAsState()
    val currentPreset by themeViewModel.equalizerPreset.collectAsState()
    val bassBoost by themeViewModel.bassBoost.collectAsState()
    val virtualizer by themeViewModel.virtualizer.collectAsState()
    val reverb by themeViewModel.reverb.collectAsState()

    // Custom band levels (for Custom preset)
    var band60Hz by remember { mutableFloatStateOf(0f) }
    var band230Hz by remember { mutableFloatStateOf(0f) }
    var band910Hz by remember { mutableFloatStateOf(0f) }
    var band3600Hz by remember { mutableFloatStateOf(0f) }
    var band14000Hz by remember { mutableFloatStateOf(0f) }

    // Update band levels when preset changes
    LaunchedEffect(currentPreset) {
        val preset = EqualizerPresets.getPresetByName(currentPreset)
        if (currentPreset != "Custom") {
            band60Hz = preset.bands[0]
            band230Hz = preset.bands[1]
            band910Hz = preset.bands[2]
            band3600Hz = preset.bands[3]
            band14000Hz = preset.bands[4]
        }
    }

    Box(
            modifier =
                    Modifier.fillMaxSize()
                            .background(
                                    brush =
                                            Brush.verticalGradient(
                                                    colors =
                                                            listOf(
                                                                    Color(0xFF1A3A2E),
                                                                    Color(0xFF0D1F1A)
                                                            )
                                            )
                            )
    ) {
        Column(
                modifier =
                        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            // Header
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                    )
                }

                Text(
                        text = "Equalizer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                )

                // ON/OFF Switch
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                                Modifier.background(
                                                if (equalizerEnabled) AmberGold else Color.Gray,
                                                shape = RoundedCornerShape(20.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                            text = if (equalizerEnabled) "ON" else "OFF",
                            color = if (equalizerEnabled) DeepNavy else TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                            checked = equalizerEnabled,
                            onCheckedChange = { themeViewModel.toggleEqualizer() },
                            colors =
                                    SwitchDefaults.colors(
                                            checkedThumbColor = DeepNavy,
                                            checkedTrackColor = AmberGold,
                                            uncheckedThumbColor = Color.Gray,
                                            uncheckedTrackColor = Color.DarkGray
                                    )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Preset Selector
            Text(
                    text = "PRESETS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextTertiary,
                    modifier = Modifier.padding(bottom = 12.dp)
            )

            val presets =
                    listOf(
                            "Custom",
                            "Normal",
                            "Classical",
                            "Dance",
                            "Flat",
                            "Folk",
                            "Heavy Metal",
                            "Hip Hop",
                            "Jazz",
                            "Pop",
                            "Rock",
                            "Electronic"
                    )

            // Preset chips in rows
            Column {
                presets.chunked(4).forEach { rowPresets ->
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowPresets.forEach { preset ->
                            PresetChip(
                                    text = preset,
                                    selected = preset == currentPreset,
                                    onClick = { themeViewModel.setEqualizerPreset(preset) },
                                    modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill remaining space if row is not complete
                        repeat(4 - rowPresets.size) { Spacer(modifier = Modifier.weight(1f)) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Frequency Sliders
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors =
                            CardDefaults.cardColors(
                                    containerColor = Color(0xFF1A3A2E).copy(alpha = 0.5f)
                            )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FrequencySlider(
                                frequency = "60Hz",
                                value = band60Hz,
                                onValueChange = { band60Hz = it },
                                enabled = equalizerEnabled
                        )
                        FrequencySlider(
                                frequency = "230Hz",
                                value = band230Hz,
                                onValueChange = { band230Hz = it },
                                enabled = equalizerEnabled
                        )
                        FrequencySlider(
                                frequency = "910Hz",
                                value = band910Hz,
                                onValueChange = { band910Hz = it },
                                enabled = equalizerEnabled
                        )
                        FrequencySlider(
                                frequency = "3600Hz",
                                value = band3600Hz,
                                onValueChange = { band3600Hz = it },
                                enabled = equalizerEnabled
                        )
                        FrequencySlider(
                                frequency = "14000Hz",
                                value = band14000Hz,
                                onValueChange = { band14000Hz = it },
                                enabled = equalizerEnabled
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reverb Selector
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = "Reverb",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                )

                DropdownMenuButton(
                        text = reverb,
                        options = listOf("None", "Small Room", "Medium Room", "Large Room", "Hall"),
                        onSelect = { themeViewModel.setReverb(it) },
                        enabled = equalizerEnabled
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Bass Booster
            Text(
                    text = "BassBooster",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite,
                    modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                        value = bassBoost.toFloat(),
                        onValueChange = { themeViewModel.setBassBoost(it.toInt()) },
                        valueRange = 0f..100f,
                        enabled = equalizerEnabled,
                        colors =
                                SliderDefaults.colors(
                                        thumbColor = AmberGold,
                                        activeTrackColor = AmberGold,
                                        inactiveTrackColor = Color.Gray
                                ),
                        modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                        text = "$bassBoost",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Virtualizer
            Text(
                    text = "Virtualizer",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite,
                    modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                        value = virtualizer.toFloat(),
                        onValueChange = { themeViewModel.setVirtualizer(it.toInt()) },
                        valueRange = 0f..100f,
                        enabled = equalizerEnabled,
                        colors =
                                SliderDefaults.colors(
                                        thumbColor = AmberGold,
                                        activeTrackColor = AmberGold,
                                        inactiveTrackColor = Color.Gray
                                ),
                        modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                        text = "$virtualizer",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Explore More Button
            Button(
                    onClick = { /* Future feature */},
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = TextWhite
                            ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TextWhite)
            ) {
                Text("Explore more equalizer settings")
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun PresetChip(
        text: String,
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    Box(
            modifier =
                    modifier.clip(RoundedCornerShape(20.dp))
                            .background(if (selected) AmberGold else Color(0xFF2A4A3E))
                            .clickable(onClick = onClick)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
    ) {
        Text(
                text = text,
                color = if (selected) DeepNavy else TextWhite,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
        )
    }
}

@Composable
private fun FrequencySlider(
        frequency: String,
        value: Float,
        onValueChange: (Float) -> Unit,
        enabled: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
        // Value indicator
        Box(
                modifier =
                        Modifier.size(32.dp)
                                .clip(CircleShape)
                                .background(
                                        if (enabled) AmberGold.copy(alpha = 0.3f)
                                        else Color.Gray.copy(alpha = 0.2f)
                                ),
                contentAlignment = Alignment.Center
        ) {
            Text(
                    text = "${value.toInt()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) AmberGold else Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Vertical Slider
        Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = -10f..10f,
                enabled = enabled,
                colors =
                        SliderDefaults.colors(
                                thumbColor = AmberGold,
                                activeTrackColor = AmberGold,
                                inactiveTrackColor = Color.Gray
                        ),
                modifier = Modifier.height(150.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Frequency label
        Text(
                text = frequency,
                fontSize = 10.sp,
                color = TextTertiary,
                fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DropdownMenuButton(
        text: String,
        options: List<String>,
        onSelect: (String) -> Unit,
        enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
                onClick = { if (enabled) expanded = true },
                enabled = enabled,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                border =
                        androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (enabled) TextWhite else Color.Gray
                        )
        ) {
            Text(text)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                )
            }
        }
    }
}
