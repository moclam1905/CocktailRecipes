# Technical Context

## Architecture
Following MVVM with Clean Architecture pattern:
- **Data layer**: Remote and local data sources, repository implementations
- **Domain layer**: Models, repository interfaces, use cases
- **Presentation layer**: UI components, ViewModels, state management

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