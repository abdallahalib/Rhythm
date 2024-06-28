package dev.abdallah.rhythm.ui.component

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.ui.theme.Surface
import dev.abdallah.rhythm.ui.viewmodel.SongEvent


@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun SongBottomSheet(
    song: Song,
    onEvent: (SongEvent) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()
    val defaultPrimaryColor = Color.White.toArgb()
    val defaultAccentColor = Color.Gray.toArgb()
    val defaultSheetContainerColor = Surface.toArgb()
    val artworkPalette = if (song.artworkLarge.isNotBlank()) {
        Palette.from(BitmapFactory.decodeFile(song.artworkLarge)).generate()
    } else {
        null
    }
    val darkMutedSwatch = artworkPalette?.darkMutedSwatch
    val primaryColor =
        darkMutedSwatch?.titleTextColor ?: defaultPrimaryColor
    val accentColor = darkMutedSwatch?.bodyTextColor ?: defaultAccentColor
    val sheetContainerColor = darkMutedSwatch?.rgb ?: defaultSheetContainerColor
    ModalBottomSheet(
        onDismissRequest = { onEvent(SongEvent.HideSongBottomSheet) },
        containerColor = Color(sheetContainerColor),
        sheetState = sheetState,
        dragHandle = { },
    ) {
        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 12.dp,
                        horizontal = 24.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                GlideImage(
                    model = song.artworkSmall,
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp)),
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = song.title,
                        color = Color(primaryColor),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = song.artist,
                        color = Color(accentColor),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.round_edit_24),
                    contentDescription = "End",
                    tint = Color(accentColor),
                    modifier = Modifier.size(24.dp)
                )
            }
            BottomSheetOption(
                text = "Add to Playlist",
                icon = R.drawable.playlist_add_24px,
                textColor = Color(primaryColor),
                iconColor = Color(accentColor),
                onClick = {
                    onEvent(SongEvent.ShowAddToPlaylistBottomSheet)

                }
            )
            BottomSheetOption(
                text = "Share",
                icon = R.drawable.ios_share_24px,
                textColor = Color(primaryColor),
                iconColor = Color(accentColor),
                onClick = {

                }
            )
            BottomSheetOption(
                text = "Speed",
                icon = R.drawable.round_speed_24,
                textColor = Color(primaryColor),
                iconColor = Color(accentColor),
                onClick = {
                    onEvent(SongEvent.ShowSpeedBottomSheet)
                }
            )
            BottomSheetOption(
                text = "Make as ringtone",
                icon = R.drawable.round_notifications_24,
                textColor = Color(primaryColor),
                iconColor = Color(accentColor),
                onClick = {

                }
            )
            BottomSheetOption(
                text = "Sleep timer",
                icon = R.drawable.round_timer_24,
                textColor = Color(primaryColor),
                iconColor = Color(accentColor),
                onClick = {
                    onEvent(SongEvent.ShowSleepTimerBottomSheet)
                }
            )
        }
    }
}