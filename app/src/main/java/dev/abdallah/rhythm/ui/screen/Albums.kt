package dev.abdallah.rhythm.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.abdallah.rhythm.data.local.model.Album
import dev.abdallah.rhythm.data.local.model.Artist
import dev.abdallah.rhythm.data.local.model.Folder

@Composable
fun Albums(albumsList: List<Album>, onItemClick: (Int) -> Unit) {
    if (albumsList.isEmpty()) {
        NoSongsFound()
    } else {
        AlbumsList(albumsList = albumsList, onItemClick)
    }
}

@Composable
fun AlbumsList(albumsList: List<Album>, onItemClick: (Int) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(albumsList.size) {
            Album(position = it, albumsList = albumsList) { position ->
                onItemClick(position)
            }
        }
    }
}
