package dev.abdallah.rhythm.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.ui.theme.Blue
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongState
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationGraphicsApi::class
)
@Composable
fun Song(
    state: SongState,
    songs: List<dev.abdallah.rhythm.data.db.Song>,
    onEvent: (SongEvent) -> Unit,
    position: Int,
    ) {
    val topPadding = if (position == 0) 24.dp else 8.dp
    val bottomPadding = if (position == songs.size - 1) 112.dp else 8.dp
    val nowPlaying = state.queue.getOrElse(state.index) { dev.abdallah.rhythm.data.db.Song.NONE }
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
                onClick = { onEvent(SongEvent.Change(songs = songs, index = position)) },
                onLongClick = { }
            )
    ) {
        Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
            GlideImage(
                model = songs[position].artworkSmall,
                contentScale = ContentScale.Crop,
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp)),
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = songs[position].id == nowPlaying.id,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                var atEnd by remember { mutableStateOf(false) }
                LaunchedEffect(state.isPlaying) {
                    snapshotFlow { state.isPlaying }.distinctUntilChanged().collect {
                        atEnd = state.isPlaying
                    }
                }
                Icon(
                    painter = rememberAnimatedVectorPainter(
                        animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.audio_waves_animated),
                        atEnd = atEnd
                    ),
                    contentDescription = "Play/Pause",
                    tint = Color.White,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center)
                )
            }
        }


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
