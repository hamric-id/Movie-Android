package com.hamric.movie_android.ui.favorites

import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.repository.FavoriteRepository
import com.hamric.movie_android.data.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private val mockFavoriteRepository: FavoriteRepository = mockk()
    private val mockMovieRepository: MovieRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FavoritesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createMockMovie(id: Int = 1): Movie {
        return Movie(
            id = id.toUInt(),
            title = "Test Movie $id",
            overview = "This is a test overview for movie $id",
            posterPath = "/poster$id.jpg",
            backdropPath = "/backdrop$id.jpg",
            releaseDate = LocalDate.of(2024, 1, 1),
            officialUrl = "https://www.themoviedb.org/movie/$id"
        )
    }

    private fun uInt(id: Int): UInt = id.toUInt()

    @Test
    fun testLoadFavoriteMoviesSuccess() = runTest {
        val favoriteIds = listOf(1u, 2u, 3u)
        val mockMovies = listOf(
            createMockMovie(1),
            createMockMovie(2),
            createMockMovie(3)
        )

        coEvery { mockFavoriteRepository.getAllFavorites() } returns favoriteIds
        coEvery { mockMovieRepository.getMovieDetail(uInt(1)) } returns mockMovies[0]
        coEvery { mockMovieRepository.getMovieDetail(uInt(2)) } returns mockMovies[1]
        coEvery { mockMovieRepository.getMovieDetail(uInt(3)) } returns mockMovies[2]

        viewModel = FavoritesViewModel(mockFavoriteRepository, mockMovieRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.favoriteMovies).hasSize(3)
        assertThat(state.favoriteMovies[0].title).isEqualTo("Test Movie 1")
        assertThat(state.favoriteMovies[0].id).isEqualTo(1u)
        assertThat(state.favoriteMovies[1].id).isEqualTo(2u)
        assertThat(state.favoriteMovies[2].id).isEqualTo(3u)
        assertThat(state.error).isNull()
    }


    @Test
    fun testEmptyFavorites() = runTest {
        coEvery { mockFavoriteRepository.getAllFavorites() } returns emptyList()

        viewModel = FavoritesViewModel(mockFavoriteRepository, mockMovieRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.favoriteMovies).isEmpty()
        assertThat(state.error).isEqualTo("No favorite movies yet.\nTap the heart icon on any movie to add it to favorites.")
    }

    @Test
    fun testPartialFailure() = runTest {
        val favoriteIds = listOf(1u, 2u, 3u)
        val mockMovies = listOf(
            createMockMovie(1),
            createMockMovie(3)
        )

        coEvery { mockFavoriteRepository.getAllFavorites() } returns favoriteIds
        coEvery { mockMovieRepository.getMovieDetail(uInt(1)) } returns mockMovies[0]
        coEvery { mockMovieRepository.getMovieDetail(uInt(2)) } returns null
        coEvery { mockMovieRepository.getMovieDetail(uInt(3)) } returns mockMovies[1]

        viewModel = FavoritesViewModel(mockFavoriteRepository, mockMovieRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.favoriteMovies).hasSize(2)
        assertThat(state.favoriteMovies[0].id).isEqualTo(1u)
        assertThat(state.favoriteMovies[1].id).isEqualTo(3u)
        assertThat(state.error).isNull()
    }

    @Test
    fun testCompleteFailure() = runTest {
        val favoriteIds = listOf(1u, 2u, 3u)

        coEvery { mockFavoriteRepository.getAllFavorites() } returns favoriteIds
        coEvery { mockMovieRepository.getMovieDetail(any()) } returns null

        viewModel = FavoritesViewModel(mockFavoriteRepository, mockMovieRepository)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.favoriteMovies).isEmpty()
        assertThat(state.error).isEqualTo("Failed to load favorite movies. Please check your internet connection.")
    }

    @Test
    fun testRemoveFavorite() = runTest {
        val favoriteIds = listOf(1u, 2u)
        val mockMovies = listOf(
            createMockMovie(1),
            createMockMovie(2)
        )

        coEvery { mockFavoriteRepository.getAllFavorites() } returns favoriteIds
        coEvery { mockMovieRepository.getMovieDetail(uInt(1)) } returns mockMovies[0]
        coEvery { mockMovieRepository.getMovieDetail(uInt(2)) } returns mockMovies[1]
        coEvery { mockFavoriteRepository.removeFavorite(uInt(1)) } returns Unit

        viewModel = FavoritesViewModel(mockFavoriteRepository, mockMovieRepository)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.favoriteMovies).hasSize(2)

        viewModel.removeFavorite(1u)
        advanceUntilIdle()

        coVerify { mockFavoriteRepository.removeFavorite(uInt(1)) }
        coVerify(atLeast = 2) { mockFavoriteRepository.getAllFavorites() }
    }

    @Test
    fun testIsFavorite() = runTest {
        val favoriteIds = listOf(1u, 3u, 5u)
        val mockMovies = listOf(
            createMockMovie(1),
            createMockMovie(3),
            createMockMovie(5)
        )

        coEvery { mockFavoriteRepository.getAllFavorites() } returns favoriteIds
        coEvery { mockMovieRepository.getMovieDetail(uInt(1)) } returns mockMovies[0]
        coEvery { mockMovieRepository.getMovieDetail(uInt(3)) } returns mockMovies[1]
        coEvery { mockMovieRepository.getMovieDetail(uInt(5)) } returns mockMovies[2]

        viewModel = FavoritesViewModel(mockFavoriteRepository, mockMovieRepository)
        advanceUntilIdle()

        assertThat(viewModel.isFavorite(1u)).isTrue()
        assertThat(viewModel.isFavorite(3u)).isTrue()
        assertThat(viewModel.isFavorite(5u)).isTrue()
        assertThat(viewModel.isFavorite(2u)).isFalse()
        assertThat(viewModel.isFavorite(4u)).isFalse()
    }

    @Test
    fun testRemoveFavoriteWhenListEmpty() = runTest {
        coEvery { mockFavoriteRepository.getAllFavorites() } returns emptyList()
        coEvery { mockFavoriteRepository.removeFavorite(any()) } returns Unit

        viewModel = FavoritesViewModel(mockFavoriteRepository, mockMovieRepository)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.favoriteMovies).isEmpty()
        assertThat(viewModel.uiState.value.error).isNotNull()

        viewModel.removeFavorite(1u)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.favoriteMovies).isEmpty()
    }

    @Test
    fun testRetryAfterError() = runTest {
        val favoriteIds = listOf(1u)

        coEvery { mockFavoriteRepository.getAllFavorites() } returns favoriteIds
        coEvery { mockMovieRepository.getMovieDetail(uInt(1)) } returns null

        viewModel = FavoritesViewModel(mockFavoriteRepository, mockMovieRepository)
        advanceUntilIdle()

        assertThat(viewModel.uiState.value.error).isEqualTo("Failed to load favorite movies. Please check your internet connection.")
        assertThat(viewModel.uiState.value.favoriteMovies).isEmpty()

        coEvery { mockMovieRepository.getMovieDetail(uInt(1)) } returns createMockMovie(1)

        viewModel.loadFavoriteMovies()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertThat(state.error).isNull()
        assertThat(state.favoriteMovies).hasSize(1)
        assertThat(state.favoriteMovies[0].title).isEqualTo("Test Movie 1")
    }
}