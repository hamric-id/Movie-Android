package com.hamric.movie_android.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.repository.FavoriteRepository
import com.hamric.movie_android.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavoriteMovies()
    }

    fun loadFavoriteMovies() {
        viewModelScope.launch {
            // Start with loading state
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                favoriteMovies = emptyList(),
                error = null
            )

            val favoriteIds = favoriteRepository.getAllFavorites()

            if (favoriteIds.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    favoriteMovies = emptyList(),
                    error = "No favorite movies yet.\nTap the heart icon on any movie to add it to favorites."
                )
                return@launch
            }

            val deferredMovies = favoriteIds.map { id ->
                async { movieRepository.getMovieDetail(id) }
            }

            val loadedMovies = mutableListOf<Movie>()

            for (deferred in deferredMovies) {
                val movie = deferred.await()
                movie?.let {
                    loadedMovies.add(it)
                    _uiState.value = _uiState.value.copy(
                        favoriteMovies = loadedMovies.toList()
                    )
                }
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = if (loadedMovies.isEmpty()) "Failed to load favorite movies. Please check your internet connection." else null
            )
        }
    }

    fun removeFavorite(movieId: UInt) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(movieId)
            loadFavoriteMovies()
        }
    }

    fun isFavorite(movieId: UInt): Boolean {
        return uiState.value.favoriteMovies.any { it.id == movieId }
    }

    data class FavoritesUiState(
        val isLoading: Boolean = false,
        val favoriteMovies: List<Movie> = emptyList(),
        val error: String? = null
    )
}