package com.nguyenmoclam.cocktailrecipes.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.nguyenmoclam.cocktailrecipes.ui.analytics.ApiPerformanceDashboard
import com.nguyenmoclam.cocktailrecipes.ui.detail.CocktailDetailScreen
import com.nguyenmoclam.cocktailrecipes.ui.favorites.FavoritesScreen
import com.nguyenmoclam.cocktailrecipes.ui.home.HomeScreen
import com.nguyenmoclam.cocktailrecipes.ui.ingredients.IngredientExplorerScreen
import com.nguyenmoclam.cocktailrecipes.ui.search.SearchScreen
import com.nguyenmoclam.cocktailrecipes.ui.settings.SettingsScreen

object NavDestinations {
    const val HOME = "home"
    const val COCKTAIL_DETAIL = "cocktail_detail"
    const val SEARCH = "search"
    const val FAVORITES = "favorites"
    const val SETTINGS = "settings"
    const val API_DASHBOARD = "api_dashboard"
    const val INGREDIENT_EXPLORER = "ingredient_explorer"
    const val COCKTAIL_ID_ARG = "cocktailId"
}

// Transition duration for animations
private const val TRANSITION_DURATION = 500

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation(
    startDestination: String = NavDestinations.HOME
) {
    val navController = rememberAnimatedNavController()
    
    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { 
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeIn(animationSpec = tween(TRANSITION_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            ) + fadeOut(animationSpec = tween(TRANSITION_DURATION))
        }
    ) {
        composable(
            route = NavDestinations.HOME
        ) {
            HomeScreen(
                onCocktailClick = { cocktailId ->
                    navController.navigate("${NavDestinations.COCKTAIL_DETAIL}/$cocktailId")
                },
                onSearchClick = {
                    navController.navigate(NavDestinations.SEARCH)
                },
                onFavoritesClick = {
                    navController.navigate(NavDestinations.FAVORITES)
                },
                onSettingsClick = {
                    navController.navigate(NavDestinations.SETTINGS)
                },
                onIngredientExplorerClick = {
                    navController.navigate(NavDestinations.INGREDIENT_EXPLORER)
                }
            )
        }
        
        composable(
            route = NavDestinations.SEARCH
        ) {
            SearchScreen(
                onCocktailClick = { cocktailId ->
                    navController.navigate("${NavDestinations.COCKTAIL_DETAIL}/$cocktailId")
                }
            )
        }
        
        composable(
            route = NavDestinations.FAVORITES
        ) {
            FavoritesScreen(
                onCocktailClick = { cocktailId ->
                    navController.navigate("${NavDestinations.COCKTAIL_DETAIL}/$cocktailId")
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = NavDestinations.SETTINGS
        ) {
            SettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onOpenApiDashboard = {
                    navController.navigate(NavDestinations.API_DASHBOARD)
                }
            )
        }
        
        composable(
            route = NavDestinations.API_DASHBOARD
        ) {
            ApiPerformanceDashboard(
                onGenerateReport = { /* Handle report generation */ },
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = NavDestinations.INGREDIENT_EXPLORER
        ) {
            IngredientExplorerScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
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