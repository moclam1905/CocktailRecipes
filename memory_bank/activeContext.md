# Active Context

## Current Focus
API integration with TheCocktailDB

## Active Tasks
- Create mapping functions for domain models (1h)
- Set up Retrofit client with interceptors (2h)
- Implement API response handling (1h)
- Add API service interfaces (1h)

## Dependencies Added
- Hilt for dependency injection ✅
- ViewModel & StateFlow for UI state ✅
- Coroutines for asynchronous operations ✅
- Retrofit for API calls ✅
- Room for local database ✅
- Coil for image loading ✅

## Implementation Notes
- The TheCocktailDB API endpoints:
  - https://www.thecocktaildb.com/api/json/v1/1/search.php?s={query} - Search by name
  - https://www.thecocktaildb.com/api/json/v1/1/lookup.php?i={id} - Get by ID
  - https://www.thecocktaildb.com/api/json/v1/1/filter.php?i={ingredient} - Filter by ingredient
  - https://www.thecocktaildb.com/api/json/v1/1/popular.php - Get popular
- We need to handle the API's specific response format which nests cocktails in a 'drinks' array
- Implement error handling for network failures and empty responses
- Create mappers between API response models and domain models