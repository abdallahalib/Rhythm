package dev.abdallah.rhythm.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.ui.theme.Blue
import dev.abdallah.rhythm.ui.theme.Gray
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongState

@Composable
fun Songs(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    if (state.songs.isEmpty()) {
        NoSongFound()
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            SongList(
                state = state,
                onEvent = onEvent,
            )
            FloatingActionButton(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 120.dp)
                    .align(Alignment.BottomEnd),
                containerColor = Gray,
                contentColor = Blue,
                onClick = { onEvent(SongEvent.Shuffle(state.songs)) },
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.shuffle_24px),
                    contentDescription = "Shuffle",
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
fun NoSongFound() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = R.drawable.round_audiotrack_24),
            contentDescription = "Songs",
            colorFilter = ColorFilter.tint(Gray)
        )
        Text(modifier = Modifier.padding(top = 16.dp), text = "No songs found!", color = Gray)
    }
}

@Composable
fun SongList(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.songs.size, key = { state.songs[it].id }) {
            Song(
                state = state,
                songs = state.songs,
                onEvent = onEvent,
                position = it
            )
        }
    }
}