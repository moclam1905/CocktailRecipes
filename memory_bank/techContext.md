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
- **Local Storage**: Room Database for caching and favorites
- **Concurrency**: Kotlin Coroutines & Flow for async operations
- **Image Loading**: Coil for efficient image loading and caching
- **Error Handling**: Custom Resource wrapper with retry mechanism
- **State Management**: StateFlow with collectAsStateWithLifecycle
- **Animations**: Compose animations and Material Motion for smooth transitions

## API
Using TheCocktailDB API for cocktail data.
- Remote data source properly handles network errors and provides retry logic
- Repository pattern abstracts the data source from the UI layer
- Network monitoring implemented to avoid unnecessary API calls when offline
- API endpoints include search, lookup, filter, and random cocktail retrieval

## Testing Architecture
- **Test Doubles**: Fake implementations of repositories and data sources
- **Test Data Factory**: Central utility for consistent test data generation
- **Repository Testing**: Tests using fake data sources for controlled testing
- **JUnit & Mockito**: For unit testing business logic
- **Coroutines Test**: For testing asynchronous operations

## Module Structure
- **data**: API interfaces, repositories, and data sources
- **domain**: Use cases and business logic
- **ui**: Compose UI components and ViewModels
  - **home**: Home screen components and ViewModel
  - **detail**: Detail screen components and ViewModel
  - **search**: Search functionality (in progress)
  - **favorites**: Favorites management (in progress)
  - **components**: Reusable Compose UI components
  - **theme**: App theme definitions
  - **base**: Base classes for common functionality
- **di**: Dependency injection modules

## Testing Strategy
- Unit tests for ViewModels, repositories, and use cases
- UI tests for critical user flows
- QA process for feature verification