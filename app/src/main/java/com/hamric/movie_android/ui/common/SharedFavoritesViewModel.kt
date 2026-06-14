package com.hamric.movie_android.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedFavoritesViewModel @Inject constructor() : ViewModel() {

    private val _refreshFavorites = MutableSharedFlow<Unit>()
    val refreshFavorites: SharedFlow<Unit> = _refreshFavorites.asSharedFlow()

    fun notifyFavoritesChanged() {
        viewModelScope.launch {
            _refreshFavorites.emit(Unit)
        }
    }
}