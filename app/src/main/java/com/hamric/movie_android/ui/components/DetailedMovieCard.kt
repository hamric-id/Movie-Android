package com.hamric.movie_android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hamric.movie_android.data.model.Movie

@Composable
fun DetailedMovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(190.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Text(
                text = movie.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 0.dp, top = 4.dp),
                textAlign = TextAlign.Start,
                maxLines = 1,
                lineHeight = 12.sp
            )

            Text(
                text = movie.overview,
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(start = 0.dp, top = 4.dp),
                textAlign = TextAlign.Start,
                maxLines = 1,
                lineHeight = 10.sp
            )
        }
    }
}

@Preview
@Composable
fun CardViewWithDetailPreview() {
    MaterialTheme {
        DetailedMovieCard (
            Movie(
                id = 1,
                title = "Movie 1",
                overview = "abcdefghijklmnopqrstuvwxyzabcdefghijklmno",
                posterPath = "/tHhxWxge06goXU6ZQH1hj7vK8Hd.jpg",
                backdropPath = "/dyJvKsNs2KP8qQnAXbRwDjblViy.jpg"
            )
        ){}
    }
}