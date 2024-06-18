package dev.abdallah.rhythm.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.data.db.Playlist
import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.ui.screen.Playlists
import dev.abdallah.rhythm.ui.theme.Gray
import dev.abdallah.rhythm.ui.theme.Surface
import dev.abdallah.rhythm.util.THUMBNAIL_SMALL_SIZE
import dev.abdallah.rhythm.util.millisecondsToMinutes
import dev.abdallah.rhythm.util.mirror

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalAnimationGraphicsApi::class)
@Composable
fun MiniPlayer(
    offset: Float,
    progress: Float,
    mutedColor: Color,
    vibrantColor: Color,
    onProgress: (Float) -> Unit,
    isPlaying: Boolean,
    nowPlaying: Song,
    onStart: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    isFavorite: Boolean,
    onFavorite: (Song) -> Unit,
    playlists: List<Playlist>,
    onAddToPlaylist: (Song, Playlist) -> Unit,
) {
    var showMoreOptionsBottomSheet by remember {
        mutableStateOf(false)
    }
    var showAddToPlaylistBottomSheet by remember {
        mutableStateOf(false)
    }
    if (showMoreOptionsBottomSheet) {
        MoreOptions(onDismiss = {
            showMoreOptionsBottomSheet = false
        }, onAddToPlaylist = {
            showMoreOptionsBottomSheet = false
            showAddToPlaylistBottomSheet = true
        })
    }
    if (showAddToPlaylistBottomSheet) {
        AddToPlaylist(
            playlists = playlists,
            onItemClick = { playlist ->
                onAddToPlaylist(nowPlaying, playlist)
                showAddToPlaylistBottomSheet = false
            },
            onDismiss = {
                showAddToPlaylistBottomSheet = false
            }
        )
    }
    val animatedPadding by animateDpAsState(
        targetValue = if (isPlaying) {
            24.dp
        } else {
            48.dp
        }, label = "padding", animationSpec = tween(durationMillis = 250, easing = LinearEasing)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Mini Player
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(alpha = offset),
        ) {
            GlideImage(
                model = nowPlaying.artworkSmall,
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(THUMBNAIL_SMALL_SIZE.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterVertically),
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                text = nowPlaying.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterVertically),
                onClick = { onStart() }) {
                Icon(
                    painter = rememberAnimatedVectorPainter(
                        animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.play_pause_animation),
                        atEnd = isPlaying
                    ),
                    contentDescription = "Play/Pause",
                    tint = mutedColor,
                    modifier = Modifier
                        .size(48.dp)
                        .mirror()
                )
            }
            IconButton(modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterVertically),
                onClick = { onNext() }) {
                Icon(
                    modifier = Modifier.mirror(),
                    painter = painterResource(id = R.drawable.fast_forward_24px),
                    contentDescription = "Skip",
                    tint = mutedColor
                )
            }
        }

        // Expanded Mini Player
        Column {
            GlideImage(
                model = nowPlaying.artworkLarge,
                contentDescription = "Album Art",
                modifier = Modifier
                    .padding(animatedPadding)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(24.dp)),
            )
            Text(
                text = nowPlaying.title,
                color = mutedColor,
                modifier = Modifier.padding(16.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.W600,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = nowPlaying.artist,
                color = vibrantColor,
                modifier = Modifier.padding(horizontal = 16.dp),
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.weight(1f))
            var isSeeking by remember {
                mutableStateOf(false)
            }
            var seekbarValue by remember {
                mutableFloatStateOf(progress)
            }
            LaunchedEffect(key1 = progress) {
                if (!isSeeking) {
                    seekbarValue = progress
                }
            }
            val animatedValue by animateFloatAsState(
                targetValue = if (isSeeking) seekbarValue else progress,
                label = "seekbar",
                animationSpec = tween(
                    durationMillis = if (isSeeking) 0 else 500, easing = LinearEasing
                )
            )
            // SeekBar
            Slider(value = animatedValue, onValueChange = { value ->
                isSeeking = true
                seekbarValue = value
            }, onValueChangeFinished = {
                onProgress(seekbarValue * 100f)
                isSeeking = false
            }, colors = SliderDefaults.colors(
                thumbColor = mutedColor,
                activeTrackColor = mutedColor,
                inactiveTrackColor = vibrantColor,
            ), modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
            )
            // Duration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = millisecondsToMinutes((nowPlaying.duration * seekbarValue).toInt()),
                    color = vibrantColor,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = millisecondsToMinutes(nowPlaying.duration),
                    color = vibrantColor,
                    fontSize = 12.sp
                )

            }
            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(modifier = Modifier.size(64.dp), onClick = { onPrevious() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.fast_rewind_24px),
                        contentDescription = "Previous",
                        tint = mutedColor,
                        modifier = Modifier.mirror(),
                    )
                }
                IconButton(modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterVertically),
                    onClick = { onStart() }) {
                    Icon(
                        painter = rememberAnimatedVectorPainter(
                            animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.play_pause_animation),
                            atEnd = isPlaying
                        ),
                        contentDescription = "Play/Pause",
                        tint = mutedColor,
                        modifier = Modifier
                            .size(80.dp)
                            .mirror()
                    )
                }
                IconButton(modifier = Modifier.size(64.dp), onClick = { onNext() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.fast_forward_24px),
                        contentDescription = "Skip",
                        tint = mutedColor,
                        modifier = Modifier.mirror(),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
                    .align(Alignment.CenterVertically),
                    onClick = { onFavorite(nowPlaying) }) {
                    Icon(
                        painter = rememberAnimatedVectorPainter(
                            animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.favourite_animation),
                            atEnd = isFavorite
                        ),
                        contentDescription = "Favorite",
                        modifier = Modifier
                            .size(32.dp)
                            .mirror(),
                        tint = Color.Unspecified,
                    )
                }
                IconButton(modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp), onClick = {
                    showMoreOptionsBottomSheet = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_more_horiz_24),
                        contentDescription = "More",
                        tint = Color.White,
                        modifier = Modifier.mirror(),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptions(
    onDismiss: () -> Unit,
    onAddToPlaylist: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = Surface,
        sheetState = sheetState,
        dragHandle = { },
    ) {
        Column(modifier = Modifier.padding(vertical = 32.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddToPlaylist() }
                    .padding(horizontal = 32.dp, vertical = 16.dp)) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(id = R.drawable.playlist_add_24px),
                    contentDescription = "contentDescription",
                    tint = Color.White,
                )
                Text(
                    text = "Add to Playlist",
                    modifier = Modifier
                        .weight(1f, false)
                        .padding(start = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Icon(
                    modifier = Modifier.size(36.dp),
                    painter = painterResource(id = R.drawable.ios_share_24px),
                    contentDescription = "contentDescription",
                    tint = Color.White,
                )
                Text(
                    text = "Share",
                    modifier = Modifier
                        .weight(1f, false)
                        .padding(start = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddToPlaylist(
    playlists: List<Playlist>,
    onItemClick: (Playlist) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        containerColor = Surface,
        sheetState = sheetState,
        dragHandle = { },
    ) {
        Column {
            Text(
                modifier = Modifier
                    .padding(top = 36.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Add to Playlist",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.W600,
            )
            Playlists(
                playlists = playlists,
                onItemClick = {
                    onItemClick(playlists[it])
                },
                showAddPlaylistButton = false,
            )
        }
    }
}
