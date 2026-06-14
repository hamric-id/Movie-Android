package com.hamric.movie_android.data.repository

import com.hamric.movie_android.data.local.FavoriteDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class FavoriteRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    suspend fun addFavorite(movieId: UInt) = favoriteDao.addToFavorites(movieId.toInt())
    suspend fun removeFavorite(movieId: UInt) = favoriteDao.removeFromFavorites(movieId.toInt())
    suspend fun isFavorite(movieId: UInt): Boolean = favoriteDao.isFavorite(movieId.toInt())
    suspend fun getAllFavorites(): List<UInt> = favoriteDao.getAllFavorites().map { it.id.toUInt() }
}