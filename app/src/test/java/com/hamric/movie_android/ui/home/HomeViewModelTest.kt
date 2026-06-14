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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val mockRepository: MovieRepository = mockk()
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
        id: Int,
        title: String,
        posterPath: String? = "/poster${id}.jpg",
        backdropPath: String? = "/backdrop${id}.jpg"
    ): Movie {
        return Movie(
            id = id,
            title = title,
            overview = "Test overview for movie $id",
            posterPath = posterPath?:"",
            backdropPath = backdropPath?:"",
            releaseDate = LocalDate.of(2019, 5,5)
        )
    }

    @Test
    fun testLoadMoviesSuccess() = runTest {
        val popularMovies = listOf(
            createTestMovie(1, "Popular Movie 1"),
            createTestMovie(2, "Popular Movie 2")
        )
        val topRatedMovies = listOf(
            createTestMovie(3, "Top Rated Movie 1"),
            createTestMovie(4, "Top Rated Movie 2")
        )
        val nowPlayingMovies = listOf(
            createTestMovie(5, "Now Playing Movie 1"),
            createTestMovie(6, "Now Playing Movie 2")
        )

        coEvery { mockRepository.getPopularMovies() } returns popularMovies
        coEvery { mockRepository.getTopRatedMovies() } returns topRatedMovies
        coEvery { mockRepository.getNowPlayingMovies() } returns nowPlayingMovies

        viewModel = HomeViewModel(mockRepository)
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
        coEvery { mockRepository.getPopularMovies() } throws Exception(errorMessage)
        coEvery { mockRepository.getTopRatedMovies() } throws Exception(errorMessage)
        coEvery { mockRepository.getNowPlayingMovies() } throws Exception(errorMessage)

        viewModel = HomeViewModel(mockRepository)
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
        coEvery { mockRepository.getPopularMovies() } throws Exception("Network Error")
        coEvery { mockRepository.getTopRatedMovies() } throws Exception("Network Error")
        coEvery { mockRepository.getNowPlayingMovies() } throws Exception("Network Error")

        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.error).isEqualTo("Network Error")
        assertThat(viewModel.uiState.value.popularMovies).isEmpty()

        val successMovies = listOf(createTestMovie(1, "Retry Success Movie"))
        coEvery { mockRepository.getPopularMovies() } returns successMovies
        coEvery { mockRepository.getTopRatedMovies() } returns successMovies
        coEvery { mockRepository.getNowPlayingMovies() } returns successMovies

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
        coEvery { mockRepository.getPopularMovies() } returns emptyList()
        coEvery { mockRepository.getTopRatedMovies() } returns emptyList()
        coEvery { mockRepository.getNowPlayingMovies() } returns emptyList()

        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.popularMovies).isEmpty()
        assertThat(state.topRatedMovies).isEmpty()
        assertThat(state.nowPlayingMovies).isEmpty()
        assertThat(state.error).isNull()
    }

    @Test
    fun testPosterUrlFormat() = runTest {
        val movie = Movie(
            id = 100,
            title = "Test Movie",
            overview = "Test Overview",
            posterPath = "/abc123.jpg",
            backdropPath = "/def456.jpg",
            releaseDate = LocalDate.of(2019, 5,5)
        )

        assertThat(movie.posterPath).isEqualTo("/abc123.jpg")
        assertThat(movie.backdropPath).isEqualTo("/def456.jpg")
    }

    @Test
    fun testBlankPosterPath() = runTest {
        val movie = Movie(
            id = 101,
            title = "No Poster Movie",
            overview = "Test Overview",
            posterPath = "",
            backdropPath = "",
            releaseDate = LocalDate.of(2019, 5,5)
        )

        assertThat(movie.posterPath).isEqualTo("")
        assertThat(movie.backdropPath).isEqualTo("")
    }

    @Test
    fun testAllThreeApisAreCalled() = runTest {
        var popularCalled = false
        var topRatedCalled = false
        var nowPlayingCalled = false

        coEvery { mockRepository.getPopularMovies() } answers {
            popularCalled = true
            emptyList()
        }
        coEvery { mockRepository.getTopRatedMovies() } answers {
            topRatedCalled = true
            emptyList()
        }
        coEvery { mockRepository.getNowPlayingMovies() } answers {
            nowPlayingCalled = true
            emptyList()
        }

        viewModel = HomeViewModel(mockRepository)
        advanceUntilIdle()

        assertThat(popularCalled).isTrue()
        assertThat(topRatedCalled).isTrue()
        assertThat(nowPlayingCalled).isTrue()
    }
}