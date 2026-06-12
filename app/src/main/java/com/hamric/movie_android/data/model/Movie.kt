package com.hamric.movie_android.data.model

import com.google.gson.annotations.SerializedName
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?
) {
    val posterUrl: String
        get() = "https://image.tmdb.org/t/p/w185${posterPath ?: ""}"

    val backdropUrl: String
        get() = "https://image.tmdb.org/t/p/w300${backdropPath ?: ""}"
}