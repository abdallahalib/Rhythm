package dev.abdallah.rhythm.ui.screen.albums

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.abdallah.rhythm.ui.screen.NoSongFound
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongState

@Composable
fun Albums(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    if (state.albums.isEmpty()) {
        NoSongFound()
    } else {
        AlbumList(
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
fun AlbumList(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.albums.size) {
            Album(
                state = state,
                onEvent = onEvent,
                position = it,
            )
        }
    }
}
