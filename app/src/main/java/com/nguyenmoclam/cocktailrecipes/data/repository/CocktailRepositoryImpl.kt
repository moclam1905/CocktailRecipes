package com.nguyenmoclam.cocktailrecipes.data.repository

import com.nguyenmoclam.cocktailrecipes.data.common.Resource
import com.nguyenmoclam.cocktailrecipes.domain.model.Cocktail
import com.nguyenmoclam.cocktailrecipes.domain.model.Ingredient
import com.nguyenmoclam.cocktailrecipes.domain.repository.CocktailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CocktailRepository
 * This is a temporary implementation that returns mock data
 */
@Singleton
class CocktailRepositoryImpl @Inject constructor() : CocktailRepository {

    override suspend fun getPopularCocktails(): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        // Mock data for demonstration
        val mockCocktails = createMockCocktails()
        emit(Resource.Success(mockCocktails))
    }

    override suspend fun searchCocktailsByName(query: String): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        val mockCocktails = createMockCocktails().filter { 
            it.name.contains(query, ignoreCase = true) 
        }
        emit(Resource.Success(mockCocktails))
    }

    override suspend fun searchCocktailsByIngredient(ingredient: String): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        val mockCocktails = createMockCocktails().filter {
            it.ingredients.any { ing -> ing.name.contains(ingredient, ignoreCase = true) }
        }
        emit(Resource.Success(mockCocktails))
    }

    override suspend fun getCocktailDetails(id: String): Flow<Resource<Cocktail>> = flow {
        emit(Resource.Loading)
        val cocktail = createMockCocktails().find { it.id == id }
        if (cocktail != null) {
            emit(Resource.Success(cocktail))
        } else {
            emit(Resource.Error("Cocktail not found"))
        }
    }

    override suspend fun saveFavorite(cocktail: Cocktail): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        // Mock saving to database
        emit(Resource.Success(true))
    }

    override suspend fun removeFavorite(id: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        // Mock removing from database
        emit(Resource.Success(true))
    }

    override suspend fun getFavorites(): Flow<Resource<List<Cocktail>>> = flow {
        emit(Resource.Loading)
        // Return some mock favorites
        val favorites = createMockCocktails().take(2).map { it.copy(isFavorite = true) }
        emit(Resource.Success(favorites))
    }

    // Helper method to create mock cocktails
    private fun createMockCocktails(): List<Cocktail> {
        return listOf(
            Cocktail(
                id = "1",
                name = "Mojito",
                imageUrl = "https://www.thecocktaildb.com/images/media/drink/metwgh1606770327.jpg",
                instructions = "Muddle mint leaves with sugar and lime juice. Add a splash of soda water and fill the glass with cracked ice. Pour the rum and top with soda water. Garnish with mint leaves and a lime wedge.",
                ingredients = listOf(
                    Ingredient("White rum", "2 oz"),
                    Ingredient("Lime juice", "1 oz"),
                    Ingredient("Sugar", "2 tsp"),
                    Ingredient("Mint", "6 leaves"),
                    Ingredient("Soda water", "Top")
                )
            ),
            Cocktail(
                id = "2",
                name = "Margarita",
                imageUrl = "https://www.thecocktaildb.com/images/media/drink/5noda61589575158.jpg",
                instructions = "Rub the rim of the glass with the lime slice to make the salt stick to it. Take care to moisten only the outer rim and sprinkle the salt on it. The salt should present to the lips of the imbiber and never mix into the cocktail. Shake the other ingredients with ice, then carefully pour into the glass.",
                ingredients = listOf(
                    Ingredient("Tequila", "2 oz"),
                    Ingredient("Triple sec", "1 oz"),
                    Ingredient("Lime juice", "1 oz"),
                    Ingredient("Salt", "Pinch")
                )
            ),
            Cocktail(
                id = "3",
                name = "Old Fashioned",
                imageUrl = "https://www.thecocktaildb.com/images/media/drink/vrwquq1478252802.jpg",
                instructions = "Place sugar cube in old fashioned glass and saturate with bitters, add a dash of plain water. Muddle until dissolved. Fill the glass with ice cubes and add whiskey. Garnish with orange slice and a cocktail cherry.",
                ingredients = listOf(
                    Ingredient("Bourbon", "2 oz"),
                    Ingredient("Angostura bitters", "2 dashes"),
                    Ingredient("Sugar cube", "1"),
                    Ingredient("Water", "Dash")
                )
            )
        )
    }
} 