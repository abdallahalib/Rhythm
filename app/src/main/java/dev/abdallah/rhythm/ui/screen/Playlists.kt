package dev.abdallah.rhythm.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.data.db.Playlist
import dev.abdallah.rhythm.ui.theme.Blue
import dev.abdallah.rhythm.ui.theme.Gray
import dev.abdallah.rhythm.ui.theme.Surface

@Composable
fun Playlists(playlists: List<Playlist>, onItemClick: (Int) -> Unit, onNewPlaylist: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        var showDialog by remember {
            mutableStateOf(false)
        }
        PlaylistsList(playlists = playlists, onItemClick)
        AddPlaylistButton(Modifier.align(Alignment.BottomEnd), onClick = {
            showDialog = true
        })
        if (showDialog) {
            NewPlaylistDialog(
                onConfirm = { playlistName ->
                    showDialog = false
                    onNewPlaylist(playlistName)
                },
                onDismiss = { showDialog = false }
            )
        }
    }
}

@Composable
fun AddPlaylistButton(modifier: Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        modifier = modifier.padding(end = 16.dp, bottom = 120.dp),
        containerColor = Gray,
        contentColor = Blue,
        onClick = { onClick() },
    ) {
        Icon(
            painter = painterResource(id = R.drawable.round_add_24),
            contentDescription = "Shuffle",
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
fun NewPlaylistDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var playlistName by remember {
        mutableStateOf("")
    }
    AlertDialog(
        containerColor = Surface,
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(playlistName) }) { Text("Create", color = Blue) } },
        dismissButton = { TextButton(onClick = { onDismiss() }) { Text("Cancel", color = Blue) } },
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
                    selectionColors = TextSelectionColors(handleColor = Blue, backgroundColor = Gray)
                )
            )
        }
    )
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
