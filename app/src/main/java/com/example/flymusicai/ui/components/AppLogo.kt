package com.example.flymusicai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.ui.theme.*

/** Attractive Fly Music AI logo component with tap navigation */
@Composable
fun FlyMusicAILogo(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        showText: Boolean = true,
        size: LogoSize = LogoSize.MEDIUM
) {
    // Animated rotation for music note
    val infiniteTransition = rememberInfiniteTransition(label = "logo_animation")
    val rotation by
            infiniteTransition.animateFloat(
                    initialValue = -10f,
                    targetValue = 10f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation = tween(2000, easing = EaseInOutSine),
                                    repeatMode = RepeatMode.Reverse
                            ),
                    label = "rotation"
            )

    // Pulse animation
    val scale by
            infiniteTransition.animateFloat(
                    initialValue = 0.95f,
                    targetValue = 1.05f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation = tween(1500, easing = EaseInOutSine),
                                    repeatMode = RepeatMode.Reverse
                            ),
                    label = "scale"
            )

    Row(
            modifier = modifier.clickable(onClick = onClick).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
    ) {
        // Gradient circle background with music icon
        Box(
                modifier =
                        Modifier.size(size.iconSize).scale(scale).drawBehind {
                            // Gradient glow effect
                            drawCircle(
                                    brush =
                                            Brush.radialGradient(
                                                    colors =
                                                            listOf(
                                                                    GradientStart.copy(
                                                                            alpha = 0.3f
                                                                    ),
                                                                    PrimaryCyan.copy(alpha = 0.3f),
                                                                    Color.Transparent
                                                            ),
                                                    center =
                                                            Offset(
                                                                    this.size.width / 2,
                                                                    this.size.height / 2
                                                            ),
                                                    radius = this.size.width / 1.5f
                                            )
                            )
                        },
                contentAlignment = Alignment.Center
        ) {
            // Gradient circle
            Box(
                    modifier =
                            Modifier.fillMaxSize().drawBehind {
                                drawCircle(
                                        brush =
                                                Brush.linearGradient(
                                                        colors = listOf(GradientStart, PrimaryCyan)
                                                )
                                )
                            },
                    contentAlignment = Alignment.Center
            ) {
                Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = "Fly Music AI Logo",
                        modifier = Modifier.size(size.iconSize * 0.6f).rotate(rotation),
                        tint = TextPrimary
                )
            }
        }

        if (showText) {
            Spacer(modifier = Modifier.width(12.dp))

            // App name with gradient text effect
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                        text = "Fly Music",
                        style =
                                TextStyle(
                                        fontSize = size.fontSize,
                                        fontWeight = FontWeight.ExtraBold,
                                        brush =
                                                Brush.linearGradient(
                                                        listOf(AmberGold, OrangeVibrant)
                                                ),
                                        shadow =
                                                Shadow(
                                                        color = OrangeVibrant.copy(alpha = 0.5f),
                                                        offset = Offset(2f, 2f),
                                                        blurRadius = 4f
                                                )
                                )
                )
                Text(
                        text = "AI",
                        style =
                                TextStyle(
                                        fontSize = size.fontSize * 0.7f,
                                        fontWeight = FontWeight.Bold,
                                        brush = Brush.linearGradient(listOf(YellowSoft, AmberGold)),
                                        letterSpacing = 2.sp
                                )
                )
            }
        }
    }
}

/** Logo size variants */
enum class LogoSize(
        val iconSize: androidx.compose.ui.unit.Dp,
        val fontSize: androidx.compose.ui.unit.TextUnit
) {
    SMALL(32.dp, 16.sp),
    MEDIUM(48.dp, 22.sp),
    LARGE(64.dp, 28.sp),
    EXTRA_LARGE(80.dp, 32.sp)
}

/** Simple logo icon only (for smaller spaces) */
@Composable
fun FlyMusicAILogoIcon(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        size: androidx.compose.ui.unit.Dp = 40.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "icon_animation")
    val rotation by
            infiniteTransition.animateFloat(
                    initialValue = -10f,
                    targetValue = 10f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation = tween(2000, easing = EaseInOutSine),
                                    repeatMode = RepeatMode.Reverse
                            ),
                    label = "rotation"
            )

    Box(
            modifier =
                    modifier.size(size).clickable(onClick = onClick).drawBehind {
                        drawCircle(
                                brush =
                                        Brush.linearGradient(
                                                colors = listOf(GradientStart, PrimaryCyan)
                                        )
                        )
                    },
            contentAlignment = Alignment.Center
    ) {
        Icon(
                imageVector = Icons.Default.MusicNote,
                contentDescription = "Fly Music AI",
                modifier = Modifier.size(size * 0.6f).rotate(rotation),
                tint = TextPrimary
        )
    }
}
