package com.hamric.movie_android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.repository.FavoriteRepository
import com.hamric.movie_android.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    val scope = viewModelScope
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _favoriteMovies = MutableStateFlow<Set<UInt>>(emptySet())
    val favoriteMovies: StateFlow<Set<UInt>> = _favoriteMovies.asStateFlow()



    init {
        loadAllMovies()
        loadFavorites()
    }

    fun loadAllMovies() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val popularMovies = repository.getPopularMovies()
                val topRatedMovies = repository.getTopRatedMovies()
                val nowPlayingMovies = repository.getNowPlayingMovies()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    popularMovies = popularMovies,
                    topRatedMovies = topRatedMovies,
                    nowPlayingMovies = nowPlayingMovies,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            val favorites = favoriteRepository.getAllFavorites()
            _favoriteMovies.value = favorites.toSet()
        }
    }

    fun toggleFavorite(movieId: UInt) {
        viewModelScope.launch {
            if (_favoriteMovies.value.contains(movieId)) {
                favoriteRepository.removeFavorite(movieId)
                _favoriteMovies.value = _favoriteMovies.value - movieId
            } else {
                favoriteRepository.addFavorite(movieId)
                _favoriteMovies.value = _favoriteMovies.value + movieId
            }
        }
    }

    fun isFavorite(movieId: UInt): Boolean {
        return _favoriteMovies.value.contains(movieId)
    }

    data class HomeUiState(
        val isLoading: Boolean = false,
        val popularMovies: List<Movie> = emptyList(),
        val topRatedMovies: List<Movie> = emptyList(),
        val nowPlayingMovies: List<Movie> = emptyList(),
        val error: String? = null
    )
}