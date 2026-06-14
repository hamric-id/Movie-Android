package com.hamric.movie_android.data.model

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val id: UInt,
    val title: String,
    val overview: String,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String,
    @SerializedName("homepage") val officialUrl: String?,
)