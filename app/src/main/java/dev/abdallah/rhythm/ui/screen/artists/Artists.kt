package dev.abdallah.rhythm.ui.screen.artists

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.abdallah.rhythm.ui.screen.NoSongFound
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongState

@Composable
fun Artists(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    if (state.artists.isEmpty()) {
        NoSongFound()
    } else {
        ArtistList(
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
fun ArtistList(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.artists.size) {
            Artist(
                state = state,
                onEvent = onEvent,
                position = it,
            )
        }
    }
}
