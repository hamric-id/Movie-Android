package com.hamric.movie_android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.utils.DateUtils.toString
import java.time.LocalDate
import java.util.Locale

@Composable
fun DetailedMovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(190.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w185${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.
                        width(120.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = movie.title,
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        lineHeight = 14.sp
                    )

                    Text(
                        text = movie.releaseDate.run {
                            this.toString(pattern = "MMM d yyyy", locale = Locale.US)
                        },
                        fontSize = 12.sp,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        lineHeight = 12.sp
                    )
                }

                IconButton(
                    onClick = {  },
                    modifier = Modifier.offset(y = (-9).dp)
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.Red
                    )
                }
            }
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
                backdropPath = "/dyJvKsNs2KP8qQnAXbRwDjblViy.jpg",
                releaseDate = LocalDate.of(2021, 6, 23)
            )
        ){}
    }
}