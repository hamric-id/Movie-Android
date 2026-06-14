package com.hamric.movie_android.data.model

import com.hamric.movie_android.utils.DateUtils.toLocalDate
import java.time.LocalDate

data class MovieReview(
    val id: String,
    val authorName: String,
    val avatarAuthorPath: String,
    val content: String,
    val updatedAt: LocalDate
){
    constructor(reviewResponse: ReviewResponse): this(
        id = reviewResponse.id,
        authorName = when {
            reviewResponse.author.isNotBlank() -> reviewResponse.author
            reviewResponse.authorDetail.name.isNotBlank() -> reviewResponse.authorDetail.name
            else -> reviewResponse.authorDetail.username
        },
        avatarAuthorPath = reviewResponse.authorDetail.avatarPath?:"",
        content = reviewResponse.content,
        updatedAt = when {
            reviewResponse.updatedAt.isNotBlank() -> reviewResponse.updatedAt
            else -> reviewResponse.createdAt
        }.toLocalDate(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    )
}