package com.hamric.movie_android.data.repository

import com.hamric.movie_android.data.api.MovieApiService
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.utils.LocaleUtils.getDeviceCountryCode
import com.hamric.movie_android.utils.LocaleUtils.getDeviceLocaleString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: MovieApiService
) {
    suspend fun getPopularMovies(page: Int = 1): List<Movie> {
        return try {
            apiService.getPopularMovies(
                language = getDeviceLocaleString('-'),
                region = getDeviceCountryCode(),
                page = page
            ).results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTopRatedMovies(page: Int = 1): List<Movie> {
        return try {
            apiService.getTopRatedMovies(
                language = getDeviceLocaleString('-'),
                region = getDeviceCountryCode(),
                page = page
            ).results
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNowPlayingMovies(page: Int = 1): List<Movie> {
        return try {
            apiService.getNowPlayingMovies(
                language = getDeviceLocaleString('-'),
                region = getDeviceCountryCode(),
                page = page
            ).results
        } catch (e: Exception) {
            emptyList()
        }
    }
}