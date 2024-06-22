package dev.abdallah.rhythm.ui.screen.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.Screen
import dev.abdallah.rhythm.ui.theme.Surface
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongFilter
import dev.abdallah.rhythm.ui.viewmodel.SongState

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Album(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
    position: Int,
) {
    val topPadding = if (position == 0) 24.dp else 8.dp
    val bottomPadding = if (position == state.albums.size - 1) 112.dp else 8.dp
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
                onEvent(SongEvent.Filter(filter = SongFilter.Album(state.albums[position])))
                onEvent(SongEvent.Navigate(screen = Screen.ALBUM))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = state.albums[position].artwork,
            contentScale = ContentScale.Crop,
            contentDescription = "Album Artwork",
            modifier = Modifier
                .padding(16.dp)
                .size(64.dp)
                .clip(CircleShape)
        )
        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            text = state.albums[position].name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
                .background(Color.Gray, CircleShape)
                .align(Alignment.CenterVertically),
            text = "${state.albums[position].songs.size}",
            color = Color.LightGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Center
        )
    }
}