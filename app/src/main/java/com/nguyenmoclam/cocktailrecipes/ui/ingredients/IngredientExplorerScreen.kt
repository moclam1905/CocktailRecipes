package com.nguyenmoclam.cocktailrecipes.ui.ingredients

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.IngredientItem
import com.nguyenmoclam.cocktailrecipes.ui.components.CocktailItem
import com.nguyenmoclam.cocktailrecipes.ui.components.EmptySearchResult
import com.nguyenmoclam.cocktailrecipes.ui.components.ErrorView
import com.nguyenmoclam.cocktailrecipes.ui.components.LoadingIndicator
import com.nguyenmoclam.cocktailrecipes.ui.components.PulsatingLoadingIndicator
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientExplorerScreen(
    onBackPressed: () -> Unit,
    onCocktailClick: (String) -> Unit,
    viewModel: IngredientExplorerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val titleText = if (uiState.selectedIngredient == null) {
                        "Ingredient Explorer"
                    } else {
                        "Cocktails with ${uiState.selectedIngredient?.name ?: ""}"
                    }
                    Text(text = titleText)
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.selectedIngredient != null) {
                        IconButton(onClick = { viewModel.clearSelectedIngredient() }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear selection")
                        }
                    }
                    
                    IconButton(onClick = { viewModel.loadIngredients(true) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (uiState.selectedIngredient != null) {
                FloatingActionButton(
                    onClick = { viewModel.clearSelectedIngredient() },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back to ingredients"
                    )
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
                uiState.isLoading -> {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }
                uiState.error != null -> {
                    ErrorView(
                        message = uiState.error ?: "Unknown error occurred",
                        onRetry = { viewModel.loadIngredients(true) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                uiState.ingredients.isEmpty() -> {
                    EmptySearchResult(
                        message = "No ingredients found",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    IngredientExplorerContent(
                        uiState = uiState,
                        onIngredientClick = { viewModel.selectIngredient(it) },
                        onCocktailClick = onCocktailClick
                    )
                }
            }
        }
    }
}

@Composable
private fun IngredientExplorerContent(
    uiState: IngredientExplorerUiState,
    onIngredientClick: (IngredientItem) -> Unit,
    onCocktailClick: (String) -> Unit
) {
    var explodingIngredient by remember { mutableStateOf<IngredientItem?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var showAllCocktails by remember { mutableStateOf(false) }

    // Reset showAllCocktails when selected ingredient changes or becomes null
    LaunchedEffect(uiState.selectedIngredient) {
        if (uiState.selectedIngredient == null) {
            showAllCocktails = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Show grid of ingredients
        AnimatedVisibility(
            visible = uiState.selectedIngredient == null && explodingIngredient == null,
            enter = fadeIn(),
            exit = fadeOut(animationSpec = tween(durationMillis = 150))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.ingredients, key = { it.id }) { ingredient ->
                    HexagonIngredientItem(
                        ingredient = ingredient,
                        onClick = {
                            coroutineScope.launch {
                                showAllCocktails = false // Reset view when selecting new ingredient
                                explodingIngredient = ingredient
                                onIngredientClick(ingredient)
                            }
                        }
                    )
                }
            }
        }
        
        // Show exploding animation
        explodingIngredient?.let { ingredient ->
            val scale = remember { Animatable(1f) }
            val alpha = remember { Animatable(1f) }
            LaunchedEffect(ingredient) {
                launch {
                    scale.animateTo(
                        targetValue = 3f,
                        animationSpec = tween(durationMillis = 300)
                    )
                }
                launch {
                    alpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 300)
                    )
                }
                explodingIngredient = null
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                HexagonIngredientItem(
                    ingredient = ingredient,
                    onClick = {},
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale.value)
                        .graphicsLayer { this.alpha = alpha.value }
                )
            }
        }
        
        // Show Loading / Mind Map / Full List / Empty State for selected ingredient
        AnimatedVisibility(
            visible = uiState.selectedIngredient != null,
            enter = fadeIn(animationSpec = tween(delayMillis = 300)),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Center content vertically
            ) {
                // Show loading indicator when loading cocktails for the selected ingredient
                if (uiState.isLoading) {
                    PulsatingLoadingIndicator(modifier = Modifier.padding(32.dp))
                } else {
                    // Show content only when not loading
                    if (uiState.cocktails.isNotEmpty()) {
                        if (!showAllCocktails) {
                            IngredientMindMapLayout(
                                centerIngredient = uiState.selectedIngredient!!,
                                relatedCocktails = uiState.cocktails,
                                onCocktailClick = onCocktailClick,
                                maxItemsToShow = 8,
                                modifier = Modifier.weight(1f) // MindMap takes available space
                            )
                            if (uiState.cocktails.size > 8) {
                                TextButton(
                                    onClick = { showAllCocktails = true },
                                    modifier = Modifier.padding(vertical = 8.dp) // Remove align modifier, rely on Column
                                ) {
                                    Text("View All ${uiState.cocktails.size} Cocktails")
                                }
                            }
                        } else {
                            CocktailGrid(
                                cocktails = uiState.cocktails,
                                onCocktailClick = onCocktailClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else { // cocktails are empty and not loading
                        EmptySearchResult(
                            message = uiState.selectedIngredient?.let { "No cocktails found with ${it.name}" } ?: "No cocktails found",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientMindMapLayout(
    centerIngredient: IngredientItem,
    relatedCocktails: List<Cocktail>,
    onCocktailClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    maxItemsToShow: Int = 8 // Parameter to limit items
) {
    val centerItemSize = 120.dp
    val cocktailItemSize = 90.dp // Slightly smaller items
    val radiusFactor = 1.9f // Decrease radius factor to bring items closer
    val rotationAngle = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val displayedCocktails = relatedCocktails.take(maxItemsToShow)

    Layout(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    // Calculate rotation based on horizontal drag relative to center
                    // This is a simplified calculation, could be improved with atan2
                    val rotationDelta = -dragAmount.x / (3 * PI).toFloat() // Adjust sensitivity
                    coroutineScope.launch {
                        rotationAngle.snapTo(rotationAngle.value + rotationDelta) // Snap for direct control
                    }
                }
            },
        content = {
            // Center Ingredient Item
            HexagonIngredientItem(
                ingredient = centerIngredient,
                onClick = {},
                modifier = Modifier.size(centerItemSize)
            )

            // Related Cocktail Items
            displayedCocktails.forEach { cocktail ->
                CocktailItem(
                    cocktail = cocktail,
                    onClick = { onCocktailClick(cocktail.id) },
                    modifier = Modifier
                        .size(cocktailItemSize) // Apply new size
                )
            }
        }
    ) { measurables, constraints ->
        if (measurables.isEmpty()) {
            return@Layout layout(0, 0) {}
        }
        val centerPlaceable = measurables[0].measure(Constraints())
        val cocktailPlaceables = measurables.drop(1).map {
            it.measure(Constraints(maxWidth = cocktailItemSize.roundToPx(), maxHeight = cocktailItemSize.roundToPx())) // Use new size
        }

        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val centerX = width / 2
        val centerY = height / 2

        val radius = max(centerItemSize.toPx() * radiusFactor, // Use new radiusFactor
                       min(centerX - cocktailItemSize.toPx() / 2, centerY - cocktailItemSize.toPx() / 2))

        layout(width, height) {
            centerPlaceable.placeRelative(
                x = centerX - centerPlaceable.width / 2,
                y = centerY - centerPlaceable.height / 2
            )

            if (cocktailPlaceables.isNotEmpty()) {
                val angleStep = (2 * PI / cocktailPlaceables.size).toFloat()
                cocktailPlaceables.forEachIndexed { index, placeable ->
                    val currentAngle = angleStep * index - (PI / 2).toFloat() + Math.toRadians(rotationAngle.value.toDouble()).toFloat()
                    val itemX = centerX + (radius * cos(currentAngle)).toInt() - placeable.width / 2
                    val itemY = centerY + (radius * sin(currentAngle)).toInt() - placeable.height / 2
                    placeable.placeRelative(x = itemX, y = itemY)
                }
            }
        }
    }
}

@Composable
fun HexagonIngredientItem(
    ingredient: IngredientItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val hexagonShape = remember { HexagonShape() }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(hexagonShape)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = hexagonShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        AsyncImage(
            model = ingredient.getImageUrl(),
            contentDescription = ingredient.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
            error = painterResource(id = R.drawable.placeholder_cocktail),
            placeholder = painterResource(id = R.drawable.placeholder_cocktail)
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = ingredient.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun CocktailGrid(
    cocktails: List<Cocktail>,
    onCocktailClick: (String) -> Unit,
    modifier: Modifier = Modifier // Added modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier // Use modifier
    ) {
        items(cocktails) { cocktail ->
            CocktailItem(
                cocktail = cocktail,
                onClick = { onCocktailClick(cocktail.id) }
            )
        }
    }
} 