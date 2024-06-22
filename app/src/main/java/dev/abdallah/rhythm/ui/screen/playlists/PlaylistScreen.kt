package dev.abdallah.rhythm.ui.screen.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.Screen
import dev.abdallah.rhythm.ui.component.BottomSheetOption
import dev.abdallah.rhythm.ui.screen.Song
import dev.abdallah.rhythm.ui.theme.Background
import dev.abdallah.rhythm.ui.theme.Blue
import dev.abdallah.rhythm.ui.theme.Gray
import dev.abdallah.rhythm.ui.theme.SemiTransparent
import dev.abdallah.rhythm.ui.theme.Surface
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongFilter
import dev.abdallah.rhythm.ui.viewmodel.SongState

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlaylistScreen(
    state: SongState,
    onEvent: (SongEvent) -> Unit
) {
    if (state.showPlaylistBottomSheet) {
        PlaylistBottomSheet(
            onEvent = onEvent
        )
    }
    val playlist = state.filter as SongFilter.Playlist
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        item {
            val brush = Brush.verticalGradient(listOf(Gray, Background))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 56.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .background(SemiTransparent, CircleShape)
                            .padding(12.dp)
                            .size(24.dp),
                        onClick = { onEvent(SongEvent.Navigate(screen = Screen.HOME)) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier
                            .background(SemiTransparent, CircleShape)
                            .padding(12.dp)
                            .size(24.dp),
                        onClick = { onEvent(SongEvent.ShowPlaylistBottomSheet(playlist.playlist)) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_more_vert_24),
                            contentDescription = "More options",
                            tint = Color.White,
                        )
                    }
                }
                GlideImage(
                    model = R.drawable.round_audiotrack_24,
                    contentDescription = "Playlist artwork",
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(192.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = playlist.playlist.name,
                    modifier = Modifier
                        .padding(top = 36.dp)
                        .align(Alignment.CenterHorizontally),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.W600
                )
                Text(
                    text = "${playlist.playlist.songs.size} Songs",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    fontSize = 14.sp,
                    color = Color.Gray,

                    )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp, end = 8.dp),
                        onClick = { onEvent(SongEvent.Change(playlist.playlist.songs, 0)) },
                        colors = ButtonColors(
                            containerColor = Gray,
                            contentColor = Blue,
                            disabledContentColor = Blue,
                            disabledContainerColor = Gray
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.play_arrow_24px),
                            contentDescription = "Play"
                        )
                        Text(
                            text = "Play",
                            color = Blue,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 16.dp),
                        onClick = {
                            onEvent(SongEvent.Shuffle(playlist.playlist.songs))
                        },
                        colors = ButtonColors(
                            containerColor = Gray,
                            contentColor = Blue,
                            disabledContentColor = Blue,
                            disabledContainerColor = Gray
                        ),
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.shuffle_24px),
                            contentDescription = "Shuffle"
                        )
                        Text(
                            text = "Shuffle",
                            color = Blue,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
        items(playlist.playlist.songs.size, key = { playlist.playlist.songs[it].id }) {
            Song(
                state = state,
                songs = playlist.playlist.songs,
                position = it,
                onEvent = { event -> onEvent(event) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistBottomSheet(onEvent: (SongEvent) -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { onEvent(SongEvent.HidePlaylistBottomSheet) },
        containerColor = Surface,
        sheetState = sheetState,
        dragHandle = { },
    ) {
        Column(modifier = Modifier.padding(vertical = 32.dp)) {
            BottomSheetOption(text = "Delete Playlist", icon = R.drawable.round_delete_forever_24) {
                onEvent(SongEvent.DeletePlaylist)
            }
            BottomSheetOption(text = "Share", icon = R.drawable.ios_share_24px) {

            }
        }
    }
}
