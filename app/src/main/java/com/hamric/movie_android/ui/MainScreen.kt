package com.hamric.movie_android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import androidx.navigation.navigation
import com.hamric.movie_android.ui.detail.DetailScreen
import com.hamric.movie_android.ui.favorites.FavoritesScreen
import com.hamric.movie_android.ui.home.HomeScreen


@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val currentRoute = currentDestination?.route
    val isDetailScreen = currentRoute?.startsWith("detail") == true

    fun navigateBottomBar(navItem: String){
        navController.navigate(navItem) {
            popUpTo(navItem) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            NavHost(
                navController = navController,
                startDestination = BottomNavItem.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        onMovieClick = { movie ->
                            navController.navigate("detail/${movie.id}")
                        },
                        onFavoriteClick = {
                            navigateBottomBar(BottomNavItem.Favorites.route)
                        }
                    )
                }

                composable(BottomNavItem.Favorites.route) {
                    FavoritesScreen(
                        onBackPressed = { navigateBottomBar(BottomNavItem.Home.route) },
                        onMovieClick = { movie ->
                            navController.navigate("detail/${movie.id}")
                        }
                    )
                }

                composable(
                    route = "detail/{movieId}",
                    arguments = listOf(
                        navArgument("movieId") { type = NavType.IntType }
                    )
                ) {
                    DetailScreen(
                        onBackPressed = { navController.popBackStack() }
                    )
                }
            }
        }

        if (!isDetailScreen) {
            BottomNavigationBar(navController = navController)
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Favorites
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                IconButton(
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Color.Black else Color.LightGray,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String
) {
    data object Home : BottomNavItem(
        route = "home",
        icon = Icons.Default.Home,
        title = "Home"
    )

    data object Favorites : BottomNavItem(
        route = "favorites",
        icon = Icons.Default.Favorite,
        title = "Favorites"
    )
}