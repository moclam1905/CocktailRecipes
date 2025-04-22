# Technical Context

## Architecture
- MVVM with Clean Architecture
  - UI Layer: Jetpack Compose UI + ViewModels
  - Domain Layer: Use cases, Repository interfaces
  - Data Layer: Repository implementations, local and remote data sources

## Key Components
- **UI**: Jetpack Compose with Material 3 design
- **Navigation**: Jetpack Navigation Compose
- **State Management**: StateFlow for UI state, collectAsStateWithLifecycle for Compose
- **Dependency Injection**: Hilt
- **Networking**: Retrofit with OkHttp interceptors
- **Local Storage**: Room Database
- **Image Loading**: Coil
- **Concurrency**: Kotlin Coroutines + Flow
- **Background Processing**: WorkManager for syncing
- **Preferences**: DataStore for storing user preferences
- **Testing**: JUnit, Mockito, Turbine, Compose Testing, MockWebServer

## Project Structure
```
com.nguyenmoclam.cocktailrecipes/
  ├─ data/               # Data layer
  │  ├─ common/          # Common utils, error handling
  │  ├─ local/           # Room database, entities, DAOs
  │  │  └─ preferences/  # DataStore preferences management
  │  ├─ mapper/          # Data mapping functions
  │  ├─ model/           # API/DB model classes  
  │  ├─ network/         # Network monitoring
  │  ├─ remote/          # API services, interceptors
  │  ├─ repository/      # Repository implementations
  │  └─ worker/          # WorkManager classes
  ├─ di/                 # Dependency injection modules
  ├─ domain/             # Domain layer
  │  ├─ model/           # Domain models
  │  └─ repository/      # Repository interfaces
  └─ ui/                 # UI layer
     ├─ base/            # Base classes for UI
     ├─ components/      # Reusable composables
     ├─ detail/          # Cocktail detail screen
     ├─ favorites/       # Favorites screen
     ├─ home/            # Home screen
     ├─ navigation/      # Navigation configuration
     ├─ search/          # Search screen
     ├─ settings/        # Settings screen
     ├─ theme/           # Theme definition
     └─ util/            # UI utilities
```

## Design Patterns
- **Repository Pattern**: Single source of truth for data
- **Dependency Injection**: Hilt for providing dependencies
- **Factory Pattern**: For creating objects with specific configurations
- **Observer Pattern**: Flow/StateFlow for reactive UI updates
- **Strategy Pattern**: For different data source strategies (API vs local)
- **Builder Pattern**: Used for complex object construction (e.g., Retrofit, OkHttp)

## Key Technologies
- **UI**: Jetpack Compose for modern declarative UI
- **Navigation**: Compose Navigation with animations for single-activity architecture
- **DI**: Hilt for dependency injection
- **Networking**: Retrofit with OkHttp for API calls, Moshi for JSON parsing
- **Local Storage**: Room Database for favorites and caching
- **Concurrency**: Kotlin Coroutines & Flow for async operations
- **Image Loading**: Coil for efficient image loading and caching
- **Error Handling**: Custom Resource wrapper with retry mechanism
- **State Management**: StateFlow with collectAsStateWithLifecycle
- **Animations**: Compose animations and Material Motion for smooth transitions
- **Search**: Debounced search with query history and filtering options
- **Favorites**: Local Room database with type converters for complex data

## API
Using TheCocktailDB API for cocktail data.
- Remote data source properly handles network errors and provides retry logic
- Repository pattern abstracts the data source from the UI layer
- Network monitoring implemented to avoid unnecessary API calls when offline
- API endpoints include search, lookup, filter, and random cocktail retrieval
- Repository implements caching strategy to reduce API calls

## Testing Architecture
- **Test Doubles**: Fake implementations of repositories and data sources
- **Test Data Factory**: Central utility for consistent test data generation
- **Repository Testing**: Tests using fake data sources for controlled testing
- **JUnit & Mockito**: For unit testing business logic
- **Coroutines Test**: For testing asynchronous operations

## Module Structure
- **data**: API interfaces, repositories, and data sources
  - **remote**: API services and data sources
  - **local**: Room database, DAOs and entity classes
  - **common**: Shared utilities and resource handling
  - **model**: Data transfer objects
  - **mapper**: Conversion between data models
- **domain**: Use cases and business logic
  - **model**: Domain entities
  - **repository**: Repository interfaces
- **ui**: Compose UI components and ViewModels
  - **home**: Home screen components and ViewModel
  - **detail**: Detail screen components and ViewModel
  - **search**: Search screen with filtering capabilities
  - **favorites**: Favorites management and storage
  - **components**: Reusable Compose UI components
  - **theme**: App theme definitions
  - **base**: Base classes for common functionality
  - **navigation**: Navigation graph and destinations
- **di**: Dependency injection modules

## Testing Strategy
- Unit tests for ViewModels, repositories, and use cases
- UI tests for critical user flows
- QA process for feature verification