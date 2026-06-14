package com.hamric.movie_android.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.ui.components.MovieSection
import com.hamric.movie_android.ui.theme.Purple500
import com.hamric.movie_android.ui.types.CardStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Movie) -> Unit,
    onFavoriteClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MOVIES",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                },
                actions = {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple500,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadAllMovies() }) {
                            Text("Retry")
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 16.dp, bottom = 0.dp, start = 16.dp, end = 0.dp),
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        MovieSection(
                            title = "Popular Movie",
                            movies = uiState.popularMovies,
                            cardStyle = CardStyle.BIG_POSTER,
                            onMovieClick = onMovieClick
                        )

                        MovieSection(
                            title = "Top Rated",
                            movies = uiState.topRatedMovies,
                            cardStyle = CardStyle.WITH_DETAILS,
                            onMovieClick = onMovieClick
                        )

                        MovieSection(
                            title = "Now Playing",
                            movies = uiState.nowPlayingMovies,
                            cardStyle = CardStyle.WITH_DETAILS,
                            onMovieClick = onMovieClick
                        )
                    }
                }
            }
        }
    }
}





