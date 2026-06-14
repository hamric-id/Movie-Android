package com.hamric.movie_android.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.hamric.movie_android.data.api.MovieApiService
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.data.model.MovieReview
import com.hamric.movie_android.data.paging.MovieReviewsPagingSource
import com.hamric.movie_android.utils.LocaleUtils.toString
import kotlinx.coroutines.flow.Flow
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
            ).results.map { movieResponse ->
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
            ).results.map { movieResponse ->
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
            ).results.map { movieResponse ->
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

    fun getMovieReviewsPaging(movieId: UInt): Flow<PagingData<MovieReview>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = { MovieReviewsPagingSource(apiService, movieId) }
        ).flow
    }
}