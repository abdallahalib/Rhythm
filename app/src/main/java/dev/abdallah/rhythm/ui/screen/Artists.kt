package dev.abdallah.rhythm.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.abdallah.rhythm.data.local.model.Artist
import dev.abdallah.rhythm.data.local.model.Folder

@Composable
fun Artists(artistList: List<Artist>, onItemClick: (Int) -> Unit) {
    if (artistList.isEmpty()) {
        NoSongsFound()
    } else {
        ArtistsList(artistList = artistList, onItemClick)
    }
}

@Composable
fun ArtistsList(artistList: List<Artist>, onItemClick: (Int) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(artistList.size) {
            Artist(position = it, artistList = artistList) { position ->
                onItemClick(position)
            }
        }
    }
}
