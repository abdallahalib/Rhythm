package dev.abdallah.rhythm.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.data.db.Playlist
import dev.abdallah.rhythm.ui.theme.Blue
import dev.abdallah.rhythm.ui.theme.Gray

@Composable
fun Playlists(playlists: List<Playlist>, onItemClick: (Int) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        PlaylistsList(playlists = playlists, onItemClick)
        AddPlaylistButton(Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
fun AddPlaylistButton(modifier: Modifier) {
    FloatingActionButton(
        modifier = modifier.padding(end = 16.dp, bottom = 120.dp),
        containerColor = Gray,
        contentColor = Blue,
        onClick = {  },
    ) {
        Icon(
            painter = painterResource(id = R.drawable.round_add_24),
            contentDescription = "Shuffle",
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
fun PlaylistsList(playlists: List<Playlist>, onItemClick: (Int) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(playlists.size) {
            Playlist(position = it, playlists = playlists) { position ->
                onItemClick(position)
            }
        }
    }
}
