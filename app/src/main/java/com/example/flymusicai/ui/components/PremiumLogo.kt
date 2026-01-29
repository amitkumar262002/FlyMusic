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
 *  Premium Animated Logo Component Features:
 * - 360 continuous rotation
 * - Hover scale effect
 * - Gold glow animation
 * - Tap to navigate home
 */
@Composable
fun PremiumAnimatedLogo(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        size: Dp = 56.dp, // This will now serve as height
        enableRotation: Boolean = false, // Disabled rotation by default for winged logo
        enableGlow: Boolean = true
) {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        // Hover Scale Animation
        val scale by
                animateFloatAsState(
                        targetValue = if (isHovered) 1.1f else 1f,
                        animationSpec =
                                spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                ),
                        label = "scale"
                )

        // Glow Pulse Animation
        val infiniteTransition = rememberInfiniteTransition(label = "glow_animation")
        val glowAlpha by
                infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 0.6f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(2000, easing = EaseInOutSine),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "glow"
                )

        // Calculate width based on approximate aspect ratio for wings (e.g., 1.5x)
        val width = size * 1.5f

        Box(
                modifier =
                        modifier
                                .height(size)
                                .width(width)
                                .scale(scale)
                                .clickable(
                                        interactionSource = interactionSource,
                                        indication = null,
                                        onClick = onClick
                                ),
                contentAlignment = Alignment.Center
        ) {
                // Outer Glow Effect (Subtle backlight)
                if (enableGlow) {
                        Box(
                                modifier =
                                        Modifier.size(width * 1.2f) // Make glow background large enough
                                                .background(
                                                        brush =
                                                                Brush.radialGradient(
                                                                        colors =
                                                                                listOf(
                                                                                        AmberGold.copy(alpha = glowAlpha * 0.4f),
                                                                                        Color.Transparent
                                                                                )
                                                                )
                                                )
                        )
                }

                // Logo Image
                Image(
                        painter = painterResource(id = R.drawable.fly_music_logo),
                        contentDescription = "FlyMusic AI Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                )
        }
}

/**  Compact Premium Logo (for headers) */
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

/**  Large Premium Logo (for splash/login screens) */
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
