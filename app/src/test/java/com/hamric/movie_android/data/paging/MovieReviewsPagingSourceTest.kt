package com.hamric.movie_android.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hamric.movie_android.data.api.MovieApiService
import com.hamric.movie_android.data.model.AuthorDetailResponse
import com.hamric.movie_android.data.model.ReviewResponse
import com.hamric.movie_android.data.model.ReviewsResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import com.hamric.movie_android.data.model.MovieReview

@OptIn(ExperimentalCoroutinesApi::class)
class MovieReviewsPagingSourceTest {

    private val mockApiService: MovieApiService = mockk()
    private val movieId = 123u

    private fun createMockReview(
        id: String = "1",
        author: String = "John Doe",
        name: String = "John Doe",
        username: String = "johndoe",
        avatarPath: String? = "/avatar.jpg",
        rating: Double? = null,
        content: String = "Great movie!",
        createdAt: String = "2024-01-01T00:00:00.000Z",
        updatedAt: String = "2024-01-01T00:00:00.000Z"
    ): ReviewResponse {
        return ReviewResponse(
            id = id,
            author = author,
            authorDetail = AuthorDetailResponse(
                name = name,
                username = username,
                avatarPath = avatarPath
            ),
            content = content,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    @Test
    fun testLoadFirstPageSuccess() = runTest {
        val mockReviews = listOf(
            createMockReview("1", "John Doe", name = "John Doe"),
            createMockReview("2", "Jane Smith", name = "Jane Smith")
        )
        val mockResponse = ReviewsResponse(
            id = 123,
            page = 1,
            results = mockReviews,
            totalPages = 3,
            totalResults = 45
        )

        coEvery { mockApiService.getMovieReviews(movieId, 1u) } returns mockResponse

        val pagingSource = MovieReviewsPagingSource(mockApiService, movieId)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result is PagingSource.LoadResult.Page).isTrue()
        val pageResult = result as PagingSource.LoadResult.Page
        assertThat(pageResult.data).hasSize(2)
        assertThat(pageResult.data[0].id).isEqualTo("1")
        assertThat(pageResult.data[0].authorName).isEqualTo("John Doe")
        assertThat(pageResult.prevKey).isNull()
        assertThat(pageResult.nextKey).isEqualTo(2)
    }

    @Test
    fun testLoadNextPageSuccess() = runTest {
        val mockReviews = listOf(
            createMockReview("21", "User 21", name = "User 21"),
            createMockReview("22", "User 22", name = "User 22")
        )
        val mockResponse = ReviewsResponse(
            id = 123,
            page = 2,
            results = mockReviews,
            totalPages = 3,
            totalResults = 45
        )

        coEvery { mockApiService.getMovieReviews(movieId, 2u) } returns mockResponse

        val pagingSource = MovieReviewsPagingSource(mockApiService, movieId)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result is PagingSource.LoadResult.Page).isTrue()
        val pageResult = result as PagingSource.LoadResult.Page
        assertThat(pageResult.data).hasSize(2)
        assertThat(pageResult.prevKey).isEqualTo(1)
        assertThat(pageResult.nextKey).isEqualTo(3)
    }

    @Test
    fun testLoadLastPageSuccess() = runTest {
        val mockReviews = listOf(createMockReview("41", "Last User", name = "Last User"))
        val mockResponse = ReviewsResponse(
            id = 123,
            page = 3,
            results = mockReviews,
            totalPages = 3,
            totalResults = 45
        )

        coEvery { mockApiService.getMovieReviews(movieId, 3u) } returns mockResponse

        val pagingSource = MovieReviewsPagingSource(mockApiService, movieId)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 3,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result is PagingSource.LoadResult.Page).isTrue()
        val pageResult = result as PagingSource.LoadResult.Page
        assertThat(pageResult.data).hasSize(1)
        assertThat(pageResult.prevKey).isEqualTo(2)
        assertThat(pageResult.nextKey).isNull()
    }

    @Test
    fun testLoadError() = runTest {
        val errorMessage = "Network error - Unable to connect to API"
        coEvery { mockApiService.getMovieReviews(movieId, 1u) } throws Exception(errorMessage)

        val pagingSource = MovieReviewsPagingSource(mockApiService, movieId)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result is PagingSource.LoadResult.Error).isTrue()
        val errorResult = result as PagingSource.LoadResult.Error
        assertThat(errorResult.throwable.message).isEqualTo(errorMessage)
    }

    @Test
    fun testLoadEmptyReviews() = runTest {
        val mockResponse = ReviewsResponse(
            id = 123,
            page = 1,
            results = emptyList(),
            totalPages = 0,
            totalResults = 0
        )

        coEvery { mockApiService.getMovieReviews(movieId, 1u) } returns mockResponse

        val pagingSource = MovieReviewsPagingSource(mockApiService, movieId)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result is PagingSource.LoadResult.Page).isTrue()
        val pageResult = result as PagingSource.LoadResult.Page
        assertThat(pageResult.data).isEmpty()
        assertThat(pageResult.prevKey).isNull()
        assertThat(pageResult.nextKey).isNull()
    }

    @Test
    fun testLoadSinglePageNoMorePages() = runTest {
        val mockReviews = listOf(
            createMockReview("1", "User 1", name = "User 1"),
            createMockReview("2", "User 2", name = "User 2")
        )
        val mockResponse = ReviewsResponse(
            id = 123,
            page = 1,
            results = mockReviews,
            totalPages = 1,
            totalResults = 2
        )

        coEvery { mockApiService.getMovieReviews(movieId, 1u) } returns mockResponse

        val pagingSource = MovieReviewsPagingSource(mockApiService, movieId)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result is PagingSource.LoadResult.Page).isTrue()
        val pageResult = result as PagingSource.LoadResult.Page
        assertThat(pageResult.data).hasSize(2)
        assertThat(pageResult.prevKey).isNull()
        assertThat(pageResult.nextKey).isNull()
    }

    @Test
    fun testAuthorNameFallbackLogic() = runTest {
        val review1 = createMockReview(
            id = "1",
            author = "Main Author",
            name = "Detail Name",
            username = "username"
        )

        val review2 = createMockReview(
            id = "2",
            author = "",
            name = "Detail Name Only",
            username = "username123"
        )

        val review3 = createMockReview(
            id = "3",
            author = "",
            name = "",
            username = "fallbackUsername"
        )

        val mockResponse = ReviewsResponse(
            id = 123,
            page = 1,
            results = listOf(review1, review2, review3),
            totalPages = 1,
            totalResults = 3
        )

        coEvery { mockApiService.getMovieReviews(movieId, 1u) } returns mockResponse

        val pagingSource = MovieReviewsPagingSource(mockApiService, movieId)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result is PagingSource.LoadResult.Page).isTrue()
        val pageResult = result as PagingSource.LoadResult.Page

        assertThat(pageResult.data[0].authorName).isEqualTo("Main Author")
        assertThat(pageResult.data[1].authorName).isEqualTo("Detail Name Only")
        assertThat(pageResult.data[2].authorName).isEqualTo("fallbackUsername")
    }

    @Test
    fun testAvatarPathCleanup() = runTest {
        val reviewWithSlash = createMockReview(
            id = "1",
            author = "User",
            avatarPath = "/avatar.jpg"
        )

        val reviewWithoutSlash = createMockReview(
            id = "2",
            author = "User2",
            avatarPath = "avatar.jpg"
        )

        val reviewNullAvatar = createMockReview(
            id = "3",
            author = "User3",
            avatarPath = null
        )

        val mockResponse = ReviewsResponse(
            id = 123,
            page = 1,
            results = listOf(reviewWithSlash, reviewWithoutSlash, reviewNullAvatar),
            totalPages = 1,
            totalResults = 3
        )

        coEvery { mockApiService.getMovieReviews(movieId, 1u) } returns mockResponse

        val pagingSource = MovieReviewsPagingSource(mockApiService, movieId)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 20,
                placeholdersEnabled = false
            )
        )

        assertThat(result is PagingSource.LoadResult.Page).isTrue()
        val pageResult = result as PagingSource.LoadResult.Page

        assertThat(pageResult.data[0].avatarAuthorUrl).isEqualTo("avatar.jpg")
        assertThat(pageResult.data[1].avatarAuthorUrl).isEqualTo("avatar.jpg")
        assertThat(pageResult.data[2].avatarAuthorUrl).isEmpty()
    }

    @Test
    fun testGetRefreshKey() {
        val pagingSource = MovieReviewsPagingSource(mockApiService, movieId)

        val state = PagingState<Int, MovieReview>(
            pages = listOf(),
            anchorPosition = null,
            config = androidx.paging.PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)
        assertThat(refreshKey).isNull()
    }
}