# 🍹 CocktailRecipes - Your Pocket Bartender

<p align="center">
  <img src="https://img.icons8.com/color/96/000000/cocktail.png" alt="Cocktail Icon"/>
  <h3 align="center">Mix, Shake, and Discover Delicious Drinks!</h3>
</p>

## 🥂 Overview

CocktailRecipes is a sleek, modern Android application that brings the world of mixology to your fingertips! Whether you're a professional bartender looking for inspiration or a curious beginner wanting to impress friends at your next gathering, this app has everything you need to create the perfect drink.

Built with cutting-edge Android development practices, this app also showcases the power of AI-assisted development using:

- [BMad's Cursor Custom Agents Rules Generator](https://github.com/bmadcode/cursor-custom-agents-rules-generator) - For automatic rule and custom agent generation
- [Cursor Memory Bank](https://github.com/vanzan01/cursor-memory-bank) - For enhanced development workflow and context tracking

## ✨ Core Features

- 🔎 **Discover** - Browse a curated list of popular cocktails from around the world
- 🔍 **Search** - Find the perfect drink by name or ingredient
- ❤️ **Favorites** - Save recipes you love for quick access, even offline
- 📝 **Details** - Get comprehensive instructions, ingredient lists, and beautiful images
- ⚙️ **Settings** - Customize your experience with theme selection and cache management
- 📊 **Analytics** - View API performance metrics and optimize app experience
- 🎲 **Surprise Me!** - Shake your device to discover random cocktails with beautiful animations
- 🧪 **Filter Lab** - Apply multiple filters like category, glass type, and alcohol content
- 🌿 **Ingredient Explorer** - Browse ingredients in an interactive hexagon grid and discover related cocktails
- 🥤 **Mocktail Corner** - Explore a garden-inspired section dedicated to alcohol-free drinks

## 🧪 Did You Know?

> The world's most expensive cocktail is the "Diamonds Are Forever" martini, priced at $22,600 at the Ritz-Carlton in Tokyo. It includes a one-carat diamond at the bottom of the glass!

## 🌐 Data Source

All our delicious recipes come from [TheCocktailDB API](https://www.thecocktaildb.com/api.php), providing comprehensive information about cocktails, including ingredients, instructions, and mouthwatering images.

## 👥 Target Audience

- 🍸 Cocktail enthusiasts looking for new recipes
- 🏠 Home bartenders wanting to expand their repertoire
- 🎉 Party planners seeking the perfect signature drink
- 🔰 Beginners taking their first steps into mixology

## 🏗️ Project Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern with Clean Architecture principles:

- **Data Layer**: API integration and local database for offline access
- **Domain Layer**: Business logic and use cases to keep your drinks perfect
- **UI Layer**: Beautiful Jetpack Compose UI components and responsive ViewModels
- **BaseViewModel Pattern**: All ViewModels inherit from a common base class for standardized state management and error handling

## 🚀 Current Progress

### Completed
- ✅ **Project Setup & Architecture**
  - Created project structure with MVVM architecture
  - Set up dependency injection with Hilt
  - Configured UI theme and styling with dark mode support
- ✅ **API Integration**
  - Created data models for API responses
  - Implemented network layer with Retrofit
  - Set up repository pattern with caching strategy
  - Added error handling for API calls (including custom Moshi adapter for API quirks)
- ✅ **Core Features**
  - Home screen with popular cocktails list (including pull-to-refresh, loading/error/empty states)
  - Cocktail detail screen (layout, ingredients, instructions, image loading)
  - Search functionality (by name/ingredient, history, recently viewed, refined UI/state management)
  - Favorites feature (database, DAO, toggle functionality, favorites screen)
- ✅ **Polishing & UI Enhancements**
  - Added animated loading indicators (pulsating, shimmer effect, cocktail glass animation)
  - Created contextual error screens with different error types and retry actions
  - Added empty state designs for various scenarios (empty favorites, no search results, etc.)
  - Enhanced UI states across all screens with smooth transitions
- ✅ **Offline Capability**
  - Implemented database caching strategy for offline viewing of cocktails
  - Set up WorkManager for background syncing of cocktail data
  - Added NetworkMonitor for detecting and responding to connection changes
  - Created offline mode indicator with sync action
- ✅ **Testing Infrastructure**
  - Created fake/mock implementations of data sources
  - Implemented test data factory for consistent test data
  - Added comprehensive testing utilities for repository testing
- ✅ **Unit Testing**
  - Wrote unit tests for repository implementations
  - Created ViewModel tests
  - Tested use cases and business logic
  - Added testing utilities and fixtures
  - Completed FakeCocktailRepository by implementing the missing isFavorite method
- ✅ **UI Testing**
  - Set up Compose UI testing framework with test utilities
  - Implemented navigation tests for all app paths
  - Created tests for all main user flows
  - Added accessibility compliance testing
  - Simplified UI tests to resolve dependency and reference issues
- ✅ **Bug Fixes & Refinements**
  - Fixed database setup issues (duplicate classes, missing migrations)
  - Resolved Hilt duplicate bindings
  - Corrected repository logic for details fetching and favorites
  - Addressed API JSON parsing errors
  - Fixed database operations to run on background threads, preventing UI freezes
- ✅ **Settings Screen**
  - Created PreferencesManager using DataStore for preferences
  - Implemented theme selection capabilities
  - Added cache management options
  - Created About section with app information
  - Connected settings to app behavior
- ✅ **API Optimization Enhancements**
  - Added HTTP caching to reduce API calls
    - Configured OkHttp cache interceptor with appropriate cache size
    - Implemented cache control headers for GET requests
    - Added cache invalidation logic for time-sensitive data
    - Created mechanism to force refresh data regardless of cache
  - Implemented rate limiting protection
    - Created rate limit interceptor to track API calls
    - Added exponential backoff for rate limit responses
    - Implemented comprehensive UI feedback when rate limits are reached
    - Created BaseViewModel pattern to standardize rate limit error handling
  - Added analytics for API call performance
    - Created performance tracking interceptor to log call durations
    - Implemented error rate tracking mechanism
    - Added reporting system for slow or failed API calls
    - Created API performance dashboard with metrics visualization
- ✅ **"Surprise Me!" Random Cocktail Feature**
  - Created a "Surprise Me" floating action button on Home screen
  - Implemented Random Cocktail API integration using random.php endpoint
  - Added Shake-to-Activate gesture functionality with ShakeDetector
  - Implemented 3D Card Flip animation for result reveal
  - Created custom loading animation (pouring cocktail)
  - Added navigation to cocktail details on selection
- ✅ **Ingredient Explorer Feature**
  - Created Ingredient Explorer screen with hexagon grid layout
  - Implemented API integration for listing all ingredients
  - Added interactive animations with zoom/explode effects
  - Created mind-map visualization for ingredient relationships
  - Implemented navigation between ingredients and related cocktails
- ✅ **Advanced Filtering Options**
  - Created filter screen with "mixology lab" theme
  - Implemented category, glass type, and alcoholic/non-alcoholic filtering
  - Added animated filter results with staggered transitions
  - Created filter preferences persistence using DataStore
  - Implemented filter combination logic for complex searches
  - Optimized performance with parallel API loading
- ✅ **Architectural Improvements**
  - Standardized all ViewModels to use BaseViewModel pattern
  - Implemented consistent event-based approach with handleEvent method
  - Created unified error handling system across the application
  - Updated all UI screens to use proper coroutine scope for suspend functions
  - Added consistent user feedback for API errors
- ✅ **"Mocktail Corner" Feature**
  - Added dedicated section for non-alcoholic drinks
  - Designed "garden-inspired" UI theme with animations
  - Implemented special visual effects (water/fruit animations)
  - Added promotional content highlighting benefits of alcohol-free options
  - Created wave-style scroll animation
  - Implemented toggle for animations (accessibility feature)
  - Added support for landscape mode
  - Optimized dark mode appearance
  - Added in-memory caching for better performance
  - Created unit tests for MocktailViewModel
  - Added UI tests for MocktailCornerScreen

### In Progress
- 🚧 **"Mocktail Corner" Feature**
  - Adding dedicated section for non-alcoholic drinks
  - Designing "garden-inspired" UI theme
  - Implementing special visual effects and animations

### Coming Soon
- 🔮 **"Browse by First Letter" Feature**
  - Creating alphabetical browsing with rotary wheel UI
  - Adding "pouring results" animation when letter is selected
  - Implementing lazy loading for letter-based results
- 🔮 **General UI/UX Refinements**
  - Exploring neumorphism design style
  - Implementing subtle haptic feedback
  - Designing bottom navigation bar with bar theme
- 🔮 **Performance Optimizations**
  - Implementing memory caching for frequently accessed data
  - Optimizing image loading and caching further
  - Enhancing app startup time

## 🛠️ Tech Stack

- **UI**: Jetpack Compose - For beautiful, fluid interfaces
- **Architecture**: MVVM + Clean Architecture - For maintainable, testable code
- **Networking**: Retrofit + OkHttp - For reliable API communication
- **Database**: Room - For seamless local storage
- **Dependency Injection**: Hilt - For clean, modular components
- **Image Loading**: Coil - For fast, efficient image loading
- **Concurrency**: Kotlin Coroutines + Flow - For smooth, reactive experiences
- **Offline Support**: WorkManager - For background processing and syncing
- **Preferences**: DataStore - For storing user preferences
- **Testing**: JUnit, Mockito, Turbine, Compose UI Testing - For robust, reliable code

## 🚀 Getting Started

1. Clone this repository
2. Open the project in Android Studio
3. Build and run on your device or emulator
4. Start mixing delicious drinks!

## 📱 Screenshots

*Coming soon! Stay tuned for visual previews of our beautiful UI.*

## 🤝 Contributing

Contributions are welcome! Feel free to:
- Report bugs
- Suggest new features
- Submit pull requests

## 📄 License

This project is available under the MIT License. 