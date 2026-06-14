package com.hamric.movie_android.data.repository

import com.hamric.movie_android.data.api.MovieApiService
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.model.MovieReview
import com.hamric.movie_android.utils.LocaleUtils.toString
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class MovieRepository @Inject constructor(
    private val apiService: MovieApiService
) {
    private val languange = Locale.getDefault().toString(separator = '-')
    suspend fun getPopularMovies(page: UInt = 1u): List<Movie> {
        return try {
            apiService.getPopularMovies(
                language = languange,
                region = Locale.getDefault().country,
                page = page
            ).results.map{movieResponse ->
                Movie(movieResponse)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTopRatedMovies(page: UInt = 1u): List<Movie> {
        return try {
            apiService.getTopRatedMovies(
                language = languange,
                region = Locale.getDefault().country,
                page = page
            ).results.map{movieResponse ->
                Movie(movieResponse)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getNowPlayingMovies(page: UInt = 1u): List<Movie> {
        return try {
            apiService.getNowPlayingMovies(
                language = languange,
                region = Locale.getDefault().country,
                page = page
            ).results.map{movieResponse ->
                Movie(movieResponse)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMovieDetail(movieId: UInt): Movie? {
        return try {
            Movie(
                movieResponse = apiService.getMovieDetail(
                    movieId = movieId,
                    languange
                )
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMovieReviews(movieId: UInt, page: UInt = 1u): List<MovieReview> {
        return try {
            apiService.getMovieReviews(movieId,page).results.map { MovieReview(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}