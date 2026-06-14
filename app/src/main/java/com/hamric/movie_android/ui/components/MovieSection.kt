package com.hamric.movie_android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.ui.types.CardStyle
import java.time.LocalDate

@Composable
fun MovieSection(
    title: String,
    movies: List<Movie>,
    cardStyle: CardStyle,
    onFavoriteClick: (Movie) -> Unit = {},
    isFavorite: (Movie) -> Boolean = {false},
    onMovieClick: (Movie) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                when (cardStyle) {
                    CardStyle.BIG_POSTER -> {
                        BigPosterMovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie) }
                        )
                    }
                    CardStyle.WITH_DETAILS -> {
                        DetailedMovieCard(
                            movie = movie,
                            onFavoriteClick = { onFavoriteClick(movie) },
                            isFavorite = isFavorite(movie),
                            onClick = { onMovieClick(movie) }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CardViewPreview() {
    MaterialTheme {
        MovieSection (
            title = "Top Rated",
            movies = listOf(
                Movie(
                    id = 1u,
                    title = "Movie 1",
                    overview = "Overview 1cdwdwdwdwcwddww",
                    posterPath = "/tHhxWxge06goXU6ZQH1hj7vK8Hd.jpg",
                    backdropPath = "",
                    releaseDate = LocalDate.of(2021, 6, 23)
                ),
                Movie(
                    id = 2u,
                    title = "Movie 2dxwdcewcwecde",
                    overview = "Overview 2",
                    posterPath = "/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg",
                    backdropPath = "",
                    releaseDate = LocalDate.of(2021, 6, 23)
                ),
                Movie(
                    id = 3u,
                    title = "Movie 3cdeedceddceewddw",
                    overview = "Overview 3",
                    posterPath = "/3bhkrj58Vtu7enYsRolD1fZdja1.jpg",
                    backdropPath = "",
                    releaseDate = LocalDate.of(2021, 6, 23)
                )
            ),
            onFavoriteClick = {},
            isFavorite = {true},
            cardStyle = CardStyle.WITH_DETAILS
        ){ }
    }
}