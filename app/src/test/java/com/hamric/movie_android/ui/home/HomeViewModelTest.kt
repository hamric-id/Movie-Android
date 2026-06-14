package com.hamric.movie_android.ui.home


import com.hamric.movie_android.data.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.repository.FavoriteRepository
import io.mockk.coVerify
import java.time.LocalDate


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val mockMovieRepository: MovieRepository = mockk()
    private val mockFavoriteRepository: FavoriteRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createTestMovie(
        id: UInt,
        title: String,
        posterPath: String? = "/poster${id}.jpg",
        backdropPath: String? = "/backdrop${id}.jpg"
    ): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Test overview for movie $id",
            posterPath = posterPath ?: "",
            backdropPath = backdropPath ?: "",
            releaseDate = LocalDate.of(2019, 5, 5)
        )
    }

    @Test
    fun testLoadMoviesSuccess() = runTest {
        val popularMovies = listOf(
            createTestMovie(1u, "Popular Movie 1"),
            createTestMovie(2u, "Popular Movie 2")
        )
        val topRatedMovies = listOf(
            createTestMovie(3u, "Top Rated Movie 1"),
            createTestMovie(4u, "Top Rated Movie 2")
        )
        val nowPlayingMovies = listOf(
            createTestMovie(5u, "Now Playing Movie 1"),
            createTestMovie(6u, "Now Playing Movie 2")
        )

        coEvery { mockMovieRepository.getPopularMovies() } returns popularMovies
        coEvery { mockMovieRepository.getTopRatedMovies() } returns topRatedMovies
        coEvery { mockMovieRepository.getNowPlayingMovies() } returns nowPlayingMovies
        coEvery { mockFavoriteRepository.getAllFavorites() } returns emptyList()

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.popularMovies).hasSize(2)
        assertThat(state.popularMovies[0].title).isEqualTo("Popular Movie 1")
        assertThat(state.topRatedMovies).hasSize(2)
        assertThat(state.nowPlayingMovies).hasSize(2)
        assertThat(state.error).isNull()
    }

    @Test
    fun testLoadMoviesError() = runTest {
        val errorMessage = "Network error - API failed"
        coEvery { mockMovieRepository.getPopularMovies() } throws Exception(errorMessage)
        coEvery { mockMovieRepository.getTopRatedMovies() } throws Exception(errorMessage)
        coEvery { mockMovieRepository.getNowPlayingMovies() } throws Exception(errorMessage)
        coEvery { mockFavoriteRepository.getAllFavorites() } returns emptyList()

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isEqualTo(errorMessage)
        assertThat(state.popularMovies).isEmpty()
        assertThat(state.topRatedMovies).isEmpty()
        assertThat(state.nowPlayingMovies).isEmpty()
    }

    @Test
    fun testRetryAfterError() = runTest {
        coEvery { mockMovieRepository.getPopularMovies() } throws Exception("Network Error")
        coEvery { mockMovieRepository.getTopRatedMovies() } throws Exception("Network Error")
        coEvery { mockMovieRepository.getNowPlayingMovies() } throws Exception("Network Error")
        coEvery { mockFavoriteRepository.getAllFavorites() } returns emptyList()

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.error).isEqualTo("Network Error")
        assertThat(viewModel.uiState.value.popularMovies).isEmpty()

        val successMovies = listOf(createTestMovie(1u, "Retry Success Movie"))
        coEvery { mockMovieRepository.getPopularMovies() } returns successMovies
        coEvery { mockMovieRepository.getTopRatedMovies() } returns successMovies
        coEvery { mockMovieRepository.getNowPlayingMovies() } returns successMovies

        viewModel.loadAllMovies()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.error).isNull()
        assertThat(state.popularMovies).hasSize(1)
        assertThat(state.popularMovies[0].title).isEqualTo("Retry Success Movie")
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun testEmptyMovieList() = runTest {
        coEvery { mockMovieRepository.getPopularMovies() } returns emptyList()
        coEvery { mockMovieRepository.getTopRatedMovies() } returns emptyList()
        coEvery { mockMovieRepository.getNowPlayingMovies() } returns emptyList()
        coEvery { mockFavoriteRepository.getAllFavorites() } returns emptyList()

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.popularMovies).isEmpty()
        assertThat(state.topRatedMovies).isEmpty()
        assertThat(state.nowPlayingMovies).isEmpty()
        assertThat(state.error).isNull()
    }

    @Test
    fun testAllThreeApisAreCalled() = runTest {
        var popularCalled = false
        var topRatedCalled = false
        var nowPlayingCalled = false

        coEvery { mockMovieRepository.getPopularMovies() } answers {
            popularCalled = true
            emptyList()
        }
        coEvery { mockMovieRepository.getTopRatedMovies() } answers {
            topRatedCalled = true
            emptyList()
        }
        coEvery { mockMovieRepository.getNowPlayingMovies() } answers {
            nowPlayingCalled = true
            emptyList()
        }
        coEvery { mockFavoriteRepository.getAllFavorites() } returns emptyList()

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        assertThat(popularCalled).isTrue()
        assertThat(topRatedCalled).isTrue()
        assertThat(nowPlayingCalled).isTrue()
    }

    @Test
    fun testLoadFavoritesOnInit() = runTest {
        val favoriteIds = listOf(1u, 3u, 5u)

        coEvery { mockMovieRepository.getPopularMovies() } returns emptyList()
        coEvery { mockMovieRepository.getTopRatedMovies() } returns emptyList()
        coEvery { mockMovieRepository.getNowPlayingMovies() } returns emptyList()
        coEvery { mockFavoriteRepository.getAllFavorites() } returns favoriteIds

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        assertThat(viewModel.favoriteMovies.value).containsExactly(1u, 3u, 5u)
    }

    @Test
    fun testToggleFavoriteAdd() = runTest {
        val movieId = 100u

        coEvery { mockMovieRepository.getPopularMovies() } returns emptyList()
        coEvery { mockMovieRepository.getTopRatedMovies() } returns emptyList()
        coEvery { mockMovieRepository.getNowPlayingMovies() } returns emptyList()
        coEvery { mockFavoriteRepository.getAllFavorites() } returns emptyList()
        coEvery { mockFavoriteRepository.addFavorite(movieId) } returns Unit

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        assertThat(viewModel.isFavorite(movieId)).isFalse()

        viewModel.toggleFavorite(movieId)
        advanceUntilIdle()

        coVerify { mockFavoriteRepository.addFavorite(movieId) }
        assertThat(viewModel.isFavorite(movieId)).isTrue()
        assertThat(viewModel.favoriteMovies.value).contains(movieId)
    }

    @Test
    fun testToggleFavoriteRemove() = runTest {
        val movieId = 100u
        val existingFavorites = listOf(movieId)

        coEvery { mockMovieRepository.getPopularMovies() } returns emptyList()
        coEvery { mockMovieRepository.getTopRatedMovies() } returns emptyList()
        coEvery { mockMovieRepository.getNowPlayingMovies() } returns emptyList()
        coEvery { mockFavoriteRepository.getAllFavorites() } returns existingFavorites
        coEvery { mockFavoriteRepository.removeFavorite(movieId) } returns Unit

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        assertThat(viewModel.isFavorite(movieId)).isTrue()

        viewModel.toggleFavorite(movieId)
        advanceUntilIdle()

        coVerify { mockFavoriteRepository.removeFavorite(movieId) }
        assertThat(viewModel.isFavorite(movieId)).isFalse()
        assertThat(viewModel.favoriteMovies.value).doesNotContain(movieId)
    }

    @Test
    fun testIsFavoriteReturnsCorrectStatus() = runTest {
        val favoriteIds = listOf(1u, 2u, 3u)

        coEvery { mockMovieRepository.getPopularMovies() } returns emptyList()
        coEvery { mockMovieRepository.getTopRatedMovies() } returns emptyList()
        coEvery { mockMovieRepository.getNowPlayingMovies() } returns emptyList()
        coEvery { mockFavoriteRepository.getAllFavorites() } returns favoriteIds

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        assertThat(viewModel.isFavorite(1u)).isTrue()
        assertThat(viewModel.isFavorite(2u)).isTrue()
        assertThat(viewModel.isFavorite(3u)).isTrue()
        assertThat(viewModel.isFavorite(4u)).isFalse()
        assertThat(viewModel.isFavorite(5u)).isFalse()
    }

    @Test
    fun testMultipleToggleFavorites() = runTest {
        coEvery { mockMovieRepository.getPopularMovies() } returns emptyList()
        coEvery { mockMovieRepository.getTopRatedMovies() } returns emptyList()
        coEvery { mockMovieRepository.getNowPlayingMovies() } returns emptyList()
        coEvery { mockFavoriteRepository.getAllFavorites() } returns emptyList()
        coEvery { mockFavoriteRepository.addFavorite(any()) } returns Unit
        coEvery { mockFavoriteRepository.removeFavorite(any()) } returns Unit

        viewModel = HomeViewModel(mockMovieRepository, mockFavoriteRepository)
        advanceUntilIdle()

        viewModel.toggleFavorite(1u)
        viewModel.toggleFavorite(2u)
        viewModel.toggleFavorite(3u)
        advanceUntilIdle()

        assertThat(viewModel.favoriteMovies.value).hasSize(3)
        assertThat(viewModel.favoriteMovies.value).containsExactly(1u, 2u, 3u)

        viewModel.toggleFavorite(2u)
        advanceUntilIdle()

        assertThat(viewModel.favoriteMovies.value).hasSize(2)
        assertThat(viewModel.favoriteMovies.value).containsExactly(1u, 3u)
    }
}