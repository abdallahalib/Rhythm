package dev.abdallah.rhythm.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongState
import dev.abdallah.rhythm.util.millisecondsToMinutes
import dev.abdallah.rhythm.util.mirror
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(
    ExperimentalGlideComposeApi::class,
    ExperimentalAnimationGraphicsApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun MiniPlayer(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
    primaryColor: Color,
    accentColor: Color,
    sheetContainerColor: Color,
    targetValue: SheetValue,
) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (targetValue == SheetValue.PartiallyExpanded) 1.0f else 0f,
        label = "alpha",
        animationSpec = tween(
            durationMillis = if (targetValue == SheetValue.PartiallyExpanded) 500 else 250,
            easing = LinearEasing
        )
    )
    val pagerState = rememberPagerState { state.queue.size }
    val song = state.queue.getOrElse(pagerState.currentPage) { Song.NONE }
    LaunchedEffect(state.index) {
        snapshotFlow { state.index }.distinctUntilChanged().collect { index ->
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(index)
            }
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect { currentPage ->
            if (pagerState.currentPage == pagerState.targetPage) {
                onEvent(SongEvent.Change(songs = state.queue, index = currentPage))
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Horizontal Pager
            HorizontalPager(state = pagerState) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = animatedAlpha
                        }) {
                        GlideImage(
                            model = song.artworkSmall,
                            contentScale = ContentScale.Crop,
                            contentDescription = "Album Art",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .align(Alignment.CenterVertically),
                        )
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            text = song.title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    val animatedPadding by animateDpAsState(
                        targetValue = if (state.isPlaying) {
                            16.dp
                        } else {
                            36.dp
                        },
                        label = "padding",
                        animationSpec = tween(durationMillis = 250, easing = LinearEasing)
                    )
                    GlideImage(
                        model = song.artworkLarge,
                        contentScale = ContentScale.Crop,
                        contentDescription = "Album Art",
                        modifier = Modifier
                            .padding(animatedPadding)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(24.dp)),
                    )
                    Text(
                        text = song.title,
                        color = primaryColor,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.W600,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = song.artist,
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            val progress =
                state.position.toFloat() / (state.queue.getOrNull(state.index)?.duration?.toFloat()
                    ?: 0f)
            var isSeeking by remember {
                mutableStateOf(false)
            }

            var seekbarValue by remember {
                mutableFloatStateOf(progress)
            }

            LaunchedEffect(state.position) {
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
            Slider(
                value = animatedValue,
                onValueChange = { value ->
                    isSeeking = true
                    seekbarValue = value
                }, onValueChangeFinished = {
                    onEvent(SongEvent.Seek(seekbarValue))
                    isSeeking = false
                }, colors = SliderDefaults.colors(
                    thumbColor = primaryColor,
                    activeTrackColor = primaryColor,
                    inactiveTrackColor = accentColor,
                ), modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
            )
            // Duration
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(
                    text = millisecondsToMinutes(state.position),
                    color = accentColor,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = millisecondsToMinutes(song.duration),
                    color = accentColor,
                    fontSize = 12.sp
                )
            }
            // Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(
                    modifier = Modifier.size(64.dp),
                    onClick = { onEvent(SongEvent.Previous) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.fast_rewind_24px),
                        contentDescription = "Previous",
                        tint = primaryColor,
                        modifier = Modifier.mirror(),
                    )
                }
                IconButton(modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterVertically),
                    onClick = { onEvent(SongEvent.PlayPause) }) {
                    Icon(
                        painter = rememberAnimatedVectorPainter(
                            animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.play_pause_animation),
                            atEnd = state.isPlaying
                        ),
                        contentDescription = "Play/Pause",
                        tint = primaryColor,
                        modifier = Modifier
                            .size(80.dp)
                            .mirror()
                    )
                }
                IconButton(modifier = Modifier.size(64.dp), onClick = { onEvent(SongEvent.Next) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.fast_forward_24px),
                        contentDescription = "Skip",
                        tint = primaryColor,
                        modifier = Modifier.mirror(),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(modifier = Modifier
                    .padding(8.dp)
                    .size(32.dp)
                    .align(Alignment.CenterVertically),
                    onClick = {
                        onEvent(SongEvent.Favorite(song))
                    }) {
                    Icon(
                        painter = rememberAnimatedVectorPainter(
                            animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.favorite_animation),
                            atEnd = state.playlists.firstOrNull()?.songs?.any { it.id == song.id }
                                ?: false
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
                    .size(32.dp), onClick = { onEvent(SongEvent.ShowSongBottomSheet(song)) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_more_horiz_24),
                        contentDescription = "More",
                        tint = Color.White,
                        modifier = Modifier.mirror(),
                    )
                }
            }
        }
        // Static controls
        Row(
            modifier = Modifier
                .background(sheetContainerColor)
                .padding(24.dp)
                .align(Alignment.TopEnd)
                .graphicsLayer(alpha = animatedAlpha)
        ) {
            IconButton(modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterVertically),
                onClick = { onEvent(SongEvent.PlayPause) }) {
                Icon(
                    painter = rememberAnimatedVectorPainter(
                        animatedImageVector = AnimatedImageVector.animatedVectorResource(R.drawable.play_pause_animation),
                        atEnd = state.isPlaying
                    ),
                    contentDescription = "Play/Pause",
                    tint = primaryColor,
                    modifier = Modifier
                        .size(48.dp)
                        .mirror()
                )
            }
            IconButton(modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterVertically),
                onClick = { onEvent(SongEvent.Next) }) {
                Icon(
                    modifier = Modifier.mirror(),
                    painter = painterResource(id = R.drawable.fast_forward_24px),
                    contentDescription = "Skip",
                    tint = primaryColor
                )
            }
        }
    }
}