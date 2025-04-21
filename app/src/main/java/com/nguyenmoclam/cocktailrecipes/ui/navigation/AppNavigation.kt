package com.nguyenmoclam.cocktailrecipes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nguyenmoclam.cocktailrecipes.ui.detail.CocktailDetailScreen
import com.nguyenmoclam.cocktailrecipes.ui.home.HomeScreen

object NavDestinations {
    const val HOME = "home"
    const val COCKTAIL_DETAIL = "cocktail_detail"
    const val COCKTAIL_ID_ARG = "cocktailId"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavDestinations.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavDestinations.HOME) {
            HomeScreen(
                onCocktailClick = { cocktailId ->
                    navController.navigate("${NavDestinations.COCKTAIL_DETAIL}/$cocktailId")
                }
            )
        }
        
        composable(
            route = "${NavDestinations.COCKTAIL_DETAIL}/{${NavDestinations.COCKTAIL_ID_ARG}}",
            arguments = listOf(
                navArgument(NavDestinations.COCKTAIL_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val cocktailId = backStackEntry.arguments?.getString(NavDestinations.COCKTAIL_ID_ARG) ?: ""
            CocktailDetailScreen(
                cocktailId = cocktailId,
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
} 