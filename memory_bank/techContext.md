# Technical Context

## Architecture
- **Pattern**: MVVM with Clean Architecture
- **UI**: Jetpack Compose
- **State Management**: ViewModel with StateFlow

## Key Technologies
- **Network**: Retrofit with Coroutines
- **Local Storage**: Room Database
- **Dependency Injection**: Hilt
- **Image Loading**: Coil
- **Async**: Kotlin Coroutines & Flow

## Module Structure
- **data**: API interfaces, repositories, and data sources
- **domain**: Use cases and business logic
- **ui**: Compose UI components and ViewModels
- **di**: Dependency injection modules

## Testing Strategy
- Unit tests for ViewModels, repositories, and use cases
- UI tests for critical user flows