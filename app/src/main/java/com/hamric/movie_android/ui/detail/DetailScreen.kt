package com.hamric.movie_android.ui.detail



import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
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
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
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
import androidx.compose.material3.ModalBottomSheet
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.text.font.FontWeight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    onBackPressed: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val movie by viewModel.movieState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val reviewsPagingItems: LazyPagingItems<MovieReview> =
        viewModel.reviewsPagingFlow.collectAsLazyPagingItems()

    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }


    var showShareSheet by remember { mutableStateOf(false) }

    fun openShareSheet() {
        showShareSheet = true
    }

    fun copyLink() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Movie Link", movie?.officialUrl ?: "https://themoviedb.org/movie/${movie?.id}")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
        showShareSheet = false
    }

    fun shareWithSystem() {
        val shareText = "Check out ${movie?.title ?: "this movie"}! ${movie?.officialUrl ?: "https://themoviedb.org"}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
        showShareSheet = false
    }


    suspend fun refreshData() {
        isRefreshing = true
        viewModel.retryLoading()
        reviewsPagingItems.refresh()
        kotlinx.coroutines.delay(500)
        isRefreshing = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                viewModel.scope.launch {
                    refreshData()
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                movie?.backdropPath?.let { path ->
                    item {
                        Box(
                            modifier = Modifier
                                .background(Color.Transparent)
                                .fillMaxWidth()
                                .statusBarsPadding()
                        ) {
                            AsyncImage(
                                model = "https://image.tmdb.org/t/p/w780${path}",
                                contentDescription = movie?.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(top = 22.dp, bottom = 0.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Text(
                                text = movie?.title ?: "",
                                fontSize = 20.sp,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )

                            Text(
                                text = movie?.releaseDate?.run {
                                    "Release: " + this.toString(pattern = "MMM d yyyy", locale = Locale.US)
                                }?:"",
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = "Description",
                                fontSize = 20.sp,
                                color = Color.Black,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )

                            Text(
                                text = movie?.overview ?: "",
                                fontSize = 14.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }

                if (reviewsPagingItems.itemCount > 0) {
                    item {
                        Text(
                            text = "Review",
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(top = 22.dp, bottom = 5.dp, start = 16.dp, end = 16.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    items(reviewsPagingItems.itemCount) { index ->
                        val review = reviewsPagingItems[index]
                        review?.let {
                            MovieReviewCard(
                                movieReview = it,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
                            )
                        }
                    }

                    if (reviewsPagingItems.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    if (reviewsPagingItems.loadState.append is LoadState.Error) {
                        item {
                            Text(
                                text = "Failed to load more reviews. Swipe down to refresh.",
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else if (reviewsPagingItems.loadState.refresh is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (reviewsPagingItems.loadState.refresh is LoadState.NotLoading &&
                    reviewsPagingItems.itemCount == 0
                ) {
                    item {
                        Text(
                            text = "No reviews available for this movie",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.padding(bottom = 80.dp))
                }
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                onClick = { openShareSheet() }
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
            IconButton(
                onClick = { viewModel.toggleFavorite() }
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.DarkGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (isLoading && !isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        error?.let { errorMessage ->
            if (errorMessage.isNotBlank() && !isLoading) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.retryLoading()
                                reviewsPagingItems.refresh()
                            }
                        ) {
                            Text("Retry")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        if (showShareSheet) {
            ModalBottomSheet(
                onDismissRequest = { showShareSheet = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Share ${movie?.title ?: "Movie"}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { copyLink() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📋", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Copy link")
                    }

                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { shareWithSystem() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("↗️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("More options...")
                    }
                }
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
    movieRepository = MovieRepository(apiService = MockMovieApiService()),
    favoriteRepository = MockFavoriteRepository(),
    savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))
) {
    init {
        setPreviewData(
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
            favorite = true
        )
    }
}

class MockMovieApiService : MovieApiService {
    override suspend fun getMovieDetail(movieId: UInt, language: String): MovieResponse {
        return MovieResponse(
            id = movieId.toInt(),
            title = "The Dark Knight",
            overview = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham...",
            posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            backdropPath = "/zfbjgQE1uSd9wiPTX4VzsLi0rGG.jpg",
            releaseDate = "2008-07-18",
            officialUrl = "https://www.themoviedb.org/movie/155"
        )
    }

    override suspend fun getPopularMovies(language: String, region: String, page: UInt): MoviesResponse {
        return MoviesResponse(page = 1, results = emptyList(), totalPages = 1, totalResults = 0)
    }

    override suspend fun getTopRatedMovies(language: String, region: String, page: UInt): MoviesResponse {
        return MoviesResponse(page = 1, results = emptyList(), totalPages = 1, totalResults = 0)
    }

    override suspend fun getNowPlayingMovies(language: String, region: String, page: UInt): MoviesResponse {
        return MoviesResponse(page = 1, results = emptyList(), totalPages = 1, totalResults = 0)
    }

    override suspend fun getMovieReviews(movieId: UInt, page: UInt): ReviewsResponse {
        return ReviewsResponse(
            id = 1,
            page = page.toInt(),
            results = emptyList(),
            totalPages = 3,
            totalResults = 45
        )
    }
}

class MockFavoriteRepository : FavoriteRepository(favoriteDao = MockFavoriteDao())

class MockFavoriteDao : FavoriteDao {
    private val favorites = mutableSetOf<Int>()

    override suspend fun addToFavorites(movieId: Int) { favorites.add(movieId) }
    override suspend fun removeFromFavorites(movieId: Int) { favorites.remove(movieId) }
    override suspend fun getAllFavorites(): List<MovieEntity> = favorites.map { MovieEntity(id = it) }
    override suspend fun isFavorite(movieId: Int): Boolean = favorites.contains(movieId)
}