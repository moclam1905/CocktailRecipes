# CocktailRecipes - Android MVP

<p align="center">
  <img src="https://img.icons8.com/color/96/000000/cocktail.png" alt="Cocktail Icon"/>
</p>

## Overview

CocktailRecipes is a simple MVP (Minimum Viable Product) Android application that demonstrates the use of Cursor rules with two powerful development tools:

- [BMad's Cursor Custom Agents Rules Generator](https://github.com/bmadcode/cursor-custom-agents-rules-generator) - For automatic rule and custom agent generation
- [Cursor Memory Bank](https://github.com/vanzan01/cursor-memory-bank) - For enhanced development workflow and context tracking

The application is built with modern Android development practices and serves as a practical example of implementing Cursor-based workflows in a real-world project.

## Core Features

- Browse a curated list of popular cocktails
- Search for cocktails by name or ingredient
- Save favorite recipes for offline viewing
- View detailed cocktail recipes with ingredients and instructions

## Data Source

The application uses [TheCocktailDB API](https://www.thecocktaildb.com/api.php) as its data source, providing comprehensive information about cocktails, including ingredients, instructions, and images.

## Target Audience

- Cocktail enthusiasts
- Home bartenders
- Users looking for drink recipes

## Project Architecture

The application follows the MVVM (Model-View-ViewModel) architecture pattern with Clean Architecture principles:

- **Data Layer**: API integration and local database
- **Domain Layer**: Business logic and use cases
- **UI Layer**: Jetpack Compose UI components and ViewModels

## Current Progress

### Completed
- ✅ Project Setup & Architecture
  - Created project structure with MVVM architecture
  - Set up dependency injection with Hilt
  - Configured UI theme and styling with dark mode support

- ✅ API Integration
  - Created data models for API responses
  - Implemented network layer with Retrofit
  - Set up repository pattern with caching strategy
  - Added error handling for API calls

- ✅ Testing Infrastructure
  - Created test utilities and fixtures
  - Implemented fake data sources for testing

### In Progress
- 🚧 Main Features
  - Home screen with popular cocktails list
  - Cocktail detail screen
  - Search functionality
  - Favorites feature with Room integration

- 🚧 Polishing & Testing
  - UI loading states and error handling
  - Offline capability
  - Unit and UI testing

## Tech Stack

- **UI**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Networking**: Retrofit + OkHttp
- **Database**: Room
- **Dependency Injection**: Hilt
- **Image Loading**: Coil
- **Concurrency**: Kotlin Coroutines + Flow
- **Testing**: JUnit, Mockito, Fake implementations

## Development Approach

This project utilizes Cursor's advanced AI capabilities with custom rules to streamline development. The combination of BMad's rules generator and Memory Bank tools enables:

- Consistent code style and architecture
- Automated task breakdown
- Enhanced context awareness during development
- Efficient workflow with memory-driven task tracking

## License

This project is available under the MIT License. 