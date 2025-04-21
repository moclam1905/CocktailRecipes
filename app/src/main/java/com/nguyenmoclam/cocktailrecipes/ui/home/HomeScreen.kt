package com.nguyenmoclam.cocktailrecipes.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient
import com.nguyenmoclam.cocktailrecipes.ui.components.*
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi

/**
 * Home screen displaying the list of popular cocktails with pull-to-refresh
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    onCocktailClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Set up the pull-to-refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.handleEvent(HomeEvent.RefreshCocktails) }
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            text = "Cocktail Recipes",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick = onFavoritesClick) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorites"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    // Show loading state using shimmer placeholder
                    uiState.isLoading && uiState.cocktails.isEmpty() -> {
                        CocktailListLoadingPlaceholder(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Show error state
                    uiState.error != null && uiState.cocktails.isEmpty() -> {
                        ErrorView(
                            message = uiState.error ?: "Unknown error occurred",
                            onRetry = { viewModel.handleEvent(HomeEvent.LoadCocktails) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Show empty state
                    uiState.cocktails.isEmpty() -> {
                        EmptyStateView(
                            title = "No Cocktails Found",
                            message = "Try refreshing to see popular cocktails",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Show cocktail list
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(
                                items = uiState.cocktails,
                                key = { it.id }
                            ) { cocktail ->
                                CocktailListItem(
                                    cocktail = cocktail,
                                    onClick = { onCocktailClick(cocktail.id) },
                                    onFavoriteClick = { 
                                        viewModel.handleEvent(HomeEvent.ToggleFavorite(cocktail.id)) 
                                    }
                                )
                            }
                        }
                    }
                }
                
                // Pull-to-refresh indicator
                PullRefreshIndicator(
                    refreshing = uiState.isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    CocktailRecipesTheme {
        HomeScreen(
            onCocktailClick = {},
            onSearchClick = {},
            onFavoritesClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenContentPreview() {
    val cocktails = listOf(
        Cocktail(
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
        Cocktail(
            id = "2",
            name = "Margarita",
            imageUrl = "",
            instructions = "Mix ingredients...",
            ingredients = listOf(
                Ingredient("Tequila", "2 oz"),
                Ingredient("Triple Sec", "1 oz"),
                Ingredient("Lime Juice", "1 oz")
            ),
            isFavorite = true
        )
    )
    
    CocktailRecipesTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cocktails) { cocktail ->
                    CocktailListItem(
                        cocktail = cocktail,
                        onClick = {},
                        onFavoriteClick = {}
                    )
                }
            }
        }
    }
} 