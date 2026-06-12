package com.hamric.movie_android.data.api

import com.hamric.movie_android.data.model.MoviesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiService {
    @GET("3/movie/popular")
    suspend fun getPopularMovies(
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "US",
        @Query("page") page: Int = 1
    ): MoviesResponse

    @GET("3/movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "US",
        @Query("page") page: Int = 1
    ): MoviesResponse

    @GET("3/movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("language") language: String = "en-US",
        @Query("region") region: String = "US",
        @Query("page") page: Int = 1
    ): MoviesResponse
}