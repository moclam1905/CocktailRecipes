package com.nguyenmoclam.cocktailrecipes.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme

/**
 * A wrapper composable that applies the app theme for testing
 */
@Composable
fun TestTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CocktailRecipesTheme(darkTheme = darkTheme) {
        content()
    }
} 