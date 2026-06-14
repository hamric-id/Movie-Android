package com.hamric.movie_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hamric.movie_android.ui.detail.DetailScreen
import com.hamric.movie_android.ui.home.HomeScreen
import com.hamric.movie_android.ui.theme.MovieAndroidTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavType
import androidx.navigation.navArgument

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MovieAndroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(
                                onMovieClick = { movie ->
                                    navController.navigate("detail/${movie.id}")
                                },
                                onFavoriteClick = {
                                    navController.navigate("favorites")
                                }
                            )
                        }

                        composable(
                            route = "detail/{movieId}",
                            arguments = listOf(
                                navArgument("movieId"){ type = NavType.IntType }
                            )
                        ) {
                            DetailScreen(
                                onBackPressed = { navController.popBackStack() }
                            )
                        }
                        composable("favorites") {

                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MovieAndroidTheme {}
}