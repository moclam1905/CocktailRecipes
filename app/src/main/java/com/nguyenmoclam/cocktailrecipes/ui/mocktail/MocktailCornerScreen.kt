package com.nguyenmoclam.cocktailrecipes.ui.mocktail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiNature
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.ui.components.CocktailItemCard
import com.nguyenmoclam.cocktailrecipes.ui.components.CocktailListLoadingPlaceholder
import com.nguyenmoclam.cocktailrecipes.ui.components.EmptyStateView
import com.nguyenmoclam.cocktailrecipes.ui.components.ErrorView
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme
import kotlin.math.sin

/**
 * Screen for the Mocktail Corner feature
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MocktailCornerScreen(
    onBackPressed: () -> Unit,
    onCocktailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MocktailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Set up pull-to-refresh
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isRefreshing,
        onRefresh = { viewModel.handleEvent(MocktailEvent.RefreshMocktails) }
    )
    
    // Remember animation preference across configuration changes
    var showAnimations by rememberSaveable { mutableStateOf(uiState.showAnimations) }
    
    // Garden-inspired colors - adjusted for dark/light mode
    val isDarkTheme = isSystemInDarkTheme()
    val gardenGradient = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                MaterialTheme.colorScheme.surface
            )
        )
    }
    
    // Effect to sync UI state with ViewModel
    if (showAnimations != uiState.showAnimations) {
        viewModel.handleEvent(MocktailEvent.ToggleAnimations(showAnimations))
    }
    
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
                            text = stringResource(R.string.mocktail_corner_title),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    },
                    actions = {
                        // Animation toggle button
                        IconButton(
                            onClick = { showAnimations = !showAnimations }
                        ) {
                            Icon(
                                imageVector = if (showAnimations) 
                                    Icons.Default.Animation 
                                else 
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (showAnimations) 
                                    "Disable animations" 
                                else 
                                    "Enable animations"
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
                    .background(brush = gardenGradient)
            ) {
                when {
                    // Show loading state
                    uiState.isLoading && uiState.mocktails.isEmpty() -> {
                        CocktailListLoadingPlaceholder(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Show error state
                    uiState.error != null && uiState.mocktails.isEmpty() -> {
                        ErrorView(
                            message = uiState.error ?: stringResource(R.string.unknown_error),
                            onRetry = { viewModel.handleEvent(MocktailEvent.LoadMocktails) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Show empty state
                    uiState.mocktails.isEmpty() -> {
                        EmptyStateView(
                            title = stringResource(R.string.no_mocktails_found),
                            message = stringResource(R.string.try_refreshing_mocktails),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    // Show mocktail list with garden-inspired UI
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 24.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Promotional banner
                            item {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + expandVertically()
                                ) {
                                    // Adjust card appearance based on theme
                                    val cardColor = MaterialTheme.colorScheme.secondaryContainer
                                    val textColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    val iconTint = if (isDarkTheme) 
                                        textColor 
                                    else 
                                        textColor.copy(alpha = 0.9f)
                                    
                                    Card(
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = cardColor
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 8.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LocalBar,
                                                    contentDescription = null,
                                                    tint = iconTint,
                                                    modifier = Modifier.size(32.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = stringResource(R.string.mocktail_benefits_title),
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = textColor,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            Text(
                                                text = stringResource(R.string.mocktail_benefits_description),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = textColor
                                            )
                                            
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            // Garden elements decorations
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.EmojiNature,
                                                    contentDescription = null,
                                                    tint = iconTint.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Section title
                            item {
                                Text(
                                    text = stringResource(R.string.mocktail_description),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                
                                Divider(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    thickness = 2.dp,
                                    modifier = Modifier
                                        .padding(bottom = 16.dp)
                                        .width(80.dp)
                                )
                            }
                            
                            // Mocktail list with wave animation
                            itemsIndexed(
                                items = uiState.mocktails,
                                key = { _, mocktail -> mocktail.id }
                            ) { index, mocktail ->
                                // Apply a sine wave transformation based on index if animations are enabled
                                val offsetX = remember(index, showAnimations) {
                                    if (showAnimations) {
                                        // Create wave pattern - INCREASED OFFSET
                                        sin(index * 0.5f) * 40 
                                    } else 0.0 // No offset if animations disabled
                                }
                                
                                AnimatedVisibility(
                                    visible = true,
                                    enter = if (showAnimations) {
                                        fadeIn(
                                            initialAlpha = 0f,
                                            animationSpec = tween(
                                                durationMillis = 500,
                                                delayMillis = index * 100,
                                                easing = LinearOutSlowInEasing
                                            )
                                        ) + expandVertically(
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                                stiffness = Spring.StiffnessLow
                                            )
                                        ) + slideInVertically( // ADDED SLIDE IN EFFECT
                                            initialOffsetY = { it / 2 }, // Start from halfway down
                                             animationSpec = tween(
                                                durationMillis = 500,
                                                delayMillis = index * 100,
                                                easing = LinearOutSlowInEasing
                                            )
                                        )
                                    } else {
                                        fadeIn(
                                            initialAlpha = 0.5f,
                                            animationSpec = tween(durationMillis = 300)
                                        )
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .graphicsLayer {
                                                translationX = offsetX.toFloat()
                                            }
                                            .padding(horizontal = 4.dp)
                                    ) {
                                        CocktailItemCard(
                                            cocktail = mocktail,
                                            onClick = { onCocktailClick(mocktail.id) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .shadow(
                                                    elevation = 2.dp,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                        )
                                    }
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
    }
}

@Preview(showBackground = true)
@Composable
fun MocktailCornerScreenPreview() {
    CocktailRecipesTheme {
        MocktailCornerScreen(
            onBackPressed = {},
            onCocktailClick = {}
        )
    }
}

// Preview dark theme
@Preview(showBackground = true)
@Composable
fun MocktailCornerScreenDarkPreview() {
    CocktailRecipesTheme(darkTheme = true) {
        MocktailCornerScreen(
            onBackPressed = {},
            onCocktailClick = {}
        )
    }
} 