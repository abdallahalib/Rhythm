package dev.abdallah.rhythm.ui.screen.folders

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.abdallah.rhythm.ui.screen.NoSongFound
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongState

@Composable
fun Folders(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    if (state.folders.isEmpty()) {
        NoSongFound()
    } else {
        FolderList(state, onEvent)
    }
}

@Composable
fun FolderList(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(state.folders.size) {
            Folder(
                state = state,
                onEvent = onEvent,
                position = it,
            )
        }
    }
}
