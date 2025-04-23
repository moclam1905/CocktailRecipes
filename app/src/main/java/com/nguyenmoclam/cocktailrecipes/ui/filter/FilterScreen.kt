package com.nguyenmoclam.cocktailrecipes.ui.filter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WineBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseScreen
import com.nguyenmoclam.cocktailrecipes.ui.components.CocktailGlassLoading
import com.nguyenmoclam.cocktailrecipes.ui.components.CocktailItemCard
import com.nguyenmoclam.cocktailrecipes.ui.components.ErrorMessage
import com.nguyenmoclam.cocktailrecipes.ui.components.IngredientChip

/**
 * Main filter screen with "mixology lab" theme
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FilterScreen(
    viewModel: FilterViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onCocktailClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Pull-to-refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = { viewModel.refresh() }
    )
    
    // Use BaseScreen instead of Scaffold directly
    BaseScreen(
        viewModel = viewModel,
        topBar = {
            TopAppBar(
                title = { Text("The Mixology Lab") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(onClick = { viewModel.clearFilters() }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear Filters"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        // Mixology lab background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            if (uiState.isLoading && uiState.categories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CocktailGlassLoading(modifier = Modifier.size(120.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Preparing the lab...",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            } else if (uiState.error != null && uiState.categories.isEmpty()) {
                ErrorMessage(
                    message = uiState.error ?: "An error occurred",
                    onRetry = { viewModel.refresh() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Filter Lab Title
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mixology Filters",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    }
                    
                    // Category Filter Section
                    item {
                        FilterSection(
                            title = "Category",
                            icon = Icons.Default.LocalBar,
                            items = uiState.categories.map { it.name },
                            selectedItem = uiState.selectedCategory,
                            onItemSelected = { viewModel.selectCategory(it) }
                        )
                    }
                    
                    // Glass Type Filter Section
                    item {
                        FilterSection(
                            title = "Glass Type",
                            icon = Icons.Default.WineBar,
                            items = uiState.glassTypes.map { it.name },
                            selectedItem = uiState.selectedGlassType,
                            onItemSelected = { viewModel.selectGlassType(it) }
                        )
                    }
                    
                    // Alcoholic Filter Section
                    item {
                        FilterSection(
                            title = "Alcohol Content",
                            icon = Icons.Default.FilterAlt,
                            items = uiState.alcoholicFilters.map { it.name },
                            selectedItem = uiState.selectedAlcoholicFilter,
                            onItemSelected = { viewModel.selectAlcoholicFilter(it) }
                        )
                    }
                    
                    // Results Section with Animation
                    if (uiState.filteredCocktails.isNotEmpty()) {
                        item {
                            Text(
                                text = "Filter Results (${uiState.filteredCocktails.size})",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        
                        itemsIndexed(uiState.filteredCocktails) { index, cocktail ->
                            AnimatedCocktailItem(
                                cocktail = cocktail,
                                index = index,
                                onClick = { onCocktailClick(cocktail.id) }
                            )
                        }
                    } else if (uiState.filter.hasActiveFilters() && !uiState.isLoading) {
                        // Show empty state with animation
                        item {
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No cocktails found with these filters",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                    
                    // Loading indicator when applying filters
                    if (uiState.isLoading && uiState.categories.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CocktailGlassLoading(modifier = Modifier.size(80.dp))
                            }
                        }
                    }
                }
            }
            
            // Pull-to-refresh indicator
            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

/**
 * Custom filter section with "lab equipment" styling
 */
@Composable
fun FilterSection(
    title: String,
    icon: ImageVector,
    items: List<String>,
    selectedItem: String?,
    onItemSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, 
        label = "rotation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (selectedItem != null) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedItem != null) "1" else "0",
                    color = if (selectedItem != null) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
        
        // Content when expanded
        if (expanded) {
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            
            // Selected item indicator
            if (selectedItem != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected: $selectedItem",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = { onItemSelected(null) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear selection",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Divider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            
            // Filter items
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(items) { _, item ->
                    val isSelected = item == selectedItem
                    IngredientChip(
                        text = item,
                        selected = isSelected,
                        onClick = { onItemSelected(item) }
                    )
                }
            }
        }
    }
}

/**
 * Animated cocktail item with fade and slide in effects
 */
@Composable
fun AnimatedCocktailItem(
    cocktail: Cocktail,
    index: Int,
    onClick: () -> Unit
) {
    // Create a transition state for animation
    val enterTransition = remember { MutableTransitionState(false) }
    
    // Trigger animation when composable is laid out
    LaunchedEffect(cocktail.id) {
        enterTransition.targetState = true
    }
    
    AnimatedVisibility(
        visibleState = enterTransition,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing,
                delayMillis = index * 100
            )
        ) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing,
                delayMillis = index * 100
            )
        )
    ) {
        CocktailItemCard(
            cocktail = cocktail,
            onClick = onClick
        )
    }
} 