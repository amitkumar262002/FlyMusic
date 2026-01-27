package com.example.flymusicai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.ui.theme.*
import kotlin.math.sin

/**
 * 🎤 Premium Voice Search Visualizer
 * Custom animated voice feedback UI - No Google UI!
 */
@Composable
fun VoiceSearchVisualizer(
    isListening: Boolean,
    soundLevel: Float,
    partialText: String,
    recognizedText: String,
    error: String?,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animated pulse effect
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Glow animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepNavy.copy(alpha = 0.98f),
                        Color.Black.copy(alpha = 0.95f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.End)
                    .offset(y = (-100).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Close",
                    tint = AmberGold,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Voice Visualizer
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(200.dp)
            ) {
                // Outer glow rings
                if (isListening) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size((200 + index * 40).dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(
                                    AmberGold.copy(alpha = glowAlpha / (index + 1))
                                )
                        )
                    }
                }

                // Animated wave visualizer
                Canvas(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    if (isListening) AmberGold.copy(alpha = 0.3f)
                                    else Color.Gray.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                ) {
                    if (isListening) {
                        drawVoiceWaves(soundLevel)
                    }
                }

                // Mic icon in center
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            if (isListening) AmberGold else Color.Gray.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Microphone",
                        tint = if (isListening) DeepNavy else Color.White,
                        modifier = Modifier
                            .size(40.dp)
                            .scale(if (isListening) pulseScale else 1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Status text
            Text(
                text = when {
                    error != null -> "Error"
                    recognizedText.isNotEmpty() -> "Recognized!"
                    isListening -> "Listening..."
                    else -> "Tap to speak"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    error != null -> Color.Red
                    isListening -> AmberGold
                    else -> Color.White.copy(alpha = 0.7f)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Partial/Recognized text display
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when {
                        error != null -> error
                        recognizedText.isNotEmpty() -> "\"$recognizedText\""
                        partialText.isNotEmpty() -> "\"$partialText\""
                        isListening -> "Say something..."
                        else -> "Tap microphone to start"
                    },
                    fontSize = 16.sp,
                    color = when {
                        error != null -> Color.Red.copy(alpha = 0.8f)
                        recognizedText.isNotEmpty() -> AmberGold
                        partialText.isNotEmpty() -> Color.White.copy(alpha = 0.8f)
                        else -> Color.White.copy(alpha = 0.5f)
                    },
                    lineHeight = 24.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Hint text
            if (isListening) {
                Text(
                    text = "🎵 Try: \"Play Kesariya\" or \"Punjabi songs\"",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

/**
 * Draw animated voice waves based on sound level
 */
private fun DrawScope.drawVoiceWaves(soundLevel: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val baseRadius = size.minDimension / 4
    val waveCount = 24

    for (i in 0 until waveCount) {
        val angle = (i.toFloat() / waveCount) * 2 * Math.PI
        
        // Create varying wave heights with some randomness
        val waveHeight = baseRadius * (0.5f + soundLevel * 1.5f) * 
            (1f + sin(angle * 3).toFloat() * 0.3f)
        
        val startRadius = baseRadius * 0.7f
        val endRadius = startRadius + waveHeight
        
        val startX = centerX + (startRadius * kotlin.math.cos(angle)).toFloat()
        val startY = centerY + (startRadius * kotlin.math.sin(angle)).toFloat()
        val endX = centerX + (endRadius * kotlin.math.cos(angle)).toFloat()
        val endY = centerY + (endRadius * kotlin.math.sin(angle)).toFloat()
        
        drawLine(
            color = AmberGold.copy(alpha = 0.6f + soundLevel * 0.4f),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}
