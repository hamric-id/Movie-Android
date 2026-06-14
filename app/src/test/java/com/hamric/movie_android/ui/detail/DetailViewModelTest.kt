package com.hamric.movie_android.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.repository.FavoriteRepository
import com.hamric.movie_android.data.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
class DetailViewModelTest {

    private val mockMovieRepository: MovieRepository = mockk()
    private val mockFavoriteRepository: FavoriteRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createMockMovie(id: UInt = 2u): Movie {
        return Movie(
            id = id,
            title = "The Dark Knight",
            overview = "When the menace known as the Joker wreaks havoc...",
            posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
            backdropPath = "/zfbjgQE1uSd9wiPTX4VzsLi0rGG.jpg",
            releaseDate = LocalDate.of(2008, 7, 18),
            officialUrl = "https://www.themoviedb.org/movie/$id"
        )
    }

    private fun setupCommonMocks() {
        coEvery { mockMovieRepository.getMovieReviewsPaging(any()) } returns flowOf(PagingData.empty())
    }

    @Test
    fun testLoadMovieDetailsSuccess() = runTest {
        setupCommonMocks()  

        val mockMovie = createMockMovie()
        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))

        coEvery { mockMovieRepository.getMovieDetail(2u) } returns mockMovie
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns false

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )
        advanceUntilIdle()

        assertThat(viewModel.isLoading.value).isFalse()
        assertThat(viewModel.movieState.value).isNotNull()
        assertThat(viewModel.movieState.value?.title).isEqualTo("The Dark Knight")
        assertThat(viewModel.error.value).isNull()
    }

    @Test
    fun testLoadMovieDetailsError() = runTest {
        setupCommonMocks()  

        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 999))

        coEvery { mockMovieRepository.getMovieDetail(999u) } returns null
        coEvery { mockFavoriteRepository.isFavorite(999u) } returns false

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )
        advanceUntilIdle()

        assertThat(viewModel.isLoading.value).isFalse()
        assertThat(viewModel.movieState.value).isNull()
        assertThat(viewModel.error.value).isEqualTo("Failed to load movie details. Please check your internet connection.")
    }

    @Test
    fun testCheckFavoriteStatusWhenFavorite() = runTest {
        setupCommonMocks()  

        val mockMovie = createMockMovie()
        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))

        coEvery { mockMovieRepository.getMovieDetail(2u) } returns mockMovie
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns true

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )
        advanceUntilIdle()

        assertThat(viewModel.isFavorite.value).isTrue()
    }

    @Test
    fun testCheckFavoriteStatusWhenNotFavorite() = runTest {
        setupCommonMocks()  

        val mockMovie = createMockMovie()
        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))

        coEvery { mockMovieRepository.getMovieDetail(2u) } returns mockMovie
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns false

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )
        advanceUntilIdle()

        assertThat(viewModel.isFavorite.value).isFalse()
    }

    @Test
    fun testToggleFavoriteAdd() = runTest {
        setupCommonMocks()  

        val mockMovie = createMockMovie()
        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))

        coEvery { mockMovieRepository.getMovieDetail(2u) } returns mockMovie
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns false
        coEvery { mockFavoriteRepository.addFavorite(2u) } returns Unit

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )
        advanceUntilIdle()

        assertThat(viewModel.isFavorite.value).isFalse()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        coVerify { mockFavoriteRepository.addFavorite(2u) }
        assertThat(viewModel.isFavorite.value).isTrue()
    }

    @Test
    fun testToggleFavoriteRemove() = runTest {
        setupCommonMocks()  

        val mockMovie = createMockMovie()
        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))

        coEvery { mockMovieRepository.getMovieDetail(2u) } returns mockMovie
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns true
        coEvery { mockFavoriteRepository.removeFavorite(2u) } returns Unit

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )
        advanceUntilIdle()

        assertThat(viewModel.isFavorite.value).isTrue()

        viewModel.toggleFavorite()
        advanceUntilIdle()

        coVerify { mockFavoriteRepository.removeFavorite(2u) }
        assertThat(viewModel.isFavorite.value).isFalse()
    }

    @Test
    fun testLoadingStateDuringFetch() = runTest {
        setupCommonMocks()  

        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))

        coEvery { mockMovieRepository.getMovieDetail(2u) } coAnswers {
            kotlinx.coroutines.delay(1000)
            createMockMovie()
        }
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns false

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )

        assertThat(viewModel.isLoading.value).isTrue()

        advanceUntilIdle()

        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun testRetryLoadingAfterError() = runTest {
        setupCommonMocks()  

        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))

        coEvery { mockMovieRepository.getMovieDetail(2u) } returns null
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns false

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )
        advanceUntilIdle()

        assertThat(viewModel.error.value).isNotNull()
        assertThat(viewModel.movieState.value).isNull()

        val mockMovie = createMockMovie()
        coEvery { mockMovieRepository.getMovieDetail(2u) } returns mockMovie

        viewModel.retryLoading()
        advanceUntilIdle()

        assertThat(viewModel.error.value).isNull()
        assertThat(viewModel.movieState.value).isNotNull()
        assertThat(viewModel.movieState.value?.title).isEqualTo("The Dark Knight")
        assertThat(viewModel.isLoading.value).isFalse()
    }

    @Test
    fun testMovieIdConversionFromInt() = runTest {
        setupCommonMocks()  

        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 123))

        coEvery { mockMovieRepository.getMovieDetail(123u) } returns createMockMovie(123u)
        coEvery { mockFavoriteRepository.isFavorite(123u) } returns false

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )
        advanceUntilIdle()

        coVerify { mockMovieRepository.getMovieDetail(123u) }
        assertThat(viewModel.movieState.value?.id).isEqualTo(123u)
    }

    @Test
    fun testReviewsPagingFlowIsConfigured() = runTest {
        setupCommonMocks()  

        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))

        coEvery { mockMovieRepository.getMovieDetail(2u) } returns createMockMovie()
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns false

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )
        advanceUntilIdle()

        assertThat(viewModel.reviewsPagingFlow).isNotNull()
    }

    @Test
    fun testSetPreviewData() = runTest {
        setupCommonMocks()  

        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))
        val previewMovie = createMockMovie(99u)

        coEvery { mockMovieRepository.getMovieDetail(2u) } returns createMockMovie()
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns false

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )

        viewModel.setPreviewData(movie = previewMovie, favorite = true)

        assertThat(viewModel.movieState.value?.id).isEqualTo(99u)
        assertThat(viewModel.isFavorite.value).isTrue()
        assertThat(viewModel.isLoading.value).isFalse()
        assertThat(viewModel.error.value).isNull()
    }

    @Test
    fun testScopeIsAvailable() = runTest {
        setupCommonMocks()  

        val savedStateHandle = SavedStateHandle(mapOf("movieId" to 2))

        coEvery { mockMovieRepository.getMovieDetail(2u) } returns createMockMovie()
        coEvery { mockFavoriteRepository.isFavorite(2u) } returns false

        viewModel = DetailViewModel(
            movieRepository = mockMovieRepository,
            favoriteRepository = mockFavoriteRepository,
            savedStateHandle = savedStateHandle
        )

        assertThat(viewModel.scope).isNotNull()
    }
}