package com.tolongtukar.app.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Splash screen with logo animation.
 * Phase 1 (0-300ms): Logo fades in + scales from 0.8 → 1.0 with spring bounce
 * Phase 2 (300-1000ms): Hold
 * Phase 3 (1000-1500ms): Logo scales up slightly + fades out
 * Then onFinished() called to navigate to home
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var phase by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        delay(300)    // Phase 1: fade in
        phase = 1     // Hold
        delay(700)    // Phase 2: hold
        phase = 2     // Fade out
        delay(400)    // Phase 3: fade out animation
        onFinished()
    }

    // Scale animation: 0.8 → 1.0 (spring bounce) → 1.1 (fade out)
    val animScale by animateFloatAsState(
        targetValue = when (phase) {
            0 -> 0.8f
            1 -> 1.0f
            else -> 1.1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "splashScale"
    )

    // Alpha: 0 → 1 → 0
    val animAlpha by animateFloatAsState(
        targetValue = when (phase) {
            0 -> 0f
            1 -> 1f
            else -> 0f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "splashAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Use the launcher foreground as splash logo
        // On Android, this references the adaptive icon foreground
        // For KMP, we use a platform-agnostic approach via painterResource
        SplashLogo(
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    scaleX = animScale
                    scaleY = animScale
                    alpha = animAlpha
                }
        )
    }
}

@Composable
expect fun SplashLogo(modifier: Modifier = Modifier)
