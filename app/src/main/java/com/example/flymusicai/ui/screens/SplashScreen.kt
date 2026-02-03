package com.example.flymusicai.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.R
import com.example.flymusicai.ui.theme.*
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
        var startAnimation by remember { mutableStateOf(false) }

        // Animations
        val logoScale by
                animateFloatAsState(
                        targetValue = if (startAnimation) 1f else 0.5f,
                        animationSpec =
                                spring(
                                        stiffness = Spring.StiffnessLow,
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                )
        val logoAlpha by
                animateFloatAsState(
                        targetValue = if (startAnimation) 1f else 0f,
                        animationSpec = tween(1000)
                )

        LaunchedEffect(Unit) {
                startAnimation = true
                delay(3000)
                onSplashComplete()
        }

        Box(modifier = Modifier.fillMaxSize().background(DeepNavy)) {
                // Starry Particles
                StarParticles()

                Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        // Actual Logo
                        Box(
                                modifier =
                                        Modifier.size(320.dp) // Adjusted size
                                                .scale(logoScale)
                                                .alpha(logoAlpha),
                                contentAlignment = Alignment.TopCenter
                        ) {
                                Image(
                                        painter = painterResource(id = R.drawable.app_logo_splash),
                                        contentDescription = "FlyMusic AI Logo",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape), // Optional: Clip to circle if desired, or just rounded
                                        contentScale = ContentScale.Crop, // Crop to remove bottom text
                                        alignment = Alignment.TopCenter // Focus on top part (wings)
                                )
                        }

                        // Text components removed as per user request to hide text
                        Spacer(modifier = Modifier.height(24.dp))
                }

                // Bottom Progress
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)) {
                        LinearProgressIndicator(
                                color = AmberGold,
                                trackColor = NavyLight,
                                modifier = Modifier.width(150.dp).height(2.dp).clip(CircleShape)
                        )
                }
        }
}

@Composable
fun StarParticles() {
        val particles = remember { List(50) { Offset(Random.nextFloat(), Random.nextFloat()) } }
        val infiniteTransition = rememberInfiniteTransition()
        val alpha by
                infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 0.8f,
                        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse)
                )

        Canvas(modifier = Modifier.fillMaxSize()) {
                particles.forEach { pos ->
                        drawCircle(
                                color = AmberGold.copy(alpha = alpha),
                                radius = 1.5.dp.toPx(),
                                center = Offset(pos.x * size.width, pos.y * size.height)
                        )
                }
        }
}
