package com.nguyenmoclam.cocktailrecipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nguyenmoclam.cocktailrecipes.ui.components.*
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CocktailRecipesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ComponentShowcase()
                    }
                }
            }
        }
    }
}

@Composable
fun ComponentShowcase() {
    var searchText by remember { mutableStateOf("") }
    var sliderValue by remember { mutableStateOf(20f..60f) }
    var selectedFilter by remember { mutableStateOf("Popular") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Text(
            text = "Cocktail Recipes",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        // Search Bar
        CocktailSearchBar(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = "Search cocktails..."
        )
        
        // Buttons
        Text(
            text = "Buttons",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CocktailPrimaryButton(
                text = "Primary",
                onClick = { },
                modifier = Modifier.weight(1f)
            )
            CocktailSecondaryButton(
                text = "Secondary",
                onClick = { },
                modifier = Modifier.weight(1f)
            )
        }
        
        // Cards
        Text(
            text = "Cards",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        CocktailCard(
            title = "Mojito",
            description = "A refreshing Cuban cocktail with white rum, lime juice, mint leaves, and sugar.",
            onClick = { }
        )
        
        // Filter Chips
        Text(
            text = "Filters",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        FilterChipGroup(
            options = listOf("All", "Popular", "Recent", "Favorite"),
            selectedOption = selectedFilter,
            onOptionSelected = { selectedFilter = it }
        )
        
        // Slider
        CocktailRangeSlider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            title = "Alcohol Content (%)"
        )
        
        // Ingredient List
        SectionHeader(title = "Ingredients")
        IngredientItem(name = "White Rum", measure = "2 oz")
        IngredientItem(name = "Lime Juice", measure = "1 oz")
        IngredientItem(name = "Sugar", measure = "2 tsp")
        IngredientItem(name = "Mint Leaves", measure = "6")
        IngredientItem(name = "Soda Water", measure = "Top")
        
        // Step List
        SectionHeader(title = "Instructions")
        StepItem(
            number = 1, 
            instruction = "Muddle mint leaves with sugar and lime juice. Add a splash of soda water."
        )
        StepItem(
            number = 2,
            instruction = "Fill the glass with cracked ice, pour the rum and top with soda water."
        )
        StepItem(
            number = 3,
            instruction = "Garnish with mint leaves and a lime wedge. Serve with a straw."
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}