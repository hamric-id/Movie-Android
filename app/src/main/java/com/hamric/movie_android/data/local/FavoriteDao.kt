package com.hamric.movie_android.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Query("INSERT INTO favorite_movies (id) VALUES (:movieId)")
    suspend fun addToFavorites(movieId: Int)

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun removeFromFavorites(movieId: Int)

    @Query("SELECT * FROM favorite_movies")
    suspend fun getAllFavorites(): List<MovieEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    suspend fun isFavorite(movieId: Int): Boolean
}