package com.hamric.movie_android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hamric.movie_android.data.model.Movie
import com.hamric.movie_android.utils.DateUtils.toString
import java.time.LocalDate
import java.util.Locale

@Composable
fun FavoriteMovieCard(
    movie: Movie,
    onMovieClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable { onMovieClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w185${movie.posterPath}",
                contentDescription = movie.title,
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(top = 4.dp, bottom = 4.dp, start = 10.dp, end = 0.dp ),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = movie.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = "Release: ${movie.releaseDate.toString(pattern = "MMM d, yyyy", locale = Locale.US)}",
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    textAlign = TextAlign.Start
                )

                Text(
                    text = movie.overview,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    lineHeight = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(0.dp),
                verticalArrangement = Arrangement.Top
            ) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .size(48.dp)
                        .offset(y = (-8).dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color.Red else Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview(name = "Favorite Movie Card - Not Favorited", showBackground = true)
@Composable
fun PreviewFavoriteMovieCardNotFavorited() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            FavoriteMovieCard(
                movie = Movie(
                    id = 1u,
                    title = "The Dark Knight",
                    overview = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.",
                    posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                    backdropPath = "/zfbjgQE1uSd9wiPTX4VzsLi0rGG.jpg",
                    releaseDate = LocalDate.of(2008, 7, 18),
                    officialUrl = "https://www.themoviedb.org/movie/155"
                ),
                onMovieClick = {},
                onFavoriteClick = {},
                isFavorite = false
            )
        }
    }
}