package com.hamric.movie_android.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hamric.movie_android.data.model.Movie
import java.time.LocalDate

@Composable
fun BigPosterMovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(320.dp)
            .height(180.dp),
        onClick = onClick
    ) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w300${movie.backdropPath}",
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = movie.title,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 8.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                maxLines = 1,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun CardViewPosterPreview() {
    MaterialTheme {
        BigPosterMovieCard (
            Movie(
                id = 1u,
                title = "Movie 1",
                overview = "",
                posterPath = "/tHhxWxge06goXU6ZQH1hj7vK8Hd.jpg",
                backdropPath = "/dyJvKsNs2KP8qQnAXbRwDjblViy.jpg",
                releaseDate = LocalDate.of(2021, 6, 23)
            )
        ){}
    }
}

