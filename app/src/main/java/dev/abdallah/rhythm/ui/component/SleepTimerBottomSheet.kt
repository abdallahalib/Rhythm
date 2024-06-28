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
import dev.abdallah.rhythm.player.service.PlayerEvent
import dev.abdallah.rhythm.ui.theme.Background
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.util.minutesToMilliseconds
import kotlin.time.Duration

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SleepTimerBottomSheet(
    onEvent: (SongEvent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = { onEvent(SongEvent.HideSleepTimerBottomSheet) },
        containerColor = Background,
        sheetState = sheetState,
        dragHandle = { },
    ) {
        Column {
            Text(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.CenterHorizontally),
                text = "Sleep timer",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
            )
            BottomSheetOption(
                text = "5 minutes",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Sleep(minutesToMilliseconds(5)))
                }
            )
            BottomSheetOption(
                text = "10 minutes",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Sleep(minutesToMilliseconds(10)))
                }
            )
            BottomSheetOption(
                text = "15 minutes",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Sleep(minutesToMilliseconds(15)))
                }
            )
            BottomSheetOption(
                text = "30 minutes",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Sleep(minutesToMilliseconds(30)))
                }
            )
            BottomSheetOption(
                text = "45 minutes",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Sleep(minutesToMilliseconds(45)))
                }
            )
            BottomSheetOption(
                text = "1 hour",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.Sleep(minutesToMilliseconds(60)))
                }
            )
            BottomSheetOption(
                text = "End of track",
                textColor = Color.Gray,
                onClick = {
                    onEvent(SongEvent.StopAtEnd)
                }
            )
        }
    }
}