package com.hamric.movie_android.ui.detail

import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.model.MovieReview
import com.hamric.movie_android.data.repository.FavoriteRepository
import com.hamric.movie_android.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class DetailViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val favoriteRepository: FavoriteRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val scope = viewModelScope

    private val movieId: UInt = checkNotNull(savedStateHandle["movieId"]).let { id ->
        when (id) {
            is Int -> id.toUInt()
            else -> id as UInt
        }
    }

    private val _movieState = MutableStateFlow<Movie?>(null)
    val movieState: StateFlow<Movie?> = _movieState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val reviewsPagingFlow: Flow<PagingData<MovieReview>> =
        movieRepository.getMovieReviewsPaging(movieId)
            .cachedIn(viewModelScope)
            .flowOn(Dispatchers.IO)

    init {
        loadMovieDetails()
        checkFavoriteStatus()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            if (_isFavorite.value) {
                favoriteRepository.removeFavorite(movieId)
                _isFavorite.value = false
            } else {
                favoriteRepository.addFavorite(movieId)
                _isFavorite.value = true
            }
        }
    }


    private fun loadMovieDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val details = movieRepository.getMovieDetail(movieId)

            if (details != null) {
                _movieState.value = details
                _isLoading.value = false
            } else {
                _error.value = "Failed to load movie details. Please check your internet connection."
                _isLoading.value = false
            }
        }
    }


    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            _isFavorite.value = favoriteRepository.isFavorite(movieId)
        }
    }


    fun retryLoading() {
        loadMovieDetails()
    }

    fun setPreviewData(movie: Movie, favorite: Boolean = false) {
        _movieState.value = movie
        _isFavorite.value = favorite
        _isLoading.value = false
        _error.value = null
    }
}

