package com.nguyenmoclam.cocktailrecipes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme

/**
 * Card component to display a cocktail in a list or grid
 */
@Composable
fun CocktailItemCard(
    cocktail: Cocktail,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Cocktail image
            AsyncImage(
                model = cocktail.imageUrl,
                contentDescription = "Image of ${cocktail.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                placeholder = painterResource(id = R.drawable.placeholder_cocktail),
                error = painterResource(id = R.drawable.placeholder_cocktail)
            )
            
            // Content
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = cocktail.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Display ingredients
                if (cocktail.ingredients.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalBar,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        val ingredientText = cocktail.ingredients
                            .take(2)
                            .joinToString(", ") { it.name }
                        
                        Text(
                            text = if (cocktail.ingredients.size > 2) 
                                "$ingredientText..." 
                            else 
                                ingredientText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CocktailItemCardPreview() {
    CocktailRecipesTheme {
        CocktailItemCard(
            cocktail = Cocktail(
                id = "1",
                name = "Mojito",
                imageUrl = "",
                instructions = "Mix mint leaves with sugar and lime juice...",
                ingredients = listOf(
                    Ingredient("White Rum", "2 oz"),
                    Ingredient("Lime Juice", "1 oz"),
                    Ingredient("Mint Leaves", "6 leaves"),
                    Ingredient("Sugar Syrup", "0.5 oz")
                ),
                isFavorite = false
            ),
            onClick = {}
        )
    }
} 