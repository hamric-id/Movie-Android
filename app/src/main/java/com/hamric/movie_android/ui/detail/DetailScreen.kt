package com.hamric.movie_android.ui.detail

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.hamric.movie_android.data.api.MovieApiService
import com.hamric.movie_android.data.local.FavoriteDao
import com.hamric.movie_android.data.local.MovieEntity
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.model.MovieResponse
import com.hamric.movie_android.data.model.MovieReview
import com.hamric.movie_android.data.model.MoviesResponse
import com.hamric.movie_android.data.model.ReviewsResponse
import com.hamric.movie_android.data.repository.FavoriteRepository
import com.hamric.movie_android.data.repository.MovieRepository
import com.hamric.movie_android.ui.components.MovieReviewCard
import com.hamric.movie_android.utils.DateUtils.toString
import java.time.LocalDate
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBackPressed: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    fun shareMovie() {
        val shareText = "Detailed information about the movie ${uiState.movie?.title?.run{"'$this'"}?:""} is here ${uiState.movie?.officialUrl?:"https://themoviedb.org"}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {
            uiState.movie?.backdropPath?.let { path ->
                item {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w300${path}",
                        contentDescription = uiState.movie?.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp).windowInsetsPadding(WindowInsets.statusBars),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(start = 16.dp, top = 22.dp, end= 16.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Text(
                            text = uiState.movie?.title ?: "",
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = uiState.movie?.releaseDate?.run{
                                "Release: "+this.toString(pattern = "MMM d yyyy",locale = Locale.US)
                            } ?: "",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "Description",
                            fontSize = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )

                        Text(
                            text = uiState.movie?.overview ?: "",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

            if (uiState.movieReviews.isNotEmpty()) {
                item {
                    Text(
                        text = "Review",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 3.dp),
                        textAlign = TextAlign.Start
                    )
                }

                items(uiState.movieReviews) { movieReview ->
                    MovieReviewCard(movieReview = movieReview, modifier = Modifier.padding(start = 16.dp, end = 16.dp))
                }


            } else if (!uiState.isLoading && uiState.movieReviews.isEmpty()) {
                item {
                    Text(
                        text = "No Review Available",
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            item{
                Spacer(modifier = Modifier.padding(bottom = 50.dp))
            }
        }

        TopAppBar(
            title = {
                Text(
                    text = "Detail",
                    color = Color.White,
                    textAlign = TextAlign.Start
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(-8.dp)
        ) {
            IconButton(
                onClick = { shareMovie() }
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
            IconButton(
                onClick = { viewModel.toggleFavorite() }
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = if (uiState.isFavorite) Color.Red else Color.DarkGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}



@Preview(name = "DetailScreen - Direct Preview")
@Preview(name = "DetailScreen - Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewDetailScreen() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            DetailScreen(
                onBackPressed = {},
                viewModel = PreviewDetailViewModel()
            )
        }
    }
}

class PreviewDetailViewModel : DetailViewModel(
    movieRepository = MovieRepository( apiService = MockMovieApiService()),
    favoriteRepository = MockFavoriteRepository(),
    savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))
) {
    init {
        setUiStateForPreview(
            DetailUiState(
                isLoading = false,
                movie = Movie(
                    id = 2,
                    title = "The Dark Knight",
                    overview = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, " +
                            "Batman must accept one of the greatest psychological and physical tests of his ability " +
                            "to fight injustice.",
                    posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                    backdropPath = "/zfbjgQE1uSd9wiPTX4VzsLi0rGG.jpg",
                    releaseDate = LocalDate.of(2008, 7, 18)
                ),
                movieReviews = listOf(
                    MovieReview(
                        id = "1",
                        authorName = "John Doe",
                        avatarAuthorUrl = "https://secure.gravatar.com/avatar/f248ec34f953bc62cafcbdd81fddd6b6.jpg",
                        content = "Absolutely amazing! Heath Ledger's performance as the Joker is iconic. " +
                                "This is hands down the best superhero movie ever made.",
                        updatedAt = LocalDate.of(2024, 1, 15)
                    ),
                    MovieReview(
                        id = "2",
                        authorName = "Jane Smith",
                        avatarAuthorUrl = "https://secure.gravatar.com/avatar/f248ec34f953bc62cafcbdd81fddd6b6.jpg",
                        content = "A masterpiece of modern cinema. Christopher Nolan at his best.",
                        updatedAt = LocalDate.of(2024, 2, 20)
                    ),
                    MovieReview(
                        id = "3",
                        authorName = "Toyol",
                        avatarAuthorUrl = "https://secure.gravatar.com/avatar/f248ec34f953bc62cafcbdd81fddd6b6.jpg",
                        content = "A masterpiece of modern cinema. Christopher Nolan at his best.",
                        updatedAt = LocalDate.of(2024, 2, 20)
                    )
                ),
                isFavorite = true,
                error = null
            )
        )
    }
}

class MockMovieApiService : MovieApiService {

    override suspend fun getMovieDetail(movieId: UInt, language: String): MovieResponse {
        return MovieResponse(
            id = movieId.toInt(),
            title = "The Dark Knight",
            overview = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, " +
                    "Batman must accept one of the greatest psychological and physical tests of his ability " +
                    "to fight injustice.",
            posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            backdropPath = "/zfbjgQE1uSd9wiPTX4VzsLi0rGG.jpg",
            releaseDate = "2008-07-18",
            officialUrl = "http://www.starwars.com/films/star-wars-episode-iv-a-new-hope"
        )
    }
    override suspend fun getPopularMovies(
        language: String,
        region: String,
        page: UInt
    ): MoviesResponse {
        return MoviesResponse(
            page = 1,
            results = listOf(),
            totalPages = 1,
            totalResults = 0
        )
    }

    override suspend fun getTopRatedMovies(
        language: String,
        region: String,
        page: UInt
    ): MoviesResponse {
        return MoviesResponse(
            page = 1,
            results = listOf(),
            totalPages = 1,
            totalResults = 0
        )
    }

    override suspend fun getNowPlayingMovies(
        language: String,
        region: String,
        page: UInt
    ): MoviesResponse {
        return MoviesResponse(
            page = 1,
            results = listOf(),
            totalPages = 1,
            totalResults = 0
        )
    }



    override suspend fun getMovieReviews(movieId: UInt, page: UInt): ReviewsResponse {
        return ReviewsResponse(
            id = 1,
            page = 1,
            results = emptyList(),
            totalPages = 1,
            totalResults = 2
        )
    }
}

class MockFavoriteRepository : FavoriteRepository(
    favoriteDao = MockFavoriteDao()
)

class MockFavoriteDao : FavoriteDao {
    private val favorites = mutableSetOf<Int>()

    override suspend fun addToFavorites(movieId: Int) {
        favorites.add(movieId)
    }

    override suspend fun removeFromFavorites(movieId: Int) {
        favorites.remove(movieId)
    }

    override suspend fun getAllFavorites(): List<MovieEntity> {
        return favorites.map { MovieEntity(id = it) }
    }

    override suspend fun isFavorite(movieId: Int): Boolean {
        return favorites.contains(movieId)
    }
}



