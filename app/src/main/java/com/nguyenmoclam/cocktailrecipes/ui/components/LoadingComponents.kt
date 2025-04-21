package com.nguyenmoclam.cocktailrecipes.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme

/**
 * Enhanced loading animation for UI elements
 * 
 * Provides a visually appealing, animated loading indicator with a pulsating effect
 */
@Composable
fun PulsatingLoadingIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsating")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .scale(scale)
                .alpha(1f - (scale - 0.8f) / 0.4f)
                .background(color.copy(alpha = 0.2f), CircleShape)
        )
        
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = color,
            strokeWidth = 4.dp,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

/**
 * Cocktail glass loading animation
 * 
 * A fun, themed loading animation showing a cocktail glass with liquid filling up
 */
@Composable
fun CocktailGlassLoading(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "filling")
    val fillPercent by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fill"
    )
    
    val wobble by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobble"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(120.dp)
                .height(180.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glass stem
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    )
            )
            
            // Glass base
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(8.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp
                        )
                    )
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Glass body (container)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                // The liquid filling up
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .fillMaxHeight(fillPercent)
                        .offset(x = (wobble * 2).dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Mixing...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * Shimmer loading effect provider
 * 
 * Creates an animated shimmer Brush and passes it to content.
 */
@Composable
fun ShimmerLoading(
    isLoading: Boolean = true,
    content: @Composable (brush: Brush) -> Unit
) {
    if (!isLoading) {
        content(Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)))
        return
    }
    
    // Use more contrasting colors, e.g., based on onSurface
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), // More transparent center
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    // Increase target value for a wider shimmer band relative to duration
    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1500f, // Increased target value
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000, // Faster animation
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart 
        ),
        label = "shimmer translate"
    )

    // Create the animated horizontal brush
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - 500f, y = 0f), // Start offset for band width
        end = Offset(x = translateAnimation.value, y = 0f)          // Horizontal end point
    )

    content(brush)
}

/**
 * Shimmer cocktail list item placeholder
 */
@Composable
fun ShimmerCocktailItem(
    modifier: Modifier = Modifier
) {
    // Define a base placeholder background
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val placeholderModifier = Modifier.background(placeholderColor)

    ShimmerLoading { shimmerBrush -> 
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(12.dp)) 
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .then(placeholderModifier)
                    .background(shimmerBrush)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                // Title placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(4.dp)) 
                        .then(placeholderModifier)
                        .background(shimmerBrush)
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Subtitle placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(4.dp)) 
                        .then(placeholderModifier)
                        .background(shimmerBrush)
                )
            }

            // Favorite icon placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(8.dp)
                    .clip(CircleShape) 
                    .then(placeholderModifier)
                    .background(shimmerBrush)
            )
        }
    }
}

/**
 * Loading placeholder for a list of cocktails using shimmer items
 */
@Composable
fun CocktailListLoadingPlaceholder(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(itemCount) {
            ShimmerCocktailItem()
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
fun PulsatingLoadingIndicatorPreview() {
    CocktailRecipesTheme {
        PulsatingLoadingIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun CocktailGlassLoadingPreview() {
    CocktailRecipesTheme {
        CocktailGlassLoading()
    }
}

@Preview(showBackground = true, name = "Shimmer Item")
@Composable
fun ShimmerCocktailItemPreview() {
    CocktailRecipesTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ShimmerCocktailItem()
        }
    }
}

@Preview(showBackground = true, name = "Shimmer List Placeholder")
@Composable
fun CocktailListLoadingPlaceholderPreview() {
    CocktailRecipesTheme {
        CocktailListLoadingPlaceholder(itemCount = 3)
    }
} 