package com.hamric.movie_android.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hamric.movie_android.data.api.MovieApiService
import com.hamric.movie_android.data.model.MovieReview

class MovieReviewsPagingSource(
    private val apiService: MovieApiService,
    private val movieId: UInt
) : PagingSource<Int, MovieReview>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieReview> {
        return try {
            val page = params.key ?: 1
            val response = apiService.getMovieReviews(
                movieId = movieId,
                page = page.toUInt()
            )

            LoadResult.Page(
                data = response.results.map { MovieReview(it) },
                prevKey = if (page > 1) page - 1 else null,
                nextKey = if (page < response.totalPages) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieReview>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}