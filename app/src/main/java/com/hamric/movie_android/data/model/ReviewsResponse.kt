package com.hamric.movie_android.data.model

import com.google.gson.annotations.SerializedName

data class ReviewsResponse(
    val id: Int,
    val page: Int,
    val results: List<ReviewResponse>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class ReviewResponse(
    val id: String,
    val author: String,
    val content: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("author_details") val authorDetail: AuthorDetailResponse
)
data class AuthorDetailResponse(
    val name: String,
    val username: String,
    @SerializedName("avatar_path") val avatarPath: String?
)