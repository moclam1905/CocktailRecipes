plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.nguyenmoclam.cocktailrecipes"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nguyenmoclam.cocktailrecipes"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    hilt {
        enableAggregatingTask = false
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.compose.material)

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Retrofit & OkHttp
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp.logging)

    // Moshi for JSON parsing
    implementation(libs.moshi.core)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Coil for image loading
    implementation(libs.coil.compose)

    // Navigation with animations
    implementation(libs.androidx.navigation.compose)
    implementation (libs.accompanist.navigation.material)
    implementation (libs.accompanist.navigation.animation)
    implementation(libs.accompanist.systemuicontroller)
    
    // Animation libraries
    implementation(libs.androidx.animation.graphics)
    implementation(libs.androidx.animation.core)
    implementation(libs.androidx.animation)
    implementation(libs.androidx.ui.util)
    implementation(libs.material.motion.compose.core)
    
    // WorkManager for background processing
    implementation(libs.workmanager.runtime)
    implementation(libs.workmanager.hilt)
    
    // Logging
    implementation(libs.timber)
    
    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.core)
    testImplementation(libs.coroutines.android)
    testImplementation(libs.coroutines.test)
    
    // Mockito 
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    
    // Turbine for Flow testing
    testImplementation(libs.turbine)
    
    // MockWebServer for API testing
    testImplementation(libs.mockwebserver)
    
    // Android Testing - using specific versions instead of BOM for compatibility
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    
    // Compose testing with specific versions
    androidTestImplementation(libs.androidx.ui.test)
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(libs.core.ktx)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.coroutines.test)
    
    // Debug implementations for UI testing
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    // Material Icons Extended
    implementation(libs.androidx.compose.material.icons.extended)
}