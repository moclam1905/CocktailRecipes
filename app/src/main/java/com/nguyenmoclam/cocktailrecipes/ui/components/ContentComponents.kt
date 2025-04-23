package com.nguyenmoclam.cocktailrecipes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient
import com.nguyenmoclam.cocktailrecipes.ui.util.createSharedElementKey
import androidx.compose.ui.platform.testTag

/**
 * Ingredient item for the recipe detail screen
 */
@Composable
fun IngredientItem(
    name: String,
    measure: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = measure,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Step item for recipe instructions
 */
@Composable
fun StepItem(
    number: Int,
    instruction: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = instruction,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Loading indicator
 */
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

/**
 * Error view
 */
@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("Try Again")
        }
    }
}

/**
 * Empty state view
 */
@Composable
fun EmptyStateView(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/**
 * Cocktail list item for the home screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailListItem(
    cocktail: Cocktail,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    enableTransition: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cocktail Image with shared element transition
            CocktailImage(
                cocktail = cocktail,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)),
                enableTransition = enableTransition
            )
            
            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = cocktail.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Show first few ingredients
                val ingredientsText = cocktail.ingredients
                    .take(2)
                    .joinToString(", ") { it.name }
                
                Text(
                    text = ingredientsText + if (cocktail.ingredients.size > 2) "..." else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Favorite Button
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = if (cocktail.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (cocktail.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (cocktail.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Reusable cocktail image composable with shared element support
 */
@Composable
fun CocktailImage(
    cocktail: Cocktail,
    modifier: Modifier = Modifier,
    enableTransition: Boolean = true,
    contentScale: ContentScale = ContentScale.Crop
) {
    // For shared element transition, we simply add a unique test tag
    // The actual transition will be handled by the navigation framework
    val imageModifier = if (enableTransition) {
        val sharedTransitionKey = createSharedElementKey(cocktail.id, "image")
        modifier.testTag(sharedTransitionKey)
    } else {
        modifier
    }
    
    // Regular image with or without transition tag
    AsyncImage(
        model = cocktail.imageUrl,
        contentDescription = "Image of ${cocktail.name}",
        modifier = imageModifier,
        contentScale = contentScale,
        placeholder = painterResource(id = R.drawable.placeholder_cocktail),
        error = painterResource(id = R.drawable.placeholder_cocktail)
    )
}

/**
 * Compact cocktail item card for grid layouts
 */
@Composable
fun CocktailItem(
    cocktail: Cocktail,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Cocktail image
            AsyncImage(
                model = cocktail.imageUrl,
                contentDescription = cocktail.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                error = painterResource(R.drawable.placeholder_cocktail),
                placeholder = painterResource(R.drawable.placeholder_cocktail)
            )
            
            // Gradient overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 0f,
                            endY = 400f
                        )
                    )
            )
            
            // Cocktail name
            Text(
                text = cocktail.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
            
            // Favorite icon if cocktail is favorite
            if (cocktail.isFavorite) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Favorite",
                    tint = Color.Red,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
fun IngredientItemPreview() {
    CocktailRecipesTheme {
        IngredientItem(
            name = "White Rum",
            measure = "2 oz"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StepItemPreview() {
    CocktailRecipesTheme {
        StepItem(
            number = 1,
            instruction = "Muddle mint leaves with sugar and lime juice. Add splash of soda water."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoadingIndicatorPreview() {
    CocktailRecipesTheme {
        LoadingIndicator()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorViewPreview() {
    CocktailRecipesTheme {
        ErrorView(
            message = "Failed to load cocktails. Please check your connection.",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStateViewPreview() {
    CocktailRecipesTheme {
        EmptyStateView(
            title = "No Favorites Yet",
            message = "Start adding cocktails to your favorites to see them here."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CocktailListItemPreview() {
    CocktailRecipesTheme {
        CocktailListItem(
            cocktail = Cocktail(
                id = "1",
                name = "Mojito",
                imageUrl = "",
                instructions = "Mix ingredients...",
                ingredients = listOf(
                    Ingredient("White Rum", "2 oz"),
                    Ingredient("Lime Juice", "1 oz"),
                    Ingredient("Mint Leaves", "6 leaves")
                ),
                isFavorite = false
            ),
            onClick = {},
            onFavoriteClick = {}
        )
    }
} 