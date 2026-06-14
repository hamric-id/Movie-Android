package com.hamric.movie_android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hamric.movie_android.data.model.MovieReview
import com.hamric.movie_android.utils.DateUtils.toString
import java.time.LocalDate
import java.util.Locale
import com.hamric.movie_android.R

@Composable
fun MovieReviewCard(
    movieReview: MovieReview,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(
                width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )

            .clip(RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier  = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(12.dp)
        ){
            AsyncImage(
                model = movieReview.avatarAuthorUrl,
                contentDescription = movieReview.authorName,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .padding(end = 0.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_default_avatar),  // While loading
                error = painterResource(R.drawable.ic_default_avatar)
            )

            Column(modifier = Modifier
                .background(Color.White)
                .padding(start=12.dp)
            ) {
                Text(
                    text = movieReview.authorName,
                    fontSize = 16.sp
                )

                Text(
                    text = movieReview.updatedAt.toString(pattern = "MMM d yyyy",locale = Locale.US),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = 20.sp
                )

                Text(
                    text = movieReview.content,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Start,
                    lineHeight = 20.sp
                )
            }
        }

    }
}

@Preview(
    name = "Light Mode",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    backgroundColor = 0xFF000000,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewReviewCard() {
    Surface(
        modifier = Modifier.padding(16.dp)
    ) {
        MovieReviewCard(
            movieReview = MovieReview(
                id = "1",
                authorName = "John Doe",
                avatarAuthorUrl = "https://secure.gravatar.com/avatar/f248ec34f953bc62cafcbdd81fddd6b6.jpg",
                content = "This movie is absolutely amazing! The cinematography is breathtaking, " +
                        "the acting is superb, and the story keeps you on the edge of your seat. " +
                        "Highly recommend watching it!",
                updatedAt = LocalDate.of(2024, 1, 15)
            )
        )
    }
}