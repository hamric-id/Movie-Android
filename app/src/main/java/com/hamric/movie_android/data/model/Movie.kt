package com.hamric.movie_android.data.model


import com.hamric.movie_android.utils.DateUtils.toLocalDate
import java.time.LocalDate

data class Movie(
    val id: UInt,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: LocalDate,
    var officialUrl: String? = null
){
    constructor(movieResponse: MovieResponse): this(
        id = movieResponse.id,
        title = movieResponse.title,
        overview = movieResponse.overview,
        posterPath = movieResponse.posterPath ?: "",
        backdropPath = movieResponse.backdropPath ?: "",
        releaseDate = movieResponse.releaseDate.toLocalDate(pattern= "yyyy-MM-dd"),
        officialUrl = movieResponse.officialUrl
    )
}