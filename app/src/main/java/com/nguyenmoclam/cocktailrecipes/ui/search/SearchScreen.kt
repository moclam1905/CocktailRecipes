package com.nguyenmoclam.cocktailrecipes.ui.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.ui.base.BaseScreen
import com.nguyenmoclam.cocktailrecipes.ui.components.CocktailListItem
import com.nguyenmoclam.cocktailrecipes.ui.components.CocktailListLoadingPlaceholder
import com.nguyenmoclam.cocktailrecipes.ui.components.SearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onCocktailClick: (String) -> Unit,
    onFilterClick: () -> Unit,
    onBackPressed: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val recentlyViewed by viewModel.recentlyViewedCocktails.collectAsState()
    val isSearchByIngredient by viewModel.isSearchByIngredient.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // State for controlling the LazyColumn scroll position
    val listState = rememberLazyListState()

    // Sử dụng BaseScreen với BaseViewModel để có xử lý Rate Limit
    BaseScreen(
        viewModel = viewModel,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = "Search Cocktails",
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
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar and filter section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onSearch = { 
                        if (searchQuery.isNotBlank()) {
                            viewModel.addToSearchHistory(searchQuery)
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isSearchByIngredient,
                            onClick = { viewModel.toggleSearchByIngredient() },
                            label = { Text(if (isSearchByIngredient) "By Ingredient" else "By Name") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.List,
                                    contentDescription = "Search Type"
                                )
                            }
                        )
                        
                        TextButton(
                            onClick = onFilterClick,
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterAlt,
                                contentDescription = "Advanced Filters"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("More Filters")
                        }
                    }
                    
                    AnimatedVisibility(
                        visible = searchHistory.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        TextButton(onClick = { viewModel.clearSearchHistory() }) {
                            Text("Clear")
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear history"
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
            }
            
            // Content Area: Search results, History, Recently Viewed, or other states
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (uiState) {
                    SearchUIState.Initial -> {
                        // Prioritize showing Search History, then Recently Viewed
                        if (searchHistory.isNotEmpty()) {
                            SearchHistoryList(searchHistory, viewModel)
                        } else if (recentlyViewed.isNotEmpty()) {
                            RecentlyViewedList(recentlyViewed, onCocktailClick)
                        } else {
                            InitialSearchPrompt()
                        }
                    }
                    SearchUIState.Loading -> {
                        // Use the shimmer placeholder instead of a simple spinner
                        CocktailListLoadingPlaceholder(
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is SearchUIState.Success -> {
                        val cocktails = (uiState as SearchUIState.Success).cocktails
                        
                        // Scroll to top whenever the cocktails list changes
                        LaunchedEffect(cocktails) {
                            if (cocktails.isNotEmpty()) { // Avoid scrolling if the list becomes empty
                                listState.scrollToItem(index = 0)
                            }
                        }
                        
                        LazyColumn(
                            state = listState, // Assign the state here
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(cocktails, key = { it.id }) { cocktail ->
                                CocktailListItem(
                                    cocktail = cocktail,
                                    onClick = { 
                                        // Add to recently viewed when clicked
                                        viewModel.addToRecentlyViewed(cocktail)
                                        onCocktailClick(cocktail.id) 
                                    },
                                    onFavoriteClick = { 
                                        if(cocktail.isFavorite) {
                                            viewModel.removeFavorite(cocktail.id)
                                        } else {
                                            viewModel.saveFavorite(cocktail)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    SearchUIState.Empty -> {
                        EmptyStateView(
                            title = "No Cocktails Found",
                            message = if (isSearchByIngredient) 
                                        "Try a different ingredient or check your spelling"
                                    else
                                        "Try a different name or check your spelling"
                        )
                    }
                    is SearchUIState.Error -> {
                        ErrorView(
                            message = (uiState as SearchUIState.Error).message,
                            // Optional: Add retry logic if applicable for search
                            // onRetry = { viewModel.retrySearch() } 
                        )
                    }
                }
            }
        }
    }
}

// Extracted Composables for clarity

@Composable
fun SearchHistoryList(searchHistory: List<String>, viewModel: SearchViewModel) {
    // State for the history list (optional, but good practice)
    val historyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Recent Searches",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(state = historyListState) {
            // Use itemsIndexed for potentially more stable keys/ordering
            itemsIndexed(searchHistory, key = { index, query -> "history-$index-$query" }) { index, query ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onSearchQueryChange(query) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info, // Changed icon as per user edit
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = query,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Add divider only if not the last item
                if (index < searchHistory.lastIndex) {
                    Divider()
                }
            }
        }
    }
}

@Composable
fun RecentlyViewedList(recentlyViewed: List<CocktailMinimalInfo>, onCocktailClick: (String) -> Unit) {
     // State for the recently viewed list (optional, but good practice)
     val recentListState = rememberLazyListState()

     Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Recently Viewed",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(state = recentListState) {
            items(recentlyViewed, key = { it.id }) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCocktailClick(item.id) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.size(12.dp))
                    Text(
                        text = item.name,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Divider()
            }
        }
    }
}

@Composable
fun InitialSearchPrompt() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Search for cocktails by name or ingredients",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(32.dp)
        )
    }
}

// Simple Empty State Composable (replace with your actual component if available)
@Composable
fun EmptyStateView(title: String, message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Simple Error State Composable (replace with your actual component if available)
@Composable
fun ErrorView(message: String, modifier: Modifier = Modifier, onRetry: (() -> Unit)? = null) {
     Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
             horizontalAlignment = Alignment.CenterHorizontally,
             modifier = Modifier.padding(32.dp)
        ) {
             Text(
                text = "Error",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            if (onRetry != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
} 