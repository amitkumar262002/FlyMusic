package com.example.flymusicai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.ui.theme.*

/**
 * 🎛️ Extreme 10x Equalizer Screen
 * High-performance audio engine with 10x vibes for Bass, Loudness, and Virtualizer.
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    val eqBands by themeViewModel.eqBands.collectAsState()
    val loudness by themeViewModel.loudness.collectAsState()

    // Update bands when preset changes
    LaunchedEffect(currentPreset) {
        if (currentPreset != "Custom") {
            val preset = com.example.flymusicai.data.EqualizerPresets.getPresetByName(currentPreset)
            themeViewModel.setEqualizerBands(preset.bands)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Equalizer", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Switch(
                        checked = equalizerEnabled,
                        onCheckedChange = { themeViewModel.toggleEqualizer() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepNavy,
                            checkedTrackColor = AmberGold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DeepNavy)
            )
        },
        containerColor = DeepNavy
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Presets List
            Text(
                "PRESETS",
                color = AmberGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            val presets = listOf("Custom", "Normal", "Classical", "Dance", "Flat", "Folk", "Heavy Metal", "Hip Hop", "Jazz", "Pop", "Rock", "Electronic")
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { preset ->
                    FilterChip(
                        selected = preset == currentPreset,
                        onClick = { themeViewModel.setEqualizerPreset(preset) },
                        label = { Text(preset) },
                        enabled = equalizerEnabled,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberGold,
                            selectedLabelColor = DeepNavy,
                            containerColor = NavySurface,
                            labelColor = Color.White
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Frequency Bands Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        "FREQUENCY BANDS (PRO)",
                        color = AmberGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 24.dp).align(Alignment.CenterHorizontally)
                    )

                    val labels = listOf("60Hz", "230Hz", "910Hz", "3.6kHz", "14kHz")
                    labels.forEachIndexed { index, label ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                color = if (equalizerEnabled) Color.White else Color.Gray,
                                modifier = Modifier.width(60.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Slider(
                                value = eqBands.getOrElse(index) { 0f },
                                onValueChange = { themeViewModel.updateEqualizerBand(index, it) },
                                valueRange = -15f..15f,
                                enabled = equalizerEnabled,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(
                                    thumbColor = AmberGold,
                                    activeTrackColor = AmberGold,
                                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                                )
                            )
                            
                            Text(
                                text = "${eqBands.getOrElse(index) { 0f }.toInt()}dB",
                                color = if (equalizerEnabled) AmberGold else Color.Gray,
                                modifier = Modifier.width(45.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Audio Effects Section
            Text(
                "AUDIO EFFECTS",
                color = AmberGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Bass Boost
            EffectItem(
                title = "Super Bass Boost",
                value = bassBoost,
                onValueChange = { themeViewModel.setBassBoost(it) },
                enabled = equalizerEnabled,
                icon = Icons.Default.MusicNote,
                accentColor = Color(0xFFFF4081)
            )

            // Loudness
            EffectItem(
                title = "Extreme Master Gain",
                value = loudness,
                onValueChange = { themeViewModel.setLoudness(it) },
                enabled = equalizerEnabled,
                icon = Icons.Default.VolumeUp,
                accentColor = AmberGold
            )

            // Virtualizer
            EffectItem(
                title = "3D Surround Virtualizer",
                value = virtualizer,
                onValueChange = { themeViewModel.setVirtualizer(it) },
                enabled = equalizerEnabled,
                icon = Icons.Default.SurroundSound,
                accentColor = Color(0xFF00E5FF)
            )

            // Reverb Card
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GraphicEq, null, tint = AmberGold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Reverb", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    
                    ReverbSelector(
                        currentReverb = reverb,
                        onSelect = { themeViewModel.setReverb(it) },
                        enabled = equalizerEnabled
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = {
                    themeViewModel.setEqualizerPreset("Normal")
                    themeViewModel.setBassBoost(0)
                    themeViewModel.setVirtualizer(0)
                    themeViewModel.setLoudness(0)
                    themeViewModel.setReverb("None")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                enabled = equalizerEnabled,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Reset All Effects", color = if (equalizerEnabled) Color.White else Color.Gray)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun EffectItem(
    title: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    enabled: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color = AmberGold
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
                Text("${value}%", color = accentColor, fontWeight = FontWeight.Black, fontSize = 16.sp)
            }
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = 0f..100f,
                enabled = enabled,
                colors = SliderDefaults.colors(
                    thumbColor = accentColor,
                    activeTrackColor = accentColor,
                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
private fun ReverbSelector(currentReverb: String, onSelect: (String) -> Unit, enabled: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("None", "Small Room", "Medium Room", "Large Room", "Hall")

    Box {
        Surface(
            onClick = { if (enabled) expanded = true },
            color = Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            enabled = enabled
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(currentReverb, color = if (enabled) Color.White else Color.Gray, fontSize = 14.sp)
                Icon(Icons.Default.ArrowDropDown, null, tint = AmberGold)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(NavySurface)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color.White) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
