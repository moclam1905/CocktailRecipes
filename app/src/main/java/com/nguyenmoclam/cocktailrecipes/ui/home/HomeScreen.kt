package com.nguyenmoclam.cocktailrecipes.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient
import com.nguyenmoclam.cocktailrecipes.ui.components.*
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.vector.ImageVector
import com.nguyenmoclam.cocktailrecipes.ui.util.ShakeDetector
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color

/**
 * Home screen displaying the list of popular cocktails with pull-to-refresh
 * and a "Surprise Me" button for random cocktails
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun HomeScreen(
    onCocktailClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onIngredientExplorerClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Remember the callback separately
    val handleShake = remember {
        { viewModel.handleEvent(HomeEvent.GetRandomCocktail) }
    }

    // Use applicationContext to avoid memory leaks
    val shakeDetector = remember {
        ShakeDetector(context.applicationContext, handleShake)
    }

    // Start/stop shake detection based on lifecycle
    DisposableEffect(key1 = Unit) {
        shakeDetector.startDetecting()
        onDispose {
            shakeDetector.stopDetecting()
        }
    }
    
    // Make sure the random cocktail dialog is dismissed when this screen is not active
    // This ensures it won't show when returning to this screen
    LaunchedEffect(key1 = Unit) {
        viewModel.handleEvent(HomeEvent.DismissRandomCocktail)
    }
    
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
                            text = stringResource(R.string.home_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(onClick = onSearchClick) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.search)
                            )
                        }
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                // "Surprise Me" floating action button
                FloatingActionButton(
                    onClick = { viewModel.handleEvent(HomeEvent.GetRandomCocktail) },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Surprise Me"
                    )
                }
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
                            message = uiState.error ?: stringResource(R.string.unknown_error),
                            onRetry = { viewModel.handleEvent(HomeEvent.LoadCocktails) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Show empty state
                    uiState.cocktails.isEmpty() -> {
                        EmptyStateView(
                            title = stringResource(R.string.no_cocktails_found),
                            message = stringResource(R.string.try_refreshing),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Show cocktail list
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp, 
                                end = 16.dp, 
                                top = 16.dp,
                                bottom = 80.dp // Add extra padding for the FAB
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Feature Cards
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    FeatureCard(
                                        title = "Ingredients",
                                        icon = Icons.Default.Science,
                                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.weight(1f),
                                        onClick = onIngredientExplorerClick
                                    )
                                    
                                    FeatureCard(
                                        title = "Favorites",
                                        icon = Icons.Default.Favorite,
                                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.weight(1f),
                                        onClick = onFavoritesClick
                                    )
                                }
                            }
                            
                            // "Shake for a random cocktail" tip
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shuffle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(
                                            text = "Shake your device for a random cocktail!",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                }
                            }

                            // Cocktail list
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
        
        // Random Cocktail Dialog
        if (uiState.showRandomCocktail) {
            Dialog(
                onDismissRequest = { viewModel.handleEvent(HomeEvent.DismissRandomCocktail) }
            ) {
                RandomCocktailCard(
                    cocktail = uiState.randomCocktail,
                    isLoading = uiState.isLoadingRandom,
                    error = uiState.randomCocktailError,
                    onDismiss = { viewModel.handleEvent(HomeEvent.DismissRandomCocktail) },
                    onFavoriteClick = { cocktailId ->
                        viewModel.handleEvent(HomeEvent.ToggleFavorite(cocktailId))
                    },
                    onViewDetails = { cocktailId ->
                        viewModel.handleEvent(HomeEvent.DismissRandomCocktail)
                        onCocktailClick(cocktailId)
                    },
                    modifier = Modifier.fillMaxWidth()
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
            onFavoritesClick = {},
            onSettingsClick = {},
            onIngredientExplorerClick = {}
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
        // Preview content
    }
}

@Composable
fun FeatureCard(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
} 