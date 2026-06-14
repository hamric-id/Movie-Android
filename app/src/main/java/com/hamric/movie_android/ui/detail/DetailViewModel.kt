package com.hamric.movie_android.ui.detail


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.model.MovieReview
import com.hamric.movie_android.data.repository.FavoriteRepository
import com.hamric.movie_android.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class DetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: UInt = checkNotNull(savedStateHandle["movieId"]).let { id ->
        when (id) {
            is Int -> id.toUInt()
            else -> id as UInt
        }
    }

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()


    fun setUiStateForPreview(uiState: DetailUiState) {
        _uiState.value = uiState
    }

    init {
        loadMovieDetails()
        loadReviews()
        checkFavoriteStatus()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.isFavorite) {
                favoriteRepository.removeFavorite(movieId)
                _uiState.update { it.copy(isFavorite = false) }
            } else {
                favoriteRepository.addFavorite(movieId)
                _uiState.update { it.copy(isFavorite = true) }
            }
        }
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val details = movieRepository.getMovieDetail(movieId)
            if (details != null) {
                _uiState.update { it.copy(movie = details, isLoading = false) }
            } else {
                _uiState.update { it.copy(error = "Failed to load movie details", isLoading = false) }
            }
        }
    }

    private fun loadReviews() {
        viewModelScope.launch {
            val reviews = movieRepository.getMovieReviews(movieId= movieId, page= 1u)
            _uiState.update { it.copy(movieReviews = reviews) }
        }
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            val favorite = favoriteRepository.isFavorite(movieId)
            _uiState.update { it.copy(isFavorite = favorite) }
        }
    }

    data class DetailUiState(
        val isLoading: Boolean = false,
        val movie: Movie? = null,
        val movieReviews: List<MovieReview> = emptyList(),
        val isFavorite: Boolean = false,
        val error: String? = null
    )
}