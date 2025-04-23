package com.nguyenmoclam.cocktailrecipes.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterAlt
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
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.vector.ImageVector
import com.nguyenmoclam.cocktailrecipes.ui.util.ShakeDetector
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import androidx.compose.foundation.interaction.MutableInteractionSource

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
    onFilterClick: () -> Unit,
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
                // "Surprise Me" floating action button with animation
                val scale = remember { Animatable(1f) }
                LaunchedEffect(key1 = Unit) {
                    // Slight pulsing animation to draw attention to the FAB
                    while(true) {
                        scale.animateTo(
                            targetValue = 1.05f,
                            animationSpec = tween(700, easing = LinearOutSlowInEasing)
                        )
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(700, easing = LinearOutSlowInEasing)
                        )
                        delay(2000) // Delay between pulses
                    }
                }
                
                FloatingActionButton(
                    onClick = { viewModel.handleEvent(HomeEvent.GetRandomCocktail) },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.scale(scale.value)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Random Cocktail"
                        )
                        Spacer( modifier = Modifier.width(8.dp))
                        Text("Surprise Me")
                    }
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
                                bottom = 88.dp // Add extra padding for the FAB
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Feature Chips Section
                            item {
                                // Feature buttons row with animation
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + expandVertically(),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Card(
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Text(
                                                text = "Explore",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                FeatureChip(
                                                    title = "Ingredients",
                                                    icon = Icons.Default.Science,
                                                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                                    modifier = Modifier.weight(1f),
                                                    onClick = onIngredientExplorerClick
                                                )
                                                
                                                FeatureChip(
                                                    title = "Favorites",
                                                    icon = Icons.Default.Favorite,
                                                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                                    modifier = Modifier.weight(1f),
                                                    onClick = onFavoritesClick
                                                )
                                                
                                                FeatureChip(
                                                    title = "Filter",
                                                    icon = Icons.Default.FilterAlt,
                                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.weight(1f),
                                                    onClick = onFilterClick
                                                )
                                            }
                                            
                                            // Shake tip now as a small, subtle hint
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Shuffle,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.tertiary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Tip: Shake device for a random cocktail",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.tertiary
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Header for Popular Cocktails
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Nightlife,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Popular Cocktails",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Cocktail list with animated entrance
                            items(
                                items = uiState.cocktails,
                                key = { it.id }
                            ) { cocktail ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(initialAlpha = 0.3f) + 
                                            expandVertically(
                                                expandFrom = Alignment.Top,
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            )
                                ) {
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
        
        // Random Cocktail Dialog with animated entry
        AnimatedVisibility(
            visible = uiState.showRandomCocktail,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
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
            onIngredientExplorerClick = {},
            onFilterClick = {}
        )
    }
}

@Composable
fun FeatureChip(
    title: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = backgroundColor,
        modifier = modifier
            .height(50.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true, color = contentColor.copy(alpha = 0.2f)),
                onClick = onClick
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
} 