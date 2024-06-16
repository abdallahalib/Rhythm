package dev.abdallah.rhythm.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.abdallah.rhythm.data.local.model.Folder

@Composable
fun Folders(folderList: List<Folder>, onItemClick: (Int) -> Unit) {
    if (folderList.isEmpty()) {
        NoSongsFound()
    } else {
        FoldersList(folderList = folderList, onItemClick)
    }
}

@Composable
fun FoldersList(folderList: List<Folder>, onItemClick: (Int) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(folderList.size) {
            Folder(position = it, folderList = folderList) { position ->
                onItemClick(position)
            }
        }
    }
}
