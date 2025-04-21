package com.nguyenmoclam.cocktailrecipes.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interface defining network monitoring capability
 */
interface NetworkMonitor {
    /**
     * Returns a flow that emits when network status changes
     */
    val isOnline: Flow<Boolean>
    
    /**
     * Returns current network status
     */
    fun isCurrentlyOnline(): Boolean
}

/**
 * Implementation of NetworkMonitor that uses the Android ConnectivityManager
 * to observe network status changes
 */
@Singleton
class NetworkMonitorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NetworkMonitor {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    /**
     * Flow that emits true when the device is online and false when it's offline
     */
    override val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Timber.d("Network available")
                trySend(true)
            }
            
            override fun onLost(network: Network) {
                Timber.d("Network lost")
                trySend(false)
            }
            
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val hasValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                val isOnline = hasInternet && hasValidated
                
                Timber.d("Network capabilities changed. Internet: $hasInternet, Validated: $hasValidated")
                trySend(isOnline)
            }
        }
        
        // Start with current status
        trySend(isCurrentlyOnline())
        
        // Register for network callbacks
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        
        connectivityManager.registerNetworkCallback(request, callback)
        
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.conflate()
    
    /**
     * Checks if the device currently has internet connectivity
     */
    override fun isCurrentlyOnline(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
               capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
} 