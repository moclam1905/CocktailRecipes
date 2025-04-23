package com.nguyenmoclam.cocktailrecipes.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nguyenmoclam.cocktailrecipes.R
import com.nguyenmoclam.cocktailrecipes.data.local.PreferencesManager
import com.nguyenmoclam.cocktailrecipes.domain.repository.SettingsRepository
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    onOpenApiDashboard: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Get string resources within Composable scope
    val cacheClearedMessage = stringResource(R.string.cache_cleared)
    val backContentDesc = stringResource(R.string.back)

    // Show snackbar when cache is cleared
    LaunchedEffect(uiState.cacheCleared) {
        if (uiState.cacheCleared) {
            // Use the retrieved string here
            snackbarHostState.showSnackbar(
                message = cacheClearedMessage
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed,
                        // Use the retrieved string here
                        modifier = Modifier.semantics {
                            contentDescription = backContentDesc
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            // stringResource is okay here as it's directly in Icon's content
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Selection
            ThemeSelectionSection(
                currentTheme = uiState.themeMode,
                onThemeSelected = { theme ->
                    scope.launch {
                        viewModel.handleEvent(SettingsEvent.SetThemeMode(theme))
                    }
                }
            )

            Divider()

            // Cache Clearing
            CacheClearingSection(
                isClearing = uiState.isCacheClearing,
                onClearCache = {
                    scope.launch {
                        viewModel.handleEvent(SettingsEvent.ShowClearCacheConfirmation)
                    }
                }
            )

            Divider()
            
            // API Performance Dashboard
            ApiPerformanceSection(
                onOpenDashboard = onOpenApiDashboard
            )
            
            Divider()

            // About Section
            AboutSection(appVersion = uiState.appVersion)
        }
    }

    // Confirmation dialog for cache clearing
    if (uiState.showClearCacheConfirmation) {
        AlertDialog(
            onDismissRequest = {
                scope.launch {
                    viewModel.handleEvent(SettingsEvent.DismissClearCacheConfirmation)
                }
            },
            title = { Text(stringResource(R.string.cache_confirmation_title)) },
            text = {
                Text(stringResource(R.string.cache_confirmation_message))
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            viewModel.handleEvent(SettingsEvent.ClearCache)
                        }
                    }
                ) {
                    Text(stringResource(R.string.clear_button))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            viewModel.handleEvent(SettingsEvent.DismissClearCacheConfirmation)
                        }
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun ThemeSelectionSection(
    currentTheme: String,
    onThemeSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Brightness4,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(R.string.theme_title),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column(Modifier.selectableGroup()) {
            ThemeOption(
                text = stringResource(R.string.theme_system),
                icon = Icons.Default.BrightnessAuto,
                selected = currentTheme == PreferencesManager.THEME_MODE_SYSTEM,
                onClick = { onThemeSelected(PreferencesManager.THEME_MODE_SYSTEM) }
            )

            ThemeOption(
                text = stringResource(R.string.theme_light),
                icon = Icons.Default.Brightness7,
                selected = currentTheme == PreferencesManager.THEME_MODE_LIGHT,
                onClick = { onThemeSelected(PreferencesManager.THEME_MODE_LIGHT) }
            )

            ThemeOption(
                text = stringResource(R.string.theme_dark),
                icon = Icons.Default.Brightness4,
                selected = currentTheme == PreferencesManager.THEME_MODE_DARK,
                onClick = { onThemeSelected(PreferencesManager.THEME_MODE_DARK) }
            )
        }
    }
}

@Composable
fun ThemeOption(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp)
            .semantics {
                // Pass the already resolved string
                contentDescription = text
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null // null because we're handling the click on the row
        )
        Icon(
            imageVector = icon,
            contentDescription = null, // Icon is decorative here, text provides description
            modifier = Modifier.padding(start = 8.dp, end = 16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun CacheClearingSection(
    isClearing: Boolean,
    onClearCache: () -> Unit
) {
    // Get string resource within Composable scope
    val clearButtonDesc = stringResource(R.string.clear_button)

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.cache_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.cache_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isClearing) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(8.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Button(
                    onClick = onClearCache,
                    // Use the retrieved string here
                    modifier = Modifier.semantics {
                        contentDescription = clearButtonDesc
                    }
                ) {
                    // stringResource is okay here as it's directly in Button's content
                    Text(stringResource(R.string.clear_button))
                }
            }
        }
    }
}

@Composable
fun ApiPerformanceSection(
    onOpenDashboard: () -> Unit
) {
    val apiPerformanceContentDesc = stringResource(R.string.view_dashboard)
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = stringResource(R.string.api_performance_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.api_performance_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Button(
                onClick = onOpenDashboard,
                modifier = Modifier.semantics {
                    contentDescription = apiPerformanceContentDesc
                }
            ) {
                Text(stringResource(R.string.view_dashboard))
            }
        }
    }
}

@Composable
fun AboutSection(appVersion: String) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(R.string.about_title),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.about_version, appVersion),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.about_description),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.about_data_source),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    CocktailRecipesTheme {
        val previewViewModel = object : SettingsViewModel(
            FakeSettingsRepository()
        ) {
            override val uiState: StateFlow<SettingsUiState> = MutableStateFlow(
                SettingsUiState(
                    themeMode = PreferencesManager.THEME_MODE_SYSTEM,
                    appVersion = "1.0.0 (1)",
                    isCacheClearing = false,
                    cacheCleared = false,
                    showClearCacheConfirmation = false
                )
            )
        }

        SettingsScreen(
            onBackPressed = {},
            viewModel = previewViewModel
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun SettingsScreenDarkPreview() {
    CocktailRecipesTheme(darkTheme = true) {
        val previewViewModel = object : SettingsViewModel(
            FakeSettingsRepository()
        ) {
            override val uiState: StateFlow<SettingsUiState> = MutableStateFlow(
                SettingsUiState(
                    themeMode = PreferencesManager.THEME_MODE_DARK,
                    appVersion = "1.0.0 (1)",
                    isCacheClearing = false,
                    cacheCleared = false,
                    showClearCacheConfirmation = false
                )
            )
        }

        SettingsScreen(
            onBackPressed = {},
            viewModel = previewViewModel
        )
    }
}

// Simple fake repository for previews
private class FakeSettingsRepository : SettingsRepository {
    override fun getThemeMode(): Flow<String> = flowOf(PreferencesManager.THEME_MODE_SYSTEM)
    override suspend fun setThemeMode(mode: String) {}
    override suspend fun clearAppCache(): Boolean = true
    override suspend fun clearApiCache(): Boolean = true
    override suspend fun getAppVersion(): String = "1.0.0 (1)"
} 