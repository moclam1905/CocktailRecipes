package com.nguyenmoclam.cocktailrecipes.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.abs

/**
 * A card that can be flipped with a 3D animation effect.
 * @param isFlipped Whether the card is currently flipped or not.
 * @param frontContent Content to show on the front of the card.
 * @param backContent Content to show on the back of the card.
 * @param flipDurationMs Duration of the flip animation in milliseconds.
 * @param modifier Modifier for the layout.
 */
@Composable
fun FlipCard(
    isFlipped: Boolean,
    frontContent: @Composable () -> Unit,
    backContent: @Composable () -> Unit,
    flipDurationMs: Int = 800,
    modifier: Modifier = Modifier
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = flipDurationMs,
            easing = FastOutSlowInEasing
        ),
        label = "flip_animation"
    )

    Box(modifier = modifier) {
        // Front content
        if (rotation < 90f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Apply the rotation around the Y-axis
                        rotationY = rotation
                        // Adjust scale based on rotation to create perspective effect
                        val scale = 1f - (abs(rotation) / 180f) * 0.2f
                        scaleX = scale
                        scaleY = scale
                        cameraDistance = 12f * density // Adjusts the perspective
                    }
            ) {
                frontContent()
            }
        }
        
        // Back content
        if (rotation >= 90f) {
            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        // Apply the rotation around the Y-axis
                        rotationY = rotation - 180f
                        // Adjust scale based on rotation to create perspective effect
                        val scale = 1f - (abs(180f - rotation) / 180f) * 0.2f
                        scaleX = scale
                        scaleY = scale
                        cameraDistance = 12f * density // Adjusts the perspective
                    }
            ) {
                backContent()
            }
        }
    }
} 