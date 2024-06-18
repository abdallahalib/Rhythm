package dev.abdallah.rhythm.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.ui.theme.Blue
import dev.abdallah.rhythm.util.THUMBNAIL_SMALL_SIZE

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun Song(
    songs: List<Song>,
    position: Int,
    nowPlaying: Song,
    onItemClick: (Int) -> Unit,
    onItemLongClick: (Song) -> Unit = {},
) {
    val topPadding = if (position == 0) 24.dp else 8.dp
    val bottomPadding = if (position == songs.size - 1) 112.dp else 8.dp
    val songTitleColor by animateColorAsState(
        if (songs[position].id == nowPlaying.id) Blue else Color.White, label = "Song Title Color"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = topPadding,
                bottom = bottomPadding
            )
            .combinedClickable(
                onClick = { onItemClick(position) },
                onLongClick = { onItemLongClick(songs[position]) }
            )
    ) {
        GlideImage(
            model = songs[position].artworkSmall,
            contentDescription = "Album Art",
            modifier = Modifier
                .size(THUMBNAIL_SMALL_SIZE.dp)
                .clip(RoundedCornerShape(16.dp)),
        )
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .fillMaxHeight()
                .align(Alignment.CenterVertically)
        ) {
            Text(
                text = songs[position].title,
                color = songTitleColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = songs[position].artist,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}