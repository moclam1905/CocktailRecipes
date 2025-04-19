package com.nguyenmoclam.cocktailrecipes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nguyenmoclam.cocktailrecipes.ui.theme.*

/**
 * Primary button with custom styling for the cocktail app
 */
@Composable
fun CocktailPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

/**
 * Secondary button with outline styling
 */
@Composable
fun CocktailSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            width = 2.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

/**
 * Card component for displaying cocktail items in a list
 */
@Composable
fun CocktailCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Chip component for ingredient selection
 */
@Composable
fun IngredientChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = if (selected) 
            MaterialTheme.colorScheme.primaryContainer 
        else 
            MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        border = if (!selected) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        } else {
            null
        }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) 
                MaterialTheme.colorScheme.onPrimaryContainer 
            else 
                MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

/**
 * Section header with optional trailing content
 */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (trailingContent != null) {
            trailingContent()
        }
    }
}

/**
 * Rating indicator
 */
@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    maxRating: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxRating) {
            val isFilledStar = i <= rating
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFilledStar) 
                            MaterialTheme.colorScheme.tertiary 
                        else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
            )
            if (i < maxRating) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
fun CocktailPrimaryButtonPreview() {
    CocktailRecipesTheme {
        CocktailPrimaryButton(
            text = "View Recipe",
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CocktailSecondaryButtonPreview() {
    CocktailRecipesTheme {
        CocktailSecondaryButton(
            text = "Add to Favorites",
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CocktailCardPreview() {
    CocktailRecipesTheme {
        CocktailCard(
            title = "Mojito",
            description = "A refreshing Cuban cocktail with white rum, lime juice, mint leaves, and sugar.",
            onClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IngredientChipPreview() {
    CocktailRecipesTheme {
        Row {
            IngredientChip(
                text = "Rum",
                selected = true,
                onClick = { }
            )
            Spacer(modifier = Modifier.width(8.dp))
            IngredientChip(
                text = "Vodka",
                selected = false,
                onClick = { }
            )
        }
    }
} 