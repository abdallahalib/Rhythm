package dev.abdallah.rhythm.ui.screen.folders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.Screen
import dev.abdallah.rhythm.ui.theme.Surface
import dev.abdallah.rhythm.ui.viewmodel.SongEvent
import dev.abdallah.rhythm.ui.viewmodel.SongFilter
import dev.abdallah.rhythm.ui.viewmodel.SongState

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Folder(
    state: SongState,
    onEvent: (SongEvent) -> Unit,
    position: Int,
) {
    val topPadding = if (position == 0) 24.dp else 8.dp
    val bottomPadding = if (position == state.folders.size - 1) 112.dp else 8.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 16.dp,
                top = topPadding,
                bottom = bottomPadding
            )
            .background(Surface, RoundedCornerShape(corner = CornerSize(24.dp)))
            .clickable {
                onEvent(SongEvent.Filter(filter = SongFilter.Folder(state.folders[position])))
                onEvent(SongEvent.Navigate(screen = Screen.FOLDER))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = R.drawable.folder,
            contentScale = ContentScale.Crop,
            contentDescription = "Folder Icon",
            modifier = Modifier
                .padding(16.dp)
                .size(64.dp)
        )
        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            text = state.folders[position].name,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.W500,
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier
                .padding(end = 16.dp)
                .size(24.dp)
                .background(Color.Gray, CircleShape)
                .align(Alignment.CenterVertically),
            text = "${state.folders[position].songs.size}",
            color = Color.LightGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
            textAlign = TextAlign.Center
        )
    }
}