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

### In Progress
- 🚧 **API Optimization Enhancements (continuing)**
  - Implementing rate limiting protection
    - Creating rate limit interceptor to track API calls
    - Implementing exponential backoff for rate limit responses
    - Adding user-facing feedback when rate limits are reached
  - Integrating analytics for API call performance
    - Creating performance tracking interceptor
    - Implementing error rate tracking and reporting

### Coming Soon
- 🔮 **New Features**
  - "Surprise Me!" Random Cocktail Feature
  - Ingredient Explorer with visual grid
  - Advanced Filtering Options
  - "Mocktail Corner" for non-alcoholic options
  - "Browse by First Letter" Feature with interactive UI
- 🔮 **Performance Optimizations**
  - Implementing memory caching for frequently accessed data
  - Optimizing image loading and caching
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
- **Testing**: JUnit, Mockito, Compose UI Testing - For robust, reliable code

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