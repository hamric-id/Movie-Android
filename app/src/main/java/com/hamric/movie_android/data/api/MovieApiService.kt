package com.hamric.movie_android.data.api

import com.hamric.movie_android.data.model.MovieResponse
import com.hamric.movie_android.data.model.MoviesResponse
import com.hamric.movie_android.data.model.ReviewsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String,
        @Query("region") region: String,
        @Query("page") page: UInt = 1u
    ): MoviesResponse

    @GET("3/movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") language: String,
        @Query("region") region: String,
        @Query("page") page: UInt = 1u
    ): MoviesResponse

    @GET("3/movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") language: String,
        @Query("region") region: String,
        @Query("page") page: UInt = 1u
    ): MoviesResponse

    @GET("3/movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: UInt,
        @Query("language") language: String
    ): MovieResponse

    @GET("3/movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: UInt,
        @Query("page") page: UInt = 1u
    ): ReviewsResponse
}