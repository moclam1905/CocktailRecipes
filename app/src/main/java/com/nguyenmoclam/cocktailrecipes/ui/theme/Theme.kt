package com.nguyenmoclam.cocktailrecipes.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyenmoclam.cocktailrecipes.data.local.PreferencesManager

// Cocktail-themed light color scheme
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Primary.copy(alpha = 0.15f),
    onPrimaryContainer = Primary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = Secondary.copy(alpha = 0.15f),
    onSecondaryContainer = Secondary,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = Tertiary.copy(alpha = 0.15f),
    onTertiaryContainer = Tertiary,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = Surface.copy(alpha = 0.7f),
    onSurfaceVariant = OnSurface.copy(alpha = 0.7f),
    error = Secondary,
    errorContainer = Secondary.copy(alpha = 0.15f),
)

// Cocktail-themed dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryDark.copy(alpha = 0.15f),
    onPrimaryContainer = PrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryDark.copy(alpha = 0.15f),
    onSecondaryContainer = SecondaryDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryDark.copy(alpha = 0.15f),
    onTertiaryContainer = TertiaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDark.copy(alpha = 0.7f),
    onSurfaceVariant = OnSurfaceDark.copy(alpha = 0.7f),
    error = SecondaryDark,
    errorContainer = SecondaryDark.copy(alpha = 0.15f),
)

@Composable
fun CocktailRecipesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled by default to use our custom colors
    themeMode: String? = null,
    content: @Composable () -> Unit
) {
    // Determine if dark theme should be used based on theme mode
    val isDarkTheme = when(themeMode) {
        PreferencesManager.THEME_MODE_LIGHT -> false
        PreferencesManager.THEME_MODE_DARK -> true
        else -> darkTheme // Fall back to system default
    }
    
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = if (isDarkTheme) BackgroundDark.toArgb() else Background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}