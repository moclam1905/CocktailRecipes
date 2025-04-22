package com.nguyenmoclam.cocktailrecipes.data.remote

import android.content.Context
import com.nguyenmoclam.cocktailrecipes.data.common.DrinksListAdapter
import com.nguyenmoclam.cocktailrecipes.data.remote.interceptor.PerformanceTrackingInterceptor
import com.nguyenmoclam.cocktailrecipes.data.remote.interceptor.RateLimitInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt module that provides networking-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    private const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB cache
    private const val CACHE_DIRECTORY = "http_cache"
    
    /**
     * Provides a Moshi instance configured for the app
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(DrinksListAdapter.FACTORY)
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /**
     * Provides the HTTP cache for OkHttpClient
     */
    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheDir = File(context.cacheDir, CACHE_DIRECTORY)
        return Cache(cacheDir, CACHE_SIZE)
    }
    
    /**
     * Provides an OkHttpClient with logging, timeouts and enhanced caching
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        rateLimitInterceptor: RateLimitInterceptor,
        performanceInterceptor: PerformanceTrackingInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Use NONE in production
        }
        
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                var request = chain.request()
                // Build Cache-Control based on network availability and request method
                val cacheControl = if (request.method == "GET") {
                    // Use cache for 1 hour if available, otherwise force network if possible
                    CacheControl.Builder()
                        .maxAge(1, TimeUnit.HOURS)
                        .build()
                } else {
                    CacheControl.FORCE_NETWORK // Non-GET requests always hit the network
                }
                
                request = request.newBuilder()
                    .cacheControl(cacheControl)
                    .build()
                
                val response = chain.proceed(request)
                
                // Re-write response cache control to ensure GET requests are cached
                if (request.method == "GET") {
                    response.newBuilder()
                        .header("Cache-Control", cacheControl.toString())
                        .build()
                } else {
                    response
                }
            }
            // Add performance tracking first to measure complete call time including other interceptors
            .addInterceptor(performanceInterceptor)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(rateLimitInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    /**
     * Provides the Retrofit instance
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    /**
     * Provides the CocktailApiService implementation
     */
    @Provides
    @Singleton
    fun provideCocktailApiService(retrofit: Retrofit): CocktailApiService {
        return retrofit.create(CocktailApiService::class.java)
    }
} 