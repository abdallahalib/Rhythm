package dev.abdallah.rhythm.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.abdallah.rhythm.ui.screen.playlists.Playlists
import dev.abdallah.rhythm.ui.theme.Background
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongState


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddToPlaylistBottomSheet(
    state: SongState,
    onEvent: (SongEvent) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent(SongEvent.HideAddToPlaylistBottomSheet) },
        containerColor = Background,
        dragHandle = { },
    ) {
        Column {
            Text(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Add to Playlist",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
            )
            Playlists(state = state, onEvent = onEvent)
        }
    }
}