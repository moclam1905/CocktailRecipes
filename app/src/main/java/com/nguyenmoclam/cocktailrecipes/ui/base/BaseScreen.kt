package com.nguyenmoclam.cocktailrecipes.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.nguyenmoclam.cocktailrecipes.ui.components.RateLimitErrorObserver

/**
 * BaseScreen composable that provides common functionality for all screens:
 * - Scaffold with SnackbarHost
 * - Rate limit error handling
 * - Common layout structure
 *
 * @param viewModel The ViewModel for this screen (must be a subclass of BaseViewModel)
 * @param topBar Optional composable for the top app bar
 * @param bottomBar Optional composable for the bottom navigation bar
 * @param content Main screen content
 */
@Composable
fun <S, E> BaseScreen(
    viewModel: BaseViewModel<S, E>,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (paddingValues: PaddingValues) -> Unit
) {
    // Create SnackbarHostState for showing messages
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Observe rate limit errors
    RateLimitErrorObserver(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState
    )
    
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                //.padding(paddingValues)
        ) {
            content(paddingValues)
        }
    }
}

