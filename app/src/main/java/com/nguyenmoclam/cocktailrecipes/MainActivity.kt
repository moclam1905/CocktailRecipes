package com.nguyenmoclam.cocktailrecipes

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.work.WorkManager
import com.nguyenmoclam.cocktailrecipes.data.local.PreferencesManager
import com.nguyenmoclam.cocktailrecipes.data.network.NetworkMonitor
import com.nguyenmoclam.cocktailrecipes.data.worker.CocktailSyncWorker
import com.nguyenmoclam.cocktailrecipes.ui.components.OfflineIndicator
import com.nguyenmoclam.cocktailrecipes.ui.navigation.AppNavigation
import com.nguyenmoclam.cocktailrecipes.ui.theme.CocktailRecipesTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var networkMonitor: NetworkMonitor
    
    @Inject
    lateinit var workManager: WorkManager
    
    @Inject
    lateinit var preferencesManager: PreferencesManager
    
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Collect theme mode preference from DataStore
            val themeMode by preferencesManager.themeMode.collectAsState(initial = PreferencesManager.THEME_MODE_SYSTEM)
            
            CocktailRecipesTheme(
                themeMode = themeMode
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CocktailApp(
                        networkMonitor = networkMonitor,
                        onSyncRequest = { 
                            CocktailSyncWorker.requestImmediateSync(workManager)
                            Timber.d("Manual sync requested")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CocktailApp(
    networkMonitor: NetworkMonitor,
    onSyncRequest: () -> Unit
) {
    // Collect network status
    val isOffline by networkMonitor.isOnline
        .collectAsState(initial = !networkMonitor.isCurrentlyOnline())
        .let { state -> remember { derivedStateOf { !state.value } } }
    
    // Create a SnackbarHostState to display snackbar messages app-wide
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                //.padding(paddingValues)
        ) {
            // Main app content
            AppNavigation(snackbarHostState = snackbarHostState)

            // Offline indicator at the top of the screen
            OfflineIndicator(
                isOffline = isOffline,
                onSyncClick = onSyncRequest,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}