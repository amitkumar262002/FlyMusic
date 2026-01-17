package com.example.flymusicai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.flymusicai.R
import com.example.flymusicai.ui.theme.*

/**
 * 🎯 Premium Animated Logo Component Features:
 * - 360° continuous rotation
 * - Hover scale effect
 * - Gold glow animation
 * - Tap to navigate home
 */
@Composable
fun PremiumAnimatedLogo(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        size: Dp = 56.dp,
        enableRotation: Boolean = true,
        enableGlow: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    // 360° Infinite Rotation Animation
    val infiniteTransition = rememberInfiniteTransition(label = "logo_rotation")
    val rotation by
            infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation = tween(durationMillis = 8000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Restart
                            ),
                    label = "rotation"
            )

    // Hover Scale Animation
    val scale by
            animateFloatAsState(
                    targetValue = if (isHovered) 1.15f else 1f,
                    animationSpec =
                            spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                            ),
                    label = "scale"
            )

    // Glow Pulse Animation
    val glowAlpha by
            infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 0.7f,
                    animationSpec =
                            infiniteRepeatable(
                                    animation = tween(2000, easing = EaseInOutSine),
                                    repeatMode = RepeatMode.Reverse
                            ),
                    label = "glow"
            )

    Box(
            modifier =
                    modifier.size(size)
                            .scale(scale)
                            .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = onClick
                            ),
            contentAlignment = Alignment.Center
    ) {
        // Outer Glow Effect
        if (enableGlow) {
            Box(
                    modifier =
                            Modifier.size(size * 1.3f)
                                    .background(
                                            brush =
                                                    Brush.radialGradient(
                                                            colors =
                                                                    listOf(
                                                                            AmberGold.copy(
                                                                                    alpha =
                                                                                            glowAlpha *
                                                                                                    0.5f
                                                                            ),
                                                                            Color.Transparent
                                                                    )
                                                    ),
                                            shape = CircleShape
                                    )
            )
        }

        // Logo Container with Navy Background
        Surface(
                modifier =
                        Modifier.size(size)
                                .shadow(
                                        elevation = if (isHovered) 12.dp else 6.dp,
                                        shape = CircleShape,
                                        ambientColor = AmberGold,
                                        spotColor = AmberGold
                                ),
                shape = CircleShape,
                color = NavySurface
        ) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentAlignment = Alignment.Center
            ) {
                Image(
                        painter = painterResource(id = R.drawable.fly_music_logo),
                        contentDescription = "Fly Music AI Logo",
                        modifier =
                                Modifier.fillMaxSize().rotate(if (enableRotation) rotation else 0f),
                        contentScale = ContentScale.Fit
                )
            }
        }
    }
}

/** 🎨 Compact Premium Logo (for headers) */
@Composable
fun CompactPremiumLogo(onClick: () -> Unit, modifier: Modifier = Modifier) {
    PremiumAnimatedLogo(
            onClick = onClick,
            modifier = modifier,
            size = 48.dp,
            enableRotation = true,
            enableGlow = true
    )
}

/** 🌟 Large Premium Logo (for splash/login screens) */
@Composable
fun LargePremiumLogo(onClick: () -> Unit = {}, modifier: Modifier = Modifier) {
    PremiumAnimatedLogo(
            onClick = onClick,
            modifier = modifier,
            size = 120.dp,
            enableRotation = true,
            enableGlow = true
    )
}
