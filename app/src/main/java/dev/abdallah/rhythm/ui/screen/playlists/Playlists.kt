package dev.abdallah.rhythm.ui.screen.playlists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.Screen
import dev.abdallah.rhythm.ui.theme.Blue
import dev.abdallah.rhythm.ui.theme.Gray
import dev.abdallah.rhythm.ui.theme.Surface
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongFilter
import dev.abdallah.rhythm.ui.viewmodel.SongState

@Composable
fun Playlists(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        PlaylistList(
            state = state,
            onEvent = onEvent
        )
    }
    if (state.showNewPlaylistDialog) {
        NewPlaylistDialog(
            onEvent = onEvent
        )
    }
}

@Composable
fun NewPlaylistDialog(onEvent: (SongEvent) -> Unit) {
    var playlistName by remember {
        mutableStateOf("")
    }
    AlertDialog(
        containerColor = Surface,
        onDismissRequest = { onEvent(SongEvent.HideNewPlaylistDialog) },
        confirmButton = {
            TextButton(
                onClick = { onEvent(SongEvent.NewPlaylist(playlistName)) }) {
                Text(
                    "Create",
                    color = Blue
                )
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(SongEvent.HideNewPlaylistDialog) }) {
                Text(
                    "Cancel",
                    color = Blue
                )
            }
        },
        title = {
            Text(
                text = "New playlist",
                fontSize = 14.sp,
                color = Color.White,
                fontWeight = FontWeight.W600,
            )
        },
        text = {
            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text("Playlist name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Blue,
                    focusedLabelColor = Blue,
                    cursorColor = Blue,
                    focusedTextColor = Color.White,
                    selectionColors = TextSelectionColors(
                        handleColor = Blue,
                        backgroundColor = Gray
                    )
                )
            )
        }
    )
}

@Composable
fun PlaylistList(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 24.dp,
                        bottom = 8.dp
                    )
                    .background(Surface, RoundedCornerShape(corner = CornerSize(24.dp)))
                    .clickable {
                        onEvent(SongEvent.ShowNewPlaylistDialog)
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Add new",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W500,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Icon(
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(24.dp),
                    painter = painterResource(
                        id = R.drawable.round_add_24
                    ),
                    contentDescription = "",
                    tint = Color.White,
                )
            }
        }
        items(state.playlists.size) {
            Playlist(
                state = state,
                onEvent = onEvent,
                position = it
            )
        }
    }
}
