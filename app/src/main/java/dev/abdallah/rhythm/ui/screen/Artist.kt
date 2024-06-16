package dev.abdallah.rhythm.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.data.local.model.Artist
import dev.abdallah.rhythm.util.THUMBNAIL_SMALL_SIZE
import dev.abdallah.rhythm.ui.theme.Surface

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Artist(
    position: Int,
    artistList: List<Artist>,
    onItemClick: (Int) -> Unit
) {
    val topPadding = if (position == 0) 24.dp else 8.dp
    val bottomPadding = if (position == artistList.size - 1) 112.dp else 8.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = topPadding,
                bottom = bottomPadding
            )
            .background(Surface, RoundedCornerShape(corner = CornerSize(24.dp)))
            .clickable {
                onItemClick(position)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = artistList[position].artworkSmall,
            contentScale = ContentScale.Crop,
            contentDescription = "Artist Artwork",
            modifier = Modifier
                .padding(16.dp)
                .size(THUMBNAIL_SMALL_SIZE.dp)
                .clip(CircleShape)
        )
        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            text = artistList[position].name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            )
    }
}