# Technical Context

## Architecture
Following MVVM with Clean Architecture pattern:
- **Data layer**: Remote and local data sources, repository implementations
- **Domain layer**: Models, repository interfaces, use cases
- **Presentation layer**: UI components, ViewModels, state management

## Key Technologies
- **UI**: Jetpack Compose for modern declarative UI
- **Navigation**: Compose Navigation for single-activity architecture
- **DI**: Hilt for dependency injection
- **Networking**: Retrofit with OkHttp for API calls
- **Local Storage**: Room Database for caching and favorites
- **Concurrency**: Kotlin Coroutines & Flow for async operations
- **Image Loading**: Coil for efficient image loading and caching

## API
Using TheCocktailDB API for cocktail data.

## Testing Architecture
- **Test Doubles**: Fake implementations of repositories and data sources
- **Test Data Factory**: Central utility for consistent test data generation
- **Repository Testing**: Tests using fake data sources for controlled testing
- **JUnit & Mockito**: For unit testing business logic

## Module Structure
- **data**: API interfaces, repositories, and data sources
- **domain**: Use cases and business logic
- **ui**: Compose UI components and ViewModels
- **di**: Dependency injection modules

## Testing Strategy
- Unit tests for ViewModels, repositories, and use cases
- UI tests for critical user flows