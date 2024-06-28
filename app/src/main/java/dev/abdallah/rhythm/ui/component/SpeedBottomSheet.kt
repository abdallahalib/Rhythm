package dev.abdallah.rhythm.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.abdallah.rhythm.ui.theme.Background
import dev.abdallah.rhythm.ui.viewmodel.SongEvent

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SpeedBottomSheet(
    onEvent: (SongEvent) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { onEvent(SongEvent.HideSpeedBottomSheet) },
        containerColor = Background,
        dragHandle = { },
    ) {
        Column {
            Text(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Speed",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
            )
            BottomSheetOption(
                text = "0.5x",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Speed(0.5f))
                }
            )
            BottomSheetOption(
                text = "0.75x",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Speed(0.75f))
                }
            )
            BottomSheetOption(
                text = "1x (Default)",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Speed(1f))
                }
            )
            BottomSheetOption(
                text = "1.25x",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Speed(1.25f))
                }
            )
            BottomSheetOption(
                text = "1.5x",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Speed(1.5f))
                }
            )
            BottomSheetOption(
                text = "2.0x",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Speed(2.0f))
                }
            )
        }
    }
}